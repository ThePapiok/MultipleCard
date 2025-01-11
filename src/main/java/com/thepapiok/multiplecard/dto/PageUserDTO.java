package com.thepapiok.multiplecard.dto;

import java.util.List;
import lombok.Data;

@Data
public class PageUserDTO {
  private List<UserDTO> users;
  private int maxPage;
}
