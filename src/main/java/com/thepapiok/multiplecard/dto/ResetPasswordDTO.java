package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {
  @NotBlank
  @Pattern(regexp = "^\\+[0-9]+")
  @Size(max = 5)
  private String callingCode;

  @NotBlank
  @Pattern(regexp = "^[1-9][0-9 ]*$")
  @Size(min = 7, max = 14)
  private String phone;

  @NotBlank
  @Pattern(regexp = "^[0-9]{3} [0-9]{3}$")
  @Size(min = 7, max = 7)
  private String code;

  @NotBlank
  @Pattern(regexp = "^(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*]).*$")
  @Size(min = 6, max = 25)
  private String password;

  @NotBlank
  @Pattern(regexp = "^(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*]).*$")
  @Size(min = 6, max = 25)
  private String retypedPassword;
}
