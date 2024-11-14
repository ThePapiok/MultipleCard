package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.services.ShopService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
}
