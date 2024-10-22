package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddProductDTO {
  @Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż][a-ząćęłńóśźż ]*$")
  @Size(min = 2, max = 30)
  @NotBlank
  private String name;

  @NotBlank
  @Size(min = 5, max = 100)
  private String description;

  @NotNull private MultipartFile file;

  @NotBlank
  @Size(min = 13, max = 13)
  @Pattern(regexp = "^[0-9]*$")
  private String barcode;

  @NotNull private List<String> category;

  @NotNull
  @Size(min = 2, max = 7)
  @Pattern(regexp = "^[0-9]*\\.[0-9]{2}$")
  private String amount;
}
