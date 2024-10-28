package com.thepapiok.multiplecard.collections;

import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {
  @Id private ObjectId id;
  private String name;
  private String description;
  private String imageUrl;
  private String barcode;
  private List<ObjectId> categories;
  private int amount;
  private ObjectId shopId;
  private boolean isActive;
}
