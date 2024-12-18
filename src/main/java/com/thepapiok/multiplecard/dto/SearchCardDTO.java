package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SearchCardDTO {
  @Size(min = 24, max = 24)
  @Pattern(regexp = "^[0-9a-f]*$")
  private String cardId;

  @Size(min = 4, max = 9)
  @Pattern(regexp = "^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]*$")
  private String cardName;
}
