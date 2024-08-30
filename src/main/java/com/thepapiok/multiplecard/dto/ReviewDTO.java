package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ReviewDTO {
  @NotBlank
  @Size(min = 1, max = 80)
  private String description;

  @Range(min = 0, max = 5)
  private int rating;
}
