package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ResultServiceTest {
  private static final int PAGE_1 = 1;
  private static final int PAGE_2 = 2;
  private static final int PAGE_3 = 3;
  private static final int PAGE_4 = 4;
  private static final int PAGE_5 = 5;
  private static final int PAGE_6 = 6;
  private static final int PAGE_7 = 7;
  private static final int PAGE_8 = 8;

  private final ResultService resultService = new ResultService();

  @Test
  public void shouldReturnListOf4ElementsAtGetPagesWhenNoElementsBefore() {
    final int maxPage = 8;

    assertEquals(List.of(PAGE_1, PAGE_2, PAGE_3, PAGE_4), resultService.getPages(PAGE_1, maxPage));
  }

  @Test
  public void shouldReturnListOf2ElementsAtGetPagesWhenNoElementsBeforeAndMaxPageLessThan4() {
    final int maxPage = 2;

    assertEquals(List.of(PAGE_1, PAGE_2), resultService.getPages(PAGE_1, maxPage));
  }

  @Test
  public void shouldReturnListOf6ElementsAtGetPagesWhenElementsBefore() {
    final int maxPage = 8;

    assertEquals(
        List.of(PAGE_1, PAGE_2, PAGE_3, PAGE_4, PAGE_5, PAGE_6),
        resultService.getPages(PAGE_3, maxPage));
  }

  @Test
  public void shouldReturnListOf7ElementsAtGetPagesWhenContainsSomeElementsBeforeAndAfter() {
    final int maxPage = 8;

    assertEquals(
        List.of(PAGE_2, PAGE_3, PAGE_4, PAGE_5, PAGE_6, PAGE_7, PAGE_8),
        resultService.getPages(PAGE_5, maxPage));
  }

  @Test
  public void shouldReturnListOf6ElementsAtGetPagesWhenElementsAfter() {
    final int maxPage = 8;

    assertEquals(
        List.of(PAGE_3, PAGE_4, PAGE_5, PAGE_6, PAGE_7, PAGE_8),
        resultService.getPages(PAGE_6, maxPage));
  }

  @Test
  public void shouldReturnListOf4ElementsAtGetPagesWhenNoElementsAfter() {
    final int maxPage = 8;

    assertEquals(List.of(PAGE_5, PAGE_6, PAGE_7, PAGE_8), resultService.getPages(PAGE_8, maxPage));
  }

  @Test
  public void shouldReturnListOf2ElementsAtGetPagesWhenNoElementsAfterAndElementBeforeLessThan3() {
    final int maxPage = 2;

    assertEquals(List.of(PAGE_1, PAGE_2), resultService.getPages(PAGE_2, maxPage));
  }
}
