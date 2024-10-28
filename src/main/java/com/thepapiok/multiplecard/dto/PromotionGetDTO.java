package com.thepapiok.multiplecard.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromotionGetDTO {
  private String productId;
  private LocalDate startAt;
  private LocalDate expiredAt;
  private int amount;
}
