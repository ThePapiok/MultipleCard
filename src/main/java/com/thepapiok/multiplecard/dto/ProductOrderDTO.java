package com.thepapiok.multiplecard.dto;

import lombok.Data;

@Data
public class ProductOrderDTO {
  private String name;
  private String description;
  private String imageUrl;
  private String barcode;
  private String id;
}
