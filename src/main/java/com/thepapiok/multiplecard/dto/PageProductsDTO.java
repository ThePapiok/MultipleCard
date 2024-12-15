package com.thepapiok.multiplecard.dto;

import java.util.List;
import lombok.Data;

@Data
public class PageProductsDTO {
  private int maxPage;
  private List<ProductDTO> products;
}
