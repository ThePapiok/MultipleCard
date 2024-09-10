package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$")
  @Size(min = 2, max = 15)
  private String firstName;

  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String lastName;

  @NotBlank
  @Pattern(regexp = "^[A-za-z0-9-._]+@[A-z0-9a-z-.]+\\.[a-zA-z]{2,4}$")
  @Size(min = 4, max = 30)
  private String email;

  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String province;

  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String street;

  @NotBlank
  @Pattern(regexp = "^[1-9][0-9]*([A-Z]|/[1-9][0-9]*)?$")
  @Size(min = 1, max = 10)
  private String houseNumber;

  @Pattern(regexp = "^([1-9][0-9]*|)$")
  @Size(max = 5)
  private String apartmentNumber;

  @NotBlank
  @Pattern(regexp = "^[0-9]{2}-[0-9]{3}$")
  @Size(min = 6, max = 6)
  private String postalCode;

  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String city;

  @NotBlank private String country;

  @NotBlank
  @Pattern(regexp = "^\\+[0-9]+")
  @Size(max = 5)
  private String callingCode;

  @NotBlank
  @Pattern(regexp = "^[1-9][0-9 ]*$")
  @Size(min = 7, max = 14)
  private String phone;

  @NotBlank
  @Pattern(regexp = "^(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*]).*$")
  @Size(min = 6, max = 25)
  private String password;

  @NotBlank
  @Pattern(regexp = "^(?=.*[a-ząćęłńóśźż])(?=.*[A-ZĄĆĘŁŃÓŚŹŻ])(?=.*[0-9])(?=.*[!@#$%^&*]).*$")
  @Size(min = 6, max = 25)
  private String retypedPassword;
}
