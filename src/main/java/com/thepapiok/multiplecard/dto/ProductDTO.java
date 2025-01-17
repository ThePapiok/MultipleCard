package com.thepapiok.multiplecard.dto;

import java.time.LocalDate;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ProductDTO {
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
}
