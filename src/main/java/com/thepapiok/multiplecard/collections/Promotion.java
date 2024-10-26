package com.thepapiok.multiplecard.collections;

import java.time.LocalDateTime;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "promotions")
public class Promotion {
  @Id private ObjectId id;
  private LocalDateTime startAt;
  private LocalDateTime expiredAt;
  private int amount;
  private ObjectId productId;
}
