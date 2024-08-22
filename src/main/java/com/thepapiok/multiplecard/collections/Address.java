package com.thepapiok.multiplecard.collections;

import lombok.Data;

@Data
public class Address {
  private String country;
  private String city;
  private String postalCode;
  private String street;
  private String houseNumber;
  private Integer apartmentNumber;
}
