package com.thepapiok.multiplecard.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryGetDTO {
  private String cca2;
  private Idd idd;
  private Map<String, Translation> translations;
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
  public static class Translation {
    private String official;
    private String common;
  }
}
