package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductGetDTO;
import com.thepapiok.multiplecard.dto.PromotionGetDTO;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ResultService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
  private static final String PAGE_SELECTED_PARAM = "pageSelected";
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
      return REDIRECT_USER;
    } else {
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
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
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
      return REDIRECT_USER_ERROR;
    } else if (bindingResult.hasErrors()) {
      return redirectErrorPage(httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, order);
    } else if (!passwordEncoder.matches(
        order.getCode(), (String) httpSession.getAttribute(CODE_SMS_ORDER_PARAM))) {
      return redirectErrorPage(httpSession, amount, ERROR_BAD_SMS_CODE_MESSAGE, locale, order);
    } else if (!order.getPin().equals(order.getRetypedPin())) {
      return redirectErrorPage(
          httpSession, amount, "newCardPage.error.not_the_same_pin", locale, order);
    } else if (!cardService.createCard(order, principal.getName())) {
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale));
      return REDIRECT_USER_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
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
      return REDIRECT_USER;
    } else {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
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
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      return REDIRECT_USER_ERROR;
    } else if (!pattern.matcher(verificationNumberSms).matches()) {
      return redirectErrorPage(httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, null);
    } else if (!passwordEncoder.matches(
        verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_BLOCK_PARAM))) {
      return redirectErrorPage(httpSession, amount, ERROR_BAD_SMS_CODE_MESSAGE, locale, null);
    } else if (!cardService.isBlocked(phone)) {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage("blockCardPage.error.block_card.isBlocked", null, locale));
      return REDIRECT_USER_ERROR;
    } else if (!cardService.blockCard(phone)) {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale));
      return REDIRECT_USER_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("blockCardPage.success.block_card", null, locale));
    return REDIRECT_USER_SUCCESS;
  }

  @GetMapping("/cards")
  public String buyProductsPage(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "count") String field,
      @RequestParam(defaultValue = "true") Boolean isDescending,
      @RequestParam(defaultValue = "") String text,
      @RequestParam String id,
      Model model) {
    int maxPage;
    List<ProductGetDTO> products =
        productService.getProducts(null, page, field, isDescending, text);
    List<Promotion> promotions =
        products.stream().map(ProductGetDTO::getPromotion).filter(Objects::nonNull).toList();
    List<PromotionGetDTO> promotionGetDTOS = null;
    if (promotions.size() != 0) {
      promotionGetDTOS =
          promotions.stream()
              .map(
                  e ->
                      new PromotionGetDTO(
                          e.getProductId().toString(),
                          e.getStartAt(),
                          e.getExpiredAt(),
                          e.getAmount(),
                          e.getCount()))
              .toList();
    }
    maxPage = productService.getMaxPage(text, null);
    model.addAttribute("field", field);
    model.addAttribute("isDescending", isDescending);
    model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
    model.addAttribute(PAGE_SELECTED_PARAM, page + 1);
    model.addAttribute(
        "products", products.stream().map(e -> new ProductDTO(true, e.getProduct())).toList());
    model.addAttribute("promotions", promotionGetDTOS);
    model.addAttribute("productsSize", products.size());
    model.addAttribute("maxPage", maxPage);
    model.addAttribute("id", id);
    return "buyProductsPage";
  }

  @GetMapping("/cart")
  public String cartPage(@RequestParam(defaultValue = "0") Integer page, Model model) {
    model.addAttribute(PAGE_SELECTED_PARAM, page);
    return "cartPage";
  }
}
