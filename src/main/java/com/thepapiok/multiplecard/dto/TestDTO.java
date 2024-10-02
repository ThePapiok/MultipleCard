package com.thepapiok.multiplecard.dto;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TestDTO {
  private List<MultipartFile> file;
  private String verificationNumberSms;
  private String verificationNumberEmail;
}
