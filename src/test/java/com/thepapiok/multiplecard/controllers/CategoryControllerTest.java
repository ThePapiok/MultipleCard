package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.dto.CategoryDTO;
import com.thepapiok.multiplecard.dto.PageCategoryDTO;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ResultService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CategoryControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private CategoryService categoryService;
  @MockBean private ResultService resultService;

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

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnCategoryPageAtCategoryPageWhenEverythingOk() throws Exception {
    final String testOwnerId = "123adasdas12312312312";
    List<CategoryDTO> categoryDTOS = new ArrayList<>();
    CategoryDTO categoryDTO1 = new CategoryDTO();
    categoryDTO1.setName("testName1");
    categoryDTO1.setOwnerId(testOwnerId);
    CategoryDTO categoryDTO2 = new CategoryDTO();
    categoryDTO2.setName("testName2");
    categoryDTO2.setOwnerId(testOwnerId);
    categoryDTOS.add(categoryDTO1);
    categoryDTOS.add(categoryDTO2);
    PageCategoryDTO pageCategoryDTO = new PageCategoryDTO();
    pageCategoryDTO.setCategories(categoryDTOS);
    pageCategoryDTO.setMaxPage(1);

    when(categoryService.getCurrentPage(0, "")).thenReturn(pageCategoryDTO);
    when(resultService.getPages(1, 1)).thenReturn(List.of(1));

    mockMvc
        .perform(get("/categories"))
        .andExpect(model().attribute("pageSelected", 1))
        .andExpect(model().attribute("pages", List.of(1)))
        .andExpect(model().attribute("categories", categoryDTOS))
        .andExpect(model().attribute("categoriesEmpty", false))
        .andExpect(model().attribute("maxPage", 1))
        .andExpect(view().name("categoryPage"));
  }
}
