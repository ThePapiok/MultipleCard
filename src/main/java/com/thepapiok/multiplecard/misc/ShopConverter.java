package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShopConverter {

  private final AddressConverter addressConverter;

  @Autowired
  public ShopConverter(AddressConverter addressConverter) {
    this.addressConverter = addressConverter;
  }

  public Shop getEntity(RegisterShopDTO registerShopDTO) {
    Shop shop = new Shop();
    shop.setFirstName(registerShopDTO.getFirstName());
    shop.setLastName(registerShopDTO.getLastName());
    shop.setName(registerShopDTO.getName());
    shop.setAccountNumber(registerShopDTO.getAccountNumber());
    shop.setImageUrl("");
    shop.setPoints(addressConverter.getEntities(registerShopDTO.getAddress()));
    return shop;
  }
}
