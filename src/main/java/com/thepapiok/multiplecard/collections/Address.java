package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Address {
  private ObjectId countryId;
  private String city;
  private String postalCode;
  private String street;
  private String houseNumber;
  private Integer apartmentNumber;
}
