package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EditProductDTO {

  @NotBlank
  @Size(min = 24, max = 24)
  private String id;

  @Pattern(
      regexp =
          "^[A-ZĄĆĘŁŃÓŚŹŻ]([A-ZĄĆĘŁŃÓŚŹŻ]|[a-ząćęłńóśźż])*( ([A-ZĄĆĘŁŃÓŚŹŻ]|[a-ząćęłńóśźż])+)*$")
  @Size(min = 2, max = 60)
  @NotBlank
  private String name;

  @NotBlank
  @Size(min = 5, max = 1000)
  private String description;

  private MultipartFile file;

  private String imageUrl;

  @NotBlank
  @Size(min = 13, max = 13)
  @Pattern(regexp = "^[0-9]*$")
  private String barcode;

  @NotNull private List<String> category;

  @NotNull
  @Size(min = 5, max = 9)
  @Pattern(regexp = "^[0-9]*\\.[0-9]{2}zł$")
  private String price;
}
