package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "accounts")
public class Account {
  @Id private String id;
  private String phone;
  private String password;
  private Role role;
  private boolean isActive;
  private String verificationNumber;
}
