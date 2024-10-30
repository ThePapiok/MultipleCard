package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.PromotionDTO;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.PromotionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/promotions")
public class PromotionController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String PROMOTION_PARAM = "promotion";
  private final ProductService productService;
  private final PromotionService promotionService;
  private final MessageSource messageSource;

  @Autowired
  public PromotionController(
      ProductService productService,
      PromotionService promotionService,
      MessageSource messageSource) {
    this.productService = productService;
    this.promotionService = promotionService;
    this.messageSource = messageSource;
  }

  @GetMapping
  public String addPromotionPage(
      @RequestParam(required = false) String error,
      @RequestParam String id,
      Principal principal,
      Model model,
      HttpSession httpSession) {
    final String isOwnerParam = "isOwner";
    PromotionDTO promotionDTO = null;
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    }
    if (productService.isProductOwner(principal.getName(), id)) {
      Double amount = productService.getAmount(id);
      if (amount != null) {
        model.addAttribute("originalAmount", amount);
      }
      model.addAttribute(isOwnerParam, true);
      promotionDTO = promotionService.getPromotionDTO(id);
    } else {
      model.addAttribute(isOwnerParam, false);
    }
    if (promotionDTO == null) {
      promotionDTO = new PromotionDTO();
      promotionDTO.setProductId(id);
    }
    model.addAttribute(PROMOTION_PARAM, promotionDTO);
    model.addAttribute("productId", id);
    return "addPromotionPage";
  }

  @PostMapping
  public String addPromotion(
      @Valid @ModelAttribute PromotionDTO promotion,
      BindingResult bindingResult,
      Principal principal,
      Locale locale,
      HttpSession httpSession) {
    final LocalDate startAt = promotion.getStartAt();
    final LocalDate expiredAt = promotion.getExpiredAt();
    final String id = promotion.getProductId();
    boolean error = false;
    String message = null;
    if (bindingResult.hasErrors()) {
      error = true;
      message = messageSource.getMessage("validation.incorrect_data", null, locale);
    } else if (expiredAt.isBefore(startAt)) {
      error = true;
      message =
          messageSource.getMessage("addPromotion.error.expiredAt_before_startAt", null, locale);
    } else if (!promotionService.checkDateIsMaxNextYear(startAt)) {
      error = true;
      message = messageSource.getMessage("addPromotion.error.startAt_too_far", null, locale);
    } else if (!promotionService.checkDateIsMaxNextYear(expiredAt)) {
      error = true;
      message = messageSource.getMessage("addPromotion.error.expiredAt_too_far", null, locale);
    } else if (!productService.isProductOwner(principal.getName(), id)) {
      error = true;
      message = messageSource.getMessage("addPromotion.error.not_owner", null, locale);
    } else if (!promotionService.checkNewStartAtIsPresent(startAt, id)) {
      error = true;
      message = messageSource.getMessage("addPromotion.error.startAt_not_present", null, locale);
    } else if (!productService.isLessThanOriginalPrice(promotion.getAmount(), id)) {
      error = true;
      message = messageSource.getMessage("addPromotion.error.amount_too_less", null, locale);
    }
    if (error) {
      httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
      return "redirect:/promotions?id=" + id + "&error";
    }
    if (!promotionService.upsertPromotion(promotion)) {
      System.out.println(promotion);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return "redirect:/products?error";
    }
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("addPromotion.success.upsert_promotion", null, locale));
    return "redirect:/products?success";
  }

  @DeleteMapping
  @ResponseBody
  public String deletePromotion(@RequestParam String id) {
    return "ok";
  }
}
