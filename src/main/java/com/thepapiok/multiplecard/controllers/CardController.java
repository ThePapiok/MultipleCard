package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.services.CardService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import java.util.regex.Pattern;
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
  private static final String PHONE_PARAM = "phone";
  private static final String CODE_SMS_ORDER_PARAM = "codeSmsOrder";
  private static final String CODE_SMS_BLOCK_PARAM = "codeSmsBlock";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String ERROR_UNEXPECTED_MESSAGE = "error.unexpected";
  private static final String ERROR_BAD_SMS_CODE_MESSAGE = "error.bad_sms_code";
  private static final String ERROR_TOO_MANY_ATTEMPTS_MESSAGE = "error.to_many_attempts";
  private static final String ERROR_VALIDATION_MESSAGE = "validation.incorrect_data";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String REDIRECT_USER_SUCCESS = "redirect:/user?success";
  private static final String REDIRECT_USER_ERROR = "redirect:/user?error";
  private static final String REDIRECT_USER = "redirect:/user";
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
      }
    } else {
      resetSession(httpSession, CODE_SMS_ORDER_PARAM);
    }
    if (reset != null) {
      resetSession(httpSession, CODE_SMS_ORDER_PARAM);
      return REDIRECT_USER;
    }
    model.addAttribute(PHONE_PARAM, principal.getName());
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
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (amount == maxAmount) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, null, locale));
      resetSession(httpSession, CODE_SMS_ORDER_PARAM);
      return REDIRECT_USER_ERROR;
    }
    if (bindingResult.hasErrors()) {
      return redirectErrorPage(httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, order);
    }
    if (!passwordEncoder.matches(
        order.getCode(), (String) httpSession.getAttribute(CODE_SMS_ORDER_PARAM))) {
      return redirectErrorPage(httpSession, amount, ERROR_BAD_SMS_CODE_MESSAGE, locale, order);
    }
    if (!order.getPin().equals(order.getRetypedPin())) {
      return redirectErrorPage(
          httpSession, amount, "newCardPage.error.not_the_same_pin", locale, order);
    }
    if (!cardService.createCard(order, principal.getName())) {
      resetSession(httpSession, CODE_SMS_ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale));
      return REDIRECT_USER_ERROR;
    }
    resetSession(httpSession, CODE_SMS_ORDER_PARAM);
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("newCardPage.success.create_new_card", null, locale));
    return REDIRECT_USER_SUCCESS;
  }

  private String redirectErrorPage(
      HttpSession httpSession, int amount, String message, Locale locale, OrderCardDTO order) {
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, messageSource.getMessage(message, null, locale));
    httpSession.setAttribute(ATTEMPTS_PARAM, amount + 1);
    if (order != null) {
      order.setCode("");
      httpSession.setAttribute(ORDER_PARAM, order);
      return "redirect:/new_card?error";
    } else {
      return "redirect:/block_card?error";
    }
  }

  private void resetSession(HttpSession httpSession, String param) {
    httpSession.removeAttribute(param);
    httpSession.removeAttribute(ATTEMPTS_PARAM);
    httpSession.removeAttribute("codeAmountSms");
    httpSession.removeAttribute(ORDER_PARAM);
  }

  @GetMapping("/block_card")
  public String blockCardPage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String reset,
      Principal principal,
      Model model,
      HttpSession httpSession) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    } else {
      resetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    }
    if (reset != null) {
      resetSession(httpSession, CODE_SMS_BLOCK_PARAM);
      return REDIRECT_USER;
    }
    model.addAttribute(PHONE_PARAM, principal.getName());
    return "blockCardPage";
  }

  @PostMapping("/block_card")
  public String blockCard(
      @RequestParam String verificationNumberSms,
      HttpSession httpSession,
      Locale locale,
      Principal principal) {
    Integer amount = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    Pattern pattern = Pattern.compile("^[0-9]{3} [0-9]{3}$");
    String phone = principal.getName();
    final int maxAmount = 3;
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (amount == maxAmount) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, null, locale));
      resetSession(httpSession, CODE_SMS_BLOCK_PARAM);
      return REDIRECT_USER_ERROR;
    }
    if (!pattern.matcher(verificationNumberSms).matches()) {
      return redirectErrorPage(httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, null);
    }
    if (!passwordEncoder.matches(
        verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_BLOCK_PARAM))) {
      return redirectErrorPage(httpSession, amount, ERROR_BAD_SMS_CODE_MESSAGE, locale, null);
    }
    if (!cardService.isBlocked(phone)) {
      resetSession(httpSession, CODE_SMS_BLOCK_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage("blockCardPage.error.block_card.isBlocked", null, locale));
      return REDIRECT_USER_ERROR;
    }
    if (!cardService.blockCard(phone)) {
      resetSession(httpSession, CODE_SMS_BLOCK_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale));
      return REDIRECT_USER_ERROR;
    }
    resetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("blockCardPage.success.block_card", null, locale));
    return REDIRECT_USER_SUCCESS;
  }
}
