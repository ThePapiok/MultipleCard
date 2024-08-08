package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "likes")
public class Like {
  @Id private String id;
  private String reviewUserId;
  private String userId;
}
