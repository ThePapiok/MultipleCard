package com.thepapiok.multiplecard.collections;

import java.time.LocalDate;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "blocked")
public class Blocked {
  @Id private ObjectId id;
  private ObjectId productId;
  private LocalDate expiredAt;
}
