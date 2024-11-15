package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.services.ShopService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ShopControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private ShopService shopService;

  @Test
  public void shouldReturnListOfShopNamesAtGetShopNamesWhenEverythingOk() throws Exception {
    final String prefix = "shop";
    List<String> expectedShopNames = List.of("shop1", "shop2");

    when(shopService.getShopNamesByPrefix(prefix)).thenReturn(expectedShopNames);

    MvcResult mvcResult =
        mockMvc
            .perform(post("/get_shop_names").param("prefix", prefix))
            .andExpect(status().isOk())
            .andReturn();
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> shop =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), new TypeReference<List<String>>() {});
    assertEquals(expectedShopNames, shop);
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenEverythingOk() throws Exception {
    final String cardId = "123456789012345678901234";
    final String product1Id = "123456789012345678901231";
    final String product2Id = "123456789012345678901232";
    final int product1Amount = 1;
    final int product2Amount = 2;
    Map<String, Integer> productsId =
        Map.of(product1Id, product1Amount, product2Id, product2Amount);
    ObjectMapper objectMapper = new ObjectMapper();
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.buyProducts(productsId, cardId)).thenReturn(true);

    mockMvc
        .perform(
            post("/buy_products")
                .session(httpSession)
                .param("cardId", cardId)
                .content(objectMapper.writeValueAsString(productsId))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    assertEquals("Pomyślnie kupiono produkty", httpSession.getAttribute("successMessage"));
  }

  @Test
  public void shouldReturnStatusConflictAtBuyProductsWhenErrorAtBuyProducts() throws Exception {
    final String cardId = "123456789012345678901234";
    final String product1Id = "123456789012345678901231";
    final String product2Id = "123456789012345678901232";
    final int product1Amount = 1;
    final int product2Amount = 2;
    Map<String, Integer> productsId =
        Map.of(product1Id, product1Amount, product2Id, product2Amount);
    ObjectMapper objectMapper = new ObjectMapper();
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.buyProducts(productsId, cardId)).thenReturn(false);

    mockMvc
        .perform(
            post("/buy_products")
                .session(httpSession)
                .param("cardId", cardId)
                .content(objectMapper.writeValueAsString(productsId))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
    assertEquals("Nieoczekiwany błąd", httpSession.getAttribute("errorMessage"));
  }
}
