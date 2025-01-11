package com.thepapiok.multiplecard.dto;

import java.util.List;
import lombok.Data;

@Data
public class PageCategoryDTO {
  List<CategoryDTO> categories;
  int maxPage;
}
