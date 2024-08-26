package com.thepapiok.multiplecard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO implements Comparable<CountryDTO> {
  private String name;
  private String code;
  private String callingCode;

  @Override
  public int compareTo(CountryDTO o) {
    return name.compareTo(o.getName());
  }
}
