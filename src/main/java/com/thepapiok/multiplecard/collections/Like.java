package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "likes")
public class Like {
  @Id private ObjectId id;
  private ObjectId reviewUserId;
  private ObjectId userId;
}
