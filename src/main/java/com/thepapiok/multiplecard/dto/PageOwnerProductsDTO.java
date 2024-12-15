package com.thepapiok.multiplecard.dto;

import java.util.List;
import lombok.Data;

@Data
public class PageOwnerProductsDTO {
  private int maxPage;
  private List<ProductAtCardDTO> products;
}
