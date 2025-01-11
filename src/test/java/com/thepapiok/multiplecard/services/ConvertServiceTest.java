package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

public class ConvertServiceTest {
  private static final String IDS_PARAM = "ids";
  private final ConvertService convertService = new ConvertService();

  @Test
  public void shouldReturnEmptyHashSetAtGetIdsWhenIdsNotAtSession() {
    MockHttpSession httpSession = new MockHttpSession();

    assertEquals(new HashSet<>(), convertService.getIds(httpSession));
  }

  @Test
  public void shouldReturnHashSetAtGetIdsWhenEverythingOk() {
    HashSet<String> ids = new HashSet<>();
    ids.add("123fsdfsadfsddsff");
    ids.add("sdfadfasa1231432");
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(IDS_PARAM, ids);

    assertEquals(ids, convertService.getIds(httpSession));
  }

  @Test
  public void shouldReturnEmptyHashSetAtGetIdsWhenIsNotStringSet() {
    HashSet<Integer> ids = new HashSet<>();
    ids.add(1);
    ids.add(0);
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(IDS_PARAM, ids);

    assertEquals(new HashSet<>(), convertService.getIds(httpSession));
  }

  @Test
  public void shouldReturnEmptyHashSetAtGetIdsWhenIsNotSet() {
    List<String> ids = new ArrayList<>();
    ids.add("sfdasfdsfda1231");
    ids.add("sf234wefasfdasfdsf");
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(IDS_PARAM, ids);

    assertEquals(new HashSet<>(), convertService.getIds(httpSession));
  }
}
