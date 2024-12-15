package com.thepapiok.multiplecard.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageProductsWithShopDTO {
  private int maxPage;
  private List<ProductWithShopDTO> products;
}
