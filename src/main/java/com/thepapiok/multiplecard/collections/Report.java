package com.thepapiok.multiplecard.collections;

import java.time.LocalDateTime;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "reports")
public class Report {
  @Id private ObjectId id;
  private ObjectId userId;
  private ObjectId reportedId;
  private boolean isProduct;
  private LocalDateTime createdAt;
  private String description;
}
