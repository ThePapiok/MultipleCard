package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.PromotionService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/promotions")
public class PromotionController {
  private final ProductService productService;
  private final PromotionService promotionService;

  @Autowired
  public PromotionController(ProductService productService, PromotionService promotionService) {
    this.productService = productService;
    this.promotionService = promotionService;
  }

  @GetMapping
  public String addPromotionPage(@RequestParam String id, Principal principal, Model model) {
    final String isOwnerParam = "isOwner";
    Promotion promotion = null;
    if (productService.isProductOwner(principal.getName(), id)) {
      model.addAttribute(isOwnerParam, true);
      promotion = promotionService.getPromotion(id);
    } else {
      model.addAttribute(isOwnerParam, false);
    }
    if (promotion == null) {
      promotion = new Promotion();
    }
    model.addAttribute("promotion", promotion);
    model.addAttribute("productId", id);
    return "addPromotionPage";
  }

  @DeleteMapping
  @ResponseBody
  public String deletePromotion(@RequestParam String id) {
    return "ok";
  }
}
