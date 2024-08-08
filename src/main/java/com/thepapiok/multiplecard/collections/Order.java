package com.thepapiok.multiplecard.collections;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class Order {
  @Id private String id;
  private String userId;
  private String productId;
  private LocalDateTime createdAt;
  private boolean isUsed;
  private int amount;
}
