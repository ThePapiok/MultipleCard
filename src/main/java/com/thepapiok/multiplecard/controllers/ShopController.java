package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.services.ShopService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopController {
  private final ShopService shopService;

  @Autowired
  public ShopController(ShopService shopService) {
    this.shopService = shopService;
  }

  @PostMapping("/get_shop_names")
  public ResponseEntity<List<String>> getShopNames(@RequestParam String prefix) {
    return new ResponseEntity<>(shopService.getShopNamesByPrefix(prefix), HttpStatus.OK);
  }
}
