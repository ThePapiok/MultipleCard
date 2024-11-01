package com.thepapiok.multiplecard.dto;

import com.thepapiok.multiplecard.collections.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
  private boolean isActive;
  private Product product;
}
