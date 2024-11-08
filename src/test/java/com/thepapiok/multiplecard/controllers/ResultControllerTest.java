package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thepapiok.multiplecard.services.ResultService;
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
public class ResultControllerTest {

  @MockBean private ResultService resultService;

  @Autowired private MockMvc mvc;

  @Test
  public void shouldReturnResponseOfListPagesAtGetPagesWhenEverythingOk() throws Exception {
    final int maxPage = 5;
    final List<Integer> pages = List.of(1, 2, 3, 4, 5);

    when(resultService.getPages(1, maxPage)).thenReturn(pages);

    MvcResult mvcResult =
        mvc.perform(post("/get_pages").param("page", "0").param("maxPage", "5"))
            .andExpect(status().isOk())
            .andReturn();
    assertEquals(
        pages.toString().replaceAll(" ", ""), mvcResult.getResponse().getContentAsString());
  }
}
