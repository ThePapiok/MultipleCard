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
  private String barcode;
  private int amount;
  private ObjectId shopId;
  private LocalDate startAtPromotion;
  private LocalDate expiredAtPromotion;
  private Integer countPromotion;
  private int amountPromotion;
  private String shopName;
  private String shopImageUrl;

  public ProductWithShopDTO(ProductDTO productDTO, String shopName, String shopImageUrl) {
    isActive = productDTO.isActive();
    productId = productDTO.getProductId();
    productName = productDTO.getProductName();
    description = productDTO.getDescription();
    productImageUrl = productDTO.getProductImageUrl();
    barcode = productDTO.getBarcode();
    amount = productDTO.getAmount();
    shopId = productDTO.getShopId();
    startAtPromotion = productDTO.getStartAtPromotion();
    expiredAtPromotion = productDTO.getExpiredAtPromotion();
    countPromotion = productDTO.getCountPromotion();
    amountPromotion = productDTO.getAmountPromotion();
    this.shopName = shopName;
    this.shopImageUrl = shopImageUrl;
  }
}
