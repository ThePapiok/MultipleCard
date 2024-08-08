package com.thepapiok.multiplecard.collections;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Review {
  private String description;
  private int rating;
  private LocalDateTime createdAt;
}
