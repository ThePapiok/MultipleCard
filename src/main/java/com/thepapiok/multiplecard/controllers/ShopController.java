package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.services.ShopService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopController {
  private final ShopService shopService;
  private final MessageSource messageSource;

  @Autowired
  public ShopController(ShopService shopService, MessageSource messageSource) {
    this.shopService = shopService;
    this.messageSource = messageSource;
  }

  @PostMapping("/get_shop_names")
  public ResponseEntity<List<String>> getShopNames(@RequestParam String prefix) {
    return new ResponseEntity<>(shopService.getShopNamesByPrefix(prefix), HttpStatus.OK);
  }

  @PostMapping("/buy_products")
  public ResponseEntity<Boolean> buyProducts(
      @RequestBody Map<String, Integer> productsId,
      @RequestParam String cardId,
      HttpSession httpSession,
      Locale locale) {
    if (shopService.buyProducts(productsId, cardId)) {
      httpSession.setAttribute(
          "successMessage",
          messageSource.getMessage("buyProducts.success.buy_products", null, locale));
      return new ResponseEntity<>(HttpStatus.OK);
    }
    httpSession.setAttribute(
        "errorMessage", messageSource.getMessage("error.unexpected", null, locale));
    return new ResponseEntity<>(HttpStatus.CONFLICT);
  }
}
