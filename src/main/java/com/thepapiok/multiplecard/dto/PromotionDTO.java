package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PromotionDTO {
  @NotNull private LocalDate startAt;
  @FutureOrPresent @NotNull private LocalDate expiredAt;

  @NotBlank
  @Size(min = 12, max = 19)
  @Pattern(regexp = "^[0-9]*\\.[0-9]{2}zł \\([0-9]*\\.[0-9]{2}zł\\)$")
  private String newPrice;

  @Pattern(regexp = "^([1-9][0-9]*|)$")
  @Size(max = 5)
  private String quantity;

  @NotBlank
  @Size(min = 24, max = 24)
  private String productId;
}
