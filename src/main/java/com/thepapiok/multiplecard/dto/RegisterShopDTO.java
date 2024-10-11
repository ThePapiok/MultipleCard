package com.thepapiok.multiplecard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterShopDTO {
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

  @NotBlank
  @Size(min = 2, max = 20)
  private String name;

  @Valid @NotNull private List<AddressDTO> address;

  @NotBlank
  @Pattern(regexp = "^[0-9]*$")
  @Size(min = 26, max = 26)
  private String accountNumber;

  @NotNull private MultipartFile file;
}
