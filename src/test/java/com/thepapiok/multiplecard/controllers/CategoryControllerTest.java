package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.services.CategoryService;
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
public class CategoryControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private CategoryService categoryService;

  @Test
  public void shouldReturnListOfCategoryNamesAtGetShopNamesWhenEverythingOk() throws Exception {
    final String prefix = "category";
    List<String> expectedCategoryNames = List.of("category1", "category2");

    when(categoryService.getCategoriesByPrefix(prefix)).thenReturn(expectedCategoryNames);

    MvcResult mvcResult =
        mockMvc
            .perform(post("/get_categories").param("prefix", prefix))
            .andExpect(status().isOk())
            .andReturn();
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> categoryNames =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), new TypeReference<List<String>>() {});
    assertEquals(expectedCategoryNames, categoryNames);
  }
}
