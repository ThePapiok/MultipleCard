package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "refunds")
public class Refund {
  @Id private ObjectId id;
  private String orderId;
  private boolean isRefunded;
}
