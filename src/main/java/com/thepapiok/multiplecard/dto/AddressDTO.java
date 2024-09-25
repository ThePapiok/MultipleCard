package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDTO {
  @NotBlank private String country;

  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String city;

  @NotBlank
  @Pattern(regexp = "^[0-9]{2}-[0-9]{3}$")
  @Size(min = 6, max = 6)
  private String postalCode;

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
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String province;
}
