package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "reservedProducts")
public class ReservedProduct {
  @Id private ObjectId id;
  private ObjectId promotionId;
  private ObjectId orderId;
  private ObjectId cardId;
  private String encryptedIp;
}
