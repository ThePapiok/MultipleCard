package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.dto.ProductOrderDTO;
import com.thepapiok.multiplecard.dto.SearchCardDTO;
import com.thepapiok.multiplecard.services.OrderService;
import com.thepapiok.multiplecard.services.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
  private static final String CARD_ID_PARAM = "cardId";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String TEST_PHONE = "123412341234";
  private static final ObjectId TEST_CARD_ID = new ObjectId("123456789012345678901234");

  @Autowired private MockMvc mockMvc;
  @MockBean private OrderService orderService;
  @MockBean private ProductService productService;

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
  @WithMockUser(
      roles = {"SHOP"},
      username = TEST_PHONE)
  public void shouldTypePinPageAtOrderPageWhenGetSecondStep() throws Exception {
    final int testStep = 2;
    final String testProductName1 = "product1";
    final String testProductBarcode1 = "12341234123444";
    final String testProductDescription1 = "1243wfasdfasdfdsfa";
    final String testProductImageUrl1 = "url1";
    final String testProductName2 = "product2";
    final String testProductBarcode2 = "1234123412344";
    final String testProductDescription2 = "1243wasdfasdadsfdsfa";
    final String testProductImageUrl2 = "url2";
    final String testProductName3 = "product3";
    final String testProductBarcode3 = "53452452343";
    final String testProductDescription3 = "asfdfasdfdas";
    final String testProductImageUrl3 = "url3";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(STEP_PARAM, testStep);
    httpSession.setAttribute(CARD_ID_PARAM, TEST_CARD_ID.toHexString());
    List<ProductOrderDTO> productOrderDTOS = new ArrayList<>();
    ProductOrderDTO product1 = new ProductOrderDTO();
    product1.setName(testProductName1);
    product1.setBarcode(testProductBarcode1);
    product1.setDescription(testProductDescription1);
    product1.setImageUrl(testProductImageUrl1);
    ProductOrderDTO product2 = new ProductOrderDTO();
    product2.setName(testProductName2);
    product2.setBarcode(testProductBarcode2);
    product2.setDescription(testProductDescription2);
    product2.setImageUrl(testProductImageUrl2);
    ProductOrderDTO product3 = new ProductOrderDTO();
    product3.setName(testProductName3);
    product3.setBarcode(testProductBarcode3);
    product3.setDescription(testProductDescription3);
    product3.setImageUrl(testProductImageUrl3);
    productOrderDTOS.add(product1);
    productOrderDTOS.add(product2);
    productOrderDTOS.add(product3);

    when(productService.getProductsAtCard(TEST_PHONE, TEST_CARD_ID.toHexString()))
        .thenReturn(productOrderDTOS);

    mockMvc
        .perform(get(ORDERS_URL).session(httpSession))
        .andExpect(model().attribute("products", productOrderDTOS))
        .andExpect(view().name("ordersPage"));
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

  @Test
  @WithMockUser(roles = "SHOP", username = TEST_PHONE)
  public void shouldReturnTrueAtFinishOrderWhenEverythingOk() throws Exception {
    List<String> ids =
        List.of("323456789012345678901234", "423456789012345678901234", "523456789012345678901234");
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CARD_ID_PARAM, TEST_CARD_ID.toHexString());
    ObjectMapper objectMapper = new ObjectMapper();

    when(orderService.makeOrdersUsed(ids, TEST_CARD_ID, TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post("/finish_order")
                .session(httpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
        .andExpect(content().string("true"));
    assertEquals("Pomyślnie zakończono zamówienie", httpSession.getAttribute("successMessage"));
  }

  @Test
  @WithMockUser(roles = "SHOP", username = TEST_PHONE)
  public void shouldReturnFalseAtFinishOrderWhenError() throws Exception {
    List<String> ids =
        List.of("323456789012345678901234", "423456789012345678901234", "523456789012345678901234");
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CARD_ID_PARAM, TEST_CARD_ID.toHexString());
    ObjectMapper objectMapper = new ObjectMapper();

    when(orderService.makeOrdersUsed(ids, TEST_CARD_ID, TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post("/finish_order")
                .session(httpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
        .andExpect(content().string("false"));
  }
}
