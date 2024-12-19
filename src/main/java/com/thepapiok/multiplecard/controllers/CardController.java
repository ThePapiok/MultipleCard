package com.thepapiok.multiplecard.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.dto.PageOwnerProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsWithShopDTO;
import com.thepapiok.multiplecard.dto.ProductAtCardDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.dto.SearchCardDTO;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.EmailService;
import com.thepapiok.multiplecard.services.PayUService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ProfileService;
import com.thepapiok.multiplecard.services.RefundService;
import com.thepapiok.multiplecard.services.ResultService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CardController {
  private static final String ERROR_VALIDATION_MESSAGE = "validation.incorrect_data";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String CODE_SMS_ORDER_PARAM = "codeSmsOrder";
  private static final String CODE_SMS_BLOCK_PARAM = "codeSmsBlock";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String REDIRECT_PROFILE_ERROR = "redirect:/profile?error";
  private static final String ORDER_PARAM = "order";
  private static final String CARD_ID_PARAM = "cardId";
  private static final String STEP_PARAM = "step";
  private static final String PRODUCTS_PARAM = "products";
  private static final String SUCCESS_MESSAGE = "successMessage";
  private final PasswordEncoder passwordEncoder;
  private final MessageSource messageSource;
  private final CardService cardService;
  private final AuthenticationController authenticationController;
  private final ProductService productService;
  private final ResultService resultService;
  private final PayUService payUService;
  private final RefundService refundService;
  private final EmailService emailService;
  private final ProfileService profileService;

  @Autowired
  public CardController(
      PasswordEncoder passwordEncoder,
      MessageSource messageSource,
      CardService cardService,
      AuthenticationController authenticationController,
      ProductService productService,
      ResultService resultService,
      PayUService payUService,
      RefundService refundService,
      EmailService emailService,
      ProfileService profileService) {
    this.passwordEncoder = passwordEncoder;
    this.messageSource = messageSource;
    this.cardService = cardService;
    this.authenticationController = authenticationController;
    this.productService = productService;
    this.resultService = resultService;
    this.payUService = payUService;
    this.refundService = refundService;
    this.emailService = emailService;
    this.profileService = profileService;
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
      return "redirect:/profile";
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

  @PostMapping("/buy_card")
  @ResponseBody
  public ResponseEntity<String> buyCard(
      @RequestBody String requestBody, @RequestHeader("OpenPayu-Signature") String header)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    if (!payUService.checkNotification(requestBody, header)) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    JsonNode jsonNode = objectMapper.readTree(requestBody);
    boolean isOrder = jsonNode.has(ORDER_PARAM);
    if (isOrder) {
      jsonNode = jsonNode.get(ORDER_PARAM);
      String status = jsonNode.get("status").asText();
      if ("COMPLETED".equals(status)) {
        String cardId = jsonNode.get("extOrderId").asText();
        String payuOrderId = jsonNode.get("orderId").asText();
        Map<String, String> cardInfo =
            objectMapper.readValue(
                objectMapper
                    .readValue(
                        jsonNode.get(PRODUCTS_PARAM).toString(),
                        new TypeReference<List<Map<String, String>>>() {})
                    .get(0)
                    .get("name"),
                new TypeReference<Map<String, String>>() {});
        if (!cardService.createCard(
            jsonNode.get("description").asText(),
            cardId,
            cardInfo.get("encryptedPin"),
            cardInfo.get("name"))) {
          refundService.createRefund(
              payuOrderId,
              jsonNode.get("additionalDescription").asText(),
              jsonNode.get("buyer").get("email").asText());
          if (!payUService.makeRefund(payuOrderId)) {
            emailService.sendEmail(
                requestBody, "multiplecard@gmail.com", "Błąd zwrotu - " + payuOrderId);
          }
        }
      }
    } else {
      String payuOrderId = jsonNode.get("orderId").asText();
      String status = jsonNode.get("refund").get("status").asText();
      if ("FINALIZED".equals(status)) {
        refundService.updateRefund(payuOrderId);
      }
    }
    return new ResponseEntity<>(HttpStatus.OK);
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
      return "redirect:/profile";
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
      return REDIRECT_PROFILE_ERROR;
    } else if (!pattern.matcher(verificationNumberSms).matches()) {
      return redirectErrorPage(httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, null);
    } else if (!passwordEncoder.matches(
        verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_BLOCK_PARAM))) {
      return redirectErrorPage(httpSession, amount, "error.bad_sms_code", locale, null);
    } else if (cardService.isBlocked(phone)) {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage("blockCardPage.error.block_card.isBlocked", null, locale));
      return REDIRECT_PROFILE_ERROR;
    } else if (!cardService.blockCard(phone)) {
      authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return REDIRECT_PROFILE_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_BLOCK_PARAM, ORDER_PARAM);
    httpSession.setAttribute(
        SUCCESS_MESSAGE,
        messageSource.getMessage("blockCardPage.success.block_card", null, locale));
    return "redirect:/profile?success";
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
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      @RequestParam(required = false) String products,
      @RequestParam(required = false) String buy,
      HttpSession httpSession,
      Model model,
      Principal principal) {
    int maxPage;
    if (!cardService.cardExists(id)) {
      model.addAttribute("cardExists", false);
    } else {
      model.addAttribute("field", field);
      model.addAttribute("isDescending", isDescending);
      model.addAttribute("pageSelected", page + 1);
      model.addAttribute("id", id);
      model.addAttribute("isBuy", false);
      if (principal != null && cardService.isOwner(principal.getName(), id)) {
        if (products != null) {
          PageOwnerProductsDTO currentPage =
              productService.getProductsByOwnerCard(
                  page, field, isDescending, text, category, shopName, id);
          List<ProductAtCardDTO> ownerProducts = currentPage.getProducts();
          maxPage = currentPage.getMaxPage();
          model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
          model.addAttribute(PRODUCTS_PARAM, ownerProducts);
          model.addAttribute("productsEmpty", ownerProducts.size() == 0);
          model.addAttribute("maxPage", maxPage);
          return "ownerProductsPage";
        } else if (buy != null) {
          model.addAttribute("isBuy", true);
        } else {
          return "choiceOwnerCardPage";
        }
      }
      if (error != null) {
        String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
        if (message != null) {
          model.addAttribute(ERROR_MESSAGE_PARAM, message);
          httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
        }
      } else if (success != null) {
        String message = (String) httpSession.getAttribute(SUCCESS_MESSAGE);
        if (message != null) {
          model.addAttribute(SUCCESS_MESSAGE, message);
          httpSession.removeAttribute(SUCCESS_MESSAGE);
        }
      }
      PageProductsWithShopDTO currentPage =
          productService.getProductsWithShops(page, field, isDescending, text, category, shopName);
      maxPage = currentPage.getMaxPage();
      List<ProductWithShopDTO> allProducts = currentPage.getProducts();
      model.addAttribute("cardExists", true);
      model.addAttribute("pages", resultService.getPages(page + 1, currentPage.getMaxPage()));
      model.addAttribute(PRODUCTS_PARAM, allProducts);
      model.addAttribute("productsEmpty", allProducts.size() == 0);
      model.addAttribute("maxPage", maxPage);
    }
    return "buyProductsPage";
  }

  @GetMapping("/cart")
  public String cartPage(Model model, Principal principal) {
    if (principal != null && profileService.checkRole(principal.getName(), Role.ROLE_USER)) {
      model.addAttribute("points", profileService.getPoints(principal.getName()));
    }
    return "cartPage";
  }

  @PostMapping("/order_card")
  public String orderCard(
      @Valid @ModelAttribute OrderCardDTO order,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale,
      Principal principal,
      HttpServletRequest httpServletRequest) {
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
      return REDIRECT_PROFILE_ERROR;
    } else if (bindingResult.hasErrors()) {
      return redirectErrorPage(httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, order);
    } else if (!passwordEncoder.matches(
        order.getCode(), (String) httpSession.getAttribute(CODE_SMS_ORDER_PARAM))) {
      return redirectErrorPage(httpSession, amount, "error.bad_sms_code", locale, order);
    } else if (!order.getPin().equals(order.getRetypedPin())) {
      return redirectErrorPage(
          httpSession, amount, "orderCard.error.not_the_same_pin", locale, order);
    }
    String paymentUrl =
        payUService.cardOrder(
            new ObjectId().toString(),
            httpServletRequest.getRemoteAddr(),
            locale,
            principal.getName(),
            order);
    if (paymentUrl == null) {
      authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return REDIRECT_PROFILE_ERROR;
    }
    httpSession.setAttribute(
        SUCCESS_MESSAGE, messageSource.getMessage("orderCard.success.buy_new_card", null, locale));
    authenticationController.resetSession(httpSession, CODE_SMS_ORDER_PARAM, ORDER_PARAM);
    return "redirect:" + paymentUrl;
  }

  @PostMapping("/check_credentials")
  public String searchCard(
      @Valid @ModelAttribute SearchCardDTO searchCard,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale) {
    final int nextStep = 1;
    if (bindingResult.hasErrors()) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale));
      return "redirect:/orders?error";
    } else if (!cardService.checkIdAndNameIsValid(searchCard)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage("searchCard.error.bad_credentials", null, locale));
      return "redirect:/orders?error";
    }
    httpSession.setAttribute(CARD_ID_PARAM, searchCard.getCardId());
    httpSession.setAttribute(STEP_PARAM, nextStep);
    return "redirect:/orders";
  }

  @PostMapping("/check_pin")
  @ResponseBody
  public String checkPin(@RequestParam String pin, HttpSession httpSession, Locale locale) {
    final int nextStep = 2;
    final int maxPinLength = 4;
    final String ordersErrorUrl = "/orders?error";
    String cardId = (String) httpSession.getAttribute(CARD_ID_PARAM);
    if (!Pattern.compile("^[0-9]*$").matcher(pin).matches() || pin.length() != maxPinLength) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale));
      return ordersErrorUrl;
    } else if (cardService.isBlocked(new ObjectId(cardId))) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage("checkPin.error.blocked_card", null, locale));
      return ordersErrorUrl;
    } else if (!cardService.checkPin(cardId, pin)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("checkPin.error.bad_pin", null, locale));
      return ordersErrorUrl;
    }
    httpSession.setAttribute(STEP_PARAM, nextStep);
    return "/orders";
  }

  @GetMapping("/reset_card")
  public String resetCard(HttpSession httpSession) {
    httpSession.removeAttribute(STEP_PARAM);
    httpSession.removeAttribute(CARD_ID_PARAM);
    return "redirect:/";
  }
}
