package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PromotionDTO {
  @NotNull private LocalDate startAt;
  @FutureOrPresent @NotNull private LocalDate expiredAt;

  @NotBlank
  @Pattern(regexp = "^[0-9]*\\.?[0-9]{2}(zł)?$")
  private String amount;

  @NotBlank
  @Pattern(regexp = "^[1-9][0-9]*$")
  private String count;
}
