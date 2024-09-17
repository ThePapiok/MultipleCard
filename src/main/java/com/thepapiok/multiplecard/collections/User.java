package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class User {
  @Id private ObjectId id;
  private String firstName;
  private String lastName;
  private ObjectId cardId;
  private int points;
  private Review review;
  private Address address;
}
