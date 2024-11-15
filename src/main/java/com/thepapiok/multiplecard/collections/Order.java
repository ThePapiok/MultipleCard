package com.thepapiok.multiplecard.collections;

import java.time.LocalDateTime;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class Order {
  @Id private ObjectId id;
  private ObjectId cardId;
  private ObjectId productId;
  private ObjectId shopId;
  private LocalDateTime createdAt;
  private boolean isUsed;
  private int amount;
}
