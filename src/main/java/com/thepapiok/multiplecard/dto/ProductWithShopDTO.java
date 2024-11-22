package com.thepapiok.multiplecard.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithShopDTO {
  private boolean isActive;
  private String productId;
  private String productName;
  private String description;
  private String productImageUrl;
  private int price;
  private ObjectId shopId;
  private LocalDate startAtPromotion;
  private LocalDate expiredAtPromotion;
  private Integer quantityPromotion;
  private int newPricePromotion;
  private String shopName;
  private String shopImageUrl;

  public ProductWithShopDTO(ProductDTO productDTO, String shopName, String shopImageUrl) {
    isActive = productDTO.isActive();
    productId = productDTO.getProductId();
    productName = productDTO.getProductName();
    description = productDTO.getDescription();
    productImageUrl = productDTO.getProductImageUrl();
    price = productDTO.getPrice();
    shopId = productDTO.getShopId();
    startAtPromotion = productDTO.getStartAtPromotion();
    expiredAtPromotion = productDTO.getExpiredAtPromotion();
    quantityPromotion = productDTO.getQuantityPromotion();
    newPricePromotion = productDTO.getNewPricePromotion();
    this.shopName = shopName;
    this.shopImageUrl = shopImageUrl;
  }
}
