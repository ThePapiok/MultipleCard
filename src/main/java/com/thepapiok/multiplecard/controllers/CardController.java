package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.services.CardService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CardController {
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String CODE_SMS_ORDER_PARAM = "codeSmsOrder";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String ORDER_PARAM = "order";
  private final PasswordEncoder passwordEncoder;
  private final MessageSource messageSource;
  private final CardService cardService;

  @Autowired
  public CardController(
      PasswordEncoder passwordEncoder, MessageSource messageSource, CardService cardService) {
    this.passwordEncoder = passwordEncoder;
    this.messageSource = messageSource;
    this.cardService = cardService;
  }

  @GetMapping("/new_card")
  public String newCardPage(
      @RequestParam(required = false) String reset,
      @RequestParam(required = false) String error,
      Model model,
      Principal principal,
      HttpSession httpSession) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      } else {
        resetSession(httpSession);
      }
    } else {
      resetSession(httpSession);
    }
    if (reset != null) {
      resetSession(httpSession);
      return "redirect:/user";
    }
    model.addAttribute("phone", principal.getName());
    OrderCardDTO order = (OrderCardDTO) httpSession.getAttribute(ORDER_PARAM);
    if (order == null) {
      order = new OrderCardDTO();
    } else {
      httpSession.removeAttribute(ORDER_PARAM);
    }
    model.addAttribute("card", order);
    return "newCardPage";
  }

  @PostMapping("/new_card")
  public String newCard(
      @Valid @ModelAttribute OrderCardDTO order,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale,
      Principal principal) {
    Integer amount = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    final int maxAmount = 3;
    final String redirectUserError = "redirect:/user?error";
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (amount == maxAmount) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.to_many_attempts", null, locale));
      resetSession(httpSession);
      return redirectUserError;
    }
    if (bindingResult.hasErrors()) {
      return redirectErrorPage(httpSession, amount, "validation.incorrect_data", locale, order);
    }
    if (!passwordEncoder.matches(
        order.getCode(), (String) httpSession.getAttribute(CODE_SMS_ORDER_PARAM))) {
      return redirectErrorPage(httpSession, amount, "error.bad_sms_code", locale, order);
    }
    if (!order.getPin().equals(order.getRetypedPin())) {
      return redirectErrorPage(
          httpSession, amount, "newCardPage.error.not_the_same_pin", locale, order);
    }
    if (!cardService.createCard(order, principal.getName())) {
      resetSession(httpSession);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return redirectUserError;
    }
    resetSession(httpSession);
    httpSession.setAttribute(
        "successMessage",
        messageSource.getMessage("newCardPage.success.create_new_card", null, locale));
    return "redirect:/user?success";
  }

  private String redirectErrorPage(
      HttpSession httpSession, int amount, String message, Locale locale, OrderCardDTO order) {
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, messageSource.getMessage(message, null, locale));
    httpSession.setAttribute(ATTEMPTS_PARAM, amount + 1);
    order.setCode("");
    httpSession.setAttribute(ORDER_PARAM, order);
    return "redirect:/new_card?error";
  }

  private void resetSession(HttpSession httpSession) {
    httpSession.removeAttribute(CODE_SMS_ORDER_PARAM);
    httpSession.removeAttribute(ATTEMPTS_PARAM);
    httpSession.removeAttribute("codeAmountSms");
    httpSession.removeAttribute(ORDER_PARAM);
  }
}
