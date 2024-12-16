package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.SearchCardDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrderControllerTest {
  private static final String ORDERS_URL = "/orders";
  private static final String STEP_PARAM = "step";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockUser(roles = {"SHOP"})
  public void shouldReturnSearchCardPageAtOrderPageWhenGoForFirstTime() throws Exception {
    mockMvc
        .perform(get(ORDERS_URL))
        .andExpect(model().attribute("searchCard", new SearchCardDTO()))
        .andExpect(view().name("searchCardPage"));
  }

  @Test
  @WithMockUser(roles = {"SHOP"})
  public void shouldTypePinPageAtOrderPageWhenGetFirstStep() throws Exception {
    final int testStep = 1;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(STEP_PARAM, testStep);

    mockMvc.perform(get(ORDERS_URL).session(httpSession)).andExpect(view().name("typePinPage"));
  }

  @Test
  @WithMockUser(roles = {"SHOP"})
  public void shouldTypePinPageAtOrderPageWhenGetSecondStep() throws Exception {
    final int testStep = 2;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(STEP_PARAM, testStep);

    mockMvc.perform(get(ORDERS_URL).session(httpSession)).andExpect(view().name("ordersPage"));
  }

  @Test
  @WithMockUser(roles = {"SHOP"})
  public void shouldTypePinPageAtOrderPageWhenGetSecondStepWithErrorMessage() throws Exception {
    final int testStep = 2;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(STEP_PARAM, testStep);
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, "error!");

    mockMvc
        .perform(get(ORDERS_URL).param("error", "").session(httpSession))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, "error!"))
        .andExpect(view().name("ordersPage"));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }
}
