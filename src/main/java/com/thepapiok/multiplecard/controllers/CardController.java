package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ResultService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
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
  private static final String CODE_SMS_ORDER_PARAM = "codeSmsOrder";
  private static final String CODE_SMS_BLOCK_PARAM = "codeSmsBlock";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String REDIRECT_USER_ERROR = "redirect:/user?error";
  private static final String ORDER_PARAM = "order";
  private final PasswordEncoder passwordEncoder;
  private final MessageSource messageSource;
  private final CardService cardService;
  private final AuthenticationController authenticationController;
  private final ProductService productService;
  private final ResultService resultService;

  @Autowired
  public CardController(
      PasswordEncoder passwordEncoder,
      MessageSource messageSource,
      CardService cardService,
      AuthenticationController authenticationController,
      ProductService productService,
      ResultService resultService) {
    this.passwordEncoder = passwordEncoder;
    this.messageSource = messageSource;
    this.cardService = cardService;
    this.authenticationController = authenticationController;
    this.productService = productService;
    this.resultService = resultService;
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
    } else if (reset != null) {
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
      return "redirect:/user";
    } else {
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
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
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (amount == maxAmount) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.to_many_attempts", null, locale));
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
      return REDIRECT_USER_ERROR;
    } else if (bindingResult.hasErrors()) {
      return redirectErrorPage(httpSession, amount, "validation.incorrect_data", locale, order);
    } else if (!passwordEncoder.matches(
        order.getCode(), (String) httpSession.getAttribute(CODE_SMS_ORDER_PARAM))) {
      return redirectErrorPage(httpSession, amount, "error.bad_sms_code", locale, order);
    } else if (!order.getPin().equals(order.getRetypedPin())) {
      return redirectErrorPage(
          httpSession, amount, "newCardPage.error.not_the_same_pin", locale, order);
    } else if (!cardService.createCard(order, principal.getName())) {
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return REDIRECT_USER_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
    httpSession.setAttribute(
        "successMessage",
        messageSource.getMessage("newCardPage.success.create_new_card", null, locale));
    return "redirect:/user?success";
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
    } else if (reset != null) {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      return "redirect:/user";
    } else {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
    }
    model.addAttribute("phone", principal.getName());
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
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.to_many_attempts", null, locale));
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      return REDIRECT_USER_ERROR;
    } else if (!pattern.matcher(verificationNumberSms).matches()) {
      return redirectErrorPage(httpSession, amount, "validation.incorrect_data", locale, null);
    } else if (!passwordEncoder.matches(
        verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_BLOCK_PARAM))) {
      return redirectErrorPage(httpSession, amount, "error.bad_sms_code", locale, null);
    } else if (!cardService.isBlocked(phone)) {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage("blockCardPage.error.block_card.isBlocked", null, locale));
      return REDIRECT_USER_ERROR;
    } else if (!cardService.blockCard(phone)) {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return REDIRECT_USER_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
    httpSession.setAttribute(
        "successMessage",
        messageSource.getMessage("blockCardPage.success.block_card", null, locale));
    return "redirect:/user?success";
  }

  @GetMapping("/cards")
  public String buyProductsPage(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "count") String field,
      @RequestParam(defaultValue = "true") Boolean isDescending,
      @RequestParam(defaultValue = "") String text,
      @RequestParam(defaultValue = "") String category,
      @RequestParam(defaultValue = "") String shopName,
      @RequestParam String id,
      Model model) {
    int maxPage;
    List<ProductWithShopDTO> products =
        productService.getProductsWithShops(page, field, isDescending, text, category, shopName);
    maxPage = productService.getMaxPage(text, null, category, shopName);
    model.addAttribute("field", field);
    model.addAttribute("isDescending", isDescending);
    model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
    model.addAttribute("pageSelected", page + 1);
    model.addAttribute("products", products);
    model.addAttribute("productsEmpty", products.size() == 0);
    model.addAttribute("maxPage", maxPage);
    model.addAttribute("id", id);
    return "buyProductsPage";
  }

  @GetMapping("/cart")
  public String cartPage(@RequestParam(defaultValue = "0") Integer page, Model model) {
    model.addAttribute("pageSelected", page);
    return "cartPage";
  }
}
