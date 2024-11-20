package com.thepapiok.multiplecard.misc;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class ProductInfo {
  private ObjectId productId;
  private boolean hasPromotion;

  public ProductInfo(String productId, boolean hasPromotion) {
    this.productId = new ObjectId(productId);
    this.hasPromotion = hasPromotion;
  }
}
