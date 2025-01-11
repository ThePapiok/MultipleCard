package com.thepapiok.multiplecard.dto;

import lombok.Data;

@Data
public class ReviewAtReportDTO {
  private String id;
  private int count;
  private String firstName;
  private String description;
  private int rating;
}
