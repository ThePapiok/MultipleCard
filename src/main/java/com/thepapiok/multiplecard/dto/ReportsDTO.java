package com.thepapiok.multiplecard.dto;

import java.util.List;
import lombok.Data;

@Data
public class ReportsDTO {
  private String id;
  private boolean isProduct;
  private List<ReportDTO> reports;
}
