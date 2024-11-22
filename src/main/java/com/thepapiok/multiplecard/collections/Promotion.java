package com.thepapiok.multiplecard.collections;

import java.time.LocalDate;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "promotions")
public class Promotion {
  @Id private ObjectId id;
  private LocalDate startAt;
  private LocalDate expiredAt;
  private int newPrice;
  private ObjectId productId;
  private Integer quantity;
}
