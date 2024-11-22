package com.thepapiok.multiplecard.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.services.PayUService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ReservedProductService;
import com.thepapiok.multiplecard.services.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopController {
  private final ShopService shopService;
  private final ProductService productService;
  private final ReservedProductService reservedProductService;
  private final PayUService payUService;
  private final MessageSource messageSource;

  @Autowired
  public ShopController(
      ShopService shopService,
      ProductService productService,
      ReservedProductService reservedProductService,
      PayUService payUService,
      MessageSource messageSource) {
    this.shopService = shopService;
    this.productService = productService;
    this.reservedProductService = reservedProductService;
    this.payUService = payUService;
    this.messageSource = messageSource;
  }

  @PostMapping("/get_shop_names")
  public ResponseEntity<List<String>> getShopNames(@RequestParam String prefix) {
    return new ResponseEntity<>(shopService.getShopNamesByPrefix(prefix), HttpStatus.OK);
  }

  @PostMapping("/buy_products")
  @ResponseBody
  public ResponseEntity<String> buyProducts(
      @RequestBody String requestBody, @RequestHeader("OpenPayu-Signature") String header) {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/make_order")
  public ResponseEntity<String> makeOrder(
      @RequestBody Map<String, Integer> productsId,
      @RequestParam String cardId,
      Locale locale,
      HttpServletRequest httpServletRequest)
      throws JsonProcessingException {
    final String ip = httpServletRequest.getRemoteAddr();
    Map<ProductInfo, Integer> productsInfo = productService.getProductsInfo(productsId);
    if (productsInfo.size() == 0) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.bad_products", null, locale),
          HttpStatus.BAD_REQUEST);
    }
    String redirectUrl;
    if (!productService.checkProductsQuantity(productsInfo)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.bad_products", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!reservedProductService.checkReservedProductsIsLessThan100ByCardId(cardId)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.reserved_products_too_many", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(ip)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.reserved_products_too_many", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!reservedProductService.reservedProducts(productsInfo, ip, cardId)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.reserved_products_already", null, locale),
          HttpStatus.BAD_REQUEST);
    }
    Pair<Boolean, String> response = payUService.productsOrder(productsInfo, cardId, ip);
    redirectUrl = response.getSecond();
    if (response.getFirst()) {
      return new ResponseEntity<>(redirectUrl, HttpStatus.OK);
    }
    return new ResponseEntity<>(
        messageSource.getMessage("error.unexpected", null, locale), HttpStatus.BAD_REQUEST);
  }
}
