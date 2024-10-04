package com.thepapiok.multiplecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VerificationShopDTO {
  @NotNull private List<MultipartFile> file;

  @NotBlank
  @Pattern(regexp = "^[0-9]{3} [0-9]{3}$")
  @Size(min = 7, max = 7)
  private String verificationNumberSms;

  @NotBlank
  @Pattern(regexp = "^[0-9]{3} [0-9]{3}$")
  @Size(min = 7, max = 7)
  private String verificationNumberEmail;
}
