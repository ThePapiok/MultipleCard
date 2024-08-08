package com.thepapiok.multiplecard.collections;

import lombok.Data;

@Data
public class Card {
  private String name;
  private String imageUrl;
  private String pin;
  private int attempts;
}
