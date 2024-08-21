package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
  @NotBlank
  @Pattern(regexp = "^\\+[0-9]{1,4} ?[0-9]{3} ?[0-9]{3} ?[0-9]{3}$")
  @Size(min = 11, max = 17)
  private String phone;

  @NotBlank
  @Pattern(regexp = "^(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*]).*$")
  @Size(min = 6, max = 25)
  private String password;
}
