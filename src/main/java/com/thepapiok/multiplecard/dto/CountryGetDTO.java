package com.thepapiok.multiplecard.dto;

import java.util.Map;
import lombok.Data;

@Data
public class CountryGetDTO {
  private String cca2;
  private Idd idd;
  private Map<String, Translation> translations;
  private Float area;
  private Integer population;

  @Data
  public static class Idd {
    private String root;
    private String[] suffixes;
  }

  @Data
  public static class Translation {
    private String official;
    private String common;
  }
}
