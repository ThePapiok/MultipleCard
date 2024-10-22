package com.thepapiok.multiplecard.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.services.CategoryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductControllerTest {
  private static final String TEST_PHONE = "+4324234234234234";

  @Autowired private MockMvc mockMvc;
  @MockBean private CategoryService categoryService;

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPage() throws Exception {
    mockMvc.perform(get("/products")).andExpect(view().name("productsPage"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddProductPageAtAddProductPage() throws Exception {
    List<String> categories = List.of("category1", "category2");

    when(categoryService.getAll()).thenReturn(categories);

    mockMvc
        .perform(get("/add_product"))
        .andExpect(model().attribute("categories", categories))
        .andExpect(model().attribute("addProduct", new AddProductDTO()))
        .andExpect(view().name("addProductPage"));
  }
}
