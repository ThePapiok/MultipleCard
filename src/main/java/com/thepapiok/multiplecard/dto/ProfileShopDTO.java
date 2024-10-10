package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileShopDTO {
  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+$")
  @Size(min = 2, max = 15)
  private String firstName;

  @NotBlank
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+([- ][A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+)?$")
  @Size(min = 2, max = 40)
  private String lastName;

  @NotBlank
  @Size(min = 2, max = 20)
  private String name;

  @NotBlank
  @Pattern(regexp = "^[0-9]*$")
  @Size(min = 26, max = 26)
  private String accountNumber;

  @NotNull private List<AddressDTO> addresses;
  private String imageUrl;
  @NotBlank private String totalAmount;
  private MultipartFile file;
}
