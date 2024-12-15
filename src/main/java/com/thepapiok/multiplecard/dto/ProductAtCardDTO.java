package com.thepapiok.multiplecard.dto;

import lombok.Data;

@Data
public class ProductAtCardDTO {
  private String productName;
  private String description;
  private String productImageUrl;
  private int price;
  private String shopName;
  private String shopImageUrl;
  private int count;
}
