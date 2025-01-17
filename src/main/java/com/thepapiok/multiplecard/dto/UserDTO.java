package com.thepapiok.multiplecard.dto;

import lombok.Data;

@Data
public class UserDTO {
  private String id;
  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private String role;
  private boolean isActive;
  private boolean isBanned;
  private Boolean restricted;
  private String shopName;
}
