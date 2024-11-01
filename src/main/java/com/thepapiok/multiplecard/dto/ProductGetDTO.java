package com.thepapiok.multiplecard.dto;

import com.thepapiok.multiplecard.collections.Blocked;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import lombok.Data;

@Data
public class ProductGetDTO {
  private Product product;
  private Promotion promotion;
  private Blocked blocked;
}
