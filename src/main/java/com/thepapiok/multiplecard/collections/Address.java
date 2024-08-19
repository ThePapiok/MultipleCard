package com.thepapiok.multiplecard.collections;

import lombok.Data;

@Data
public class Address {
  private String countryId;
  private String city;
  private String postalCode;
  private String street;
  private int houseNumber;
  private int apartmentNumber;
}
