package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {
  @Id private String id;
  private String name;
  private String description;
  private String imageUrl;
  private String barcode;
  private String categoryId;
  private int amount;
  private String shopId;
  private boolean isActive;
  private int promotion;
}
