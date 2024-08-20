package com.thepapiok.multiplecard.dto;

import com.thepapiok.multiplecard.validators.IsApartmentNumber;
import com.thepapiok.multiplecard.validators.IsCountry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String street;

  @NotBlank
  @Pattern(regexp = "^[1-9][0-9]*([A-Z]|/[1-9][0-9]*)?$")
  @Size(min = 1, max = 10)
  private String houseNumber;

  @IsApartmentNumber(regexp = "^[1-9][0-9]*$", min = 1, max = 5)
  private String apartmentNumber;

  @NotBlank
  @Pattern(regexp = "^[0-9]{2}-[0-9]{3}$")
  @Size(min = 6, max = 6)
  private String postalCode;

  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String city;

  @NotBlank @IsCountry private String country;

  @NotBlank
  @Pattern(regexp = "^\\+[0-9]*$")
  @Size(min = 11, max = 14)
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
