package com.thepapiok.multiplecard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryGetDTO {
  private String cca2;
  private Idd idd;
  private Name name;
  private Float area;
  private Integer population;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Idd {
    private String root;
    private String[] suffixes;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Name {
    private String common;
  }
}
