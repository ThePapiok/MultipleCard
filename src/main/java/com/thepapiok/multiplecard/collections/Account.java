package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "accounts")
public class Account {
  @Id private ObjectId id;
  private String phone;
  private String password;
  private String email;
  private Role role;
  private boolean isActive;
  private boolean isBanned;
}
