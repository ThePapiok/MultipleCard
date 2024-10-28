package com.thepapiok.multiplecard.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ResultService {
  public List<Integer> getPages(int page, int maxPage) {
    try {
      List<Integer> pages = new ArrayList<>();
      final int maxPagesAfter = 3;
      final int maxPagesBefore = 4;
      int countI = 0;
      for (int i = page; i >= 1; i--) {
        countI++;
        if (countI > maxPagesBefore) {
          break;
        }
        pages.add(i);
      }
      Collections.reverse(pages);
      countI = 0;
      for (int i = page + 1; i <= maxPage; i++) {
        countI++;
        if (countI > maxPagesAfter) {
          break;
        }
        pages.add(i);
      }
      return pages;
    } catch (Exception e) {
      return List.of();
    }
  }
}
