package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderCardDTO {
  @NotBlank
  @Pattern(regexp = "^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]*$")
  @Size(min = 4, max = 9)
  private String name;

  @NotBlank
  @Pattern(regexp = "^[0-9]*$")
  @Size(min = 4, max = 4)
  private String pin;

  @NotBlank
  @Pattern(regexp = "^[0-9]*$")
  @Size(min = 4, max = 4)
  private String retypedPin;

  @NotBlank
  @Pattern(regexp = "^[0-9]{3} [0-9]{3}$")
  @Size(min = 7, max = 7)
  private String code;
}
