package com.thepapiok.multiplecard.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.PromotionService;
import org.bson.types.ObjectId;
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
public class PromotionControllerTest {
  private static final String TEST_PHONE = "+48421341234213";
  private static final String TEST_PRODUCT_ID = "123456789012345678901234";
  private static final String IS_OWNER_PARAM = "isOwner";
  private static final String PROMOTION_PARAM = "promotion";
  private static final String PRODUCT_ID_PARAM = "productId";
  private static final String PROMOTIONS_URL = "/promotions";
  private static final String ADD_PROMOTION_PAGE = "addPromotionPage";
  private static final String ID_PARAM = "id";
  @Autowired private MockMvc mockMvc;
  @MockBean private ProductService productService;
  @MockBean private PromotionService promotionService;

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddPromotionPageAtAddPromotionPageWhenItNotOwner() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(false);

    mockMvc
        .perform(get(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(IS_OWNER_PARAM, false))
        .andExpect(model().attribute(PROMOTION_PARAM, new Promotion()))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddPromotionPageAtAddPromotionPageWhenItOwnerAndHasPromotion()
      throws Exception {
    Promotion promotion = new Promotion();
    promotion.setCount(0);
    promotion.setProductId(new ObjectId(TEST_PRODUCT_ID));

    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.getPromotion(TEST_PRODUCT_ID)).thenReturn(promotion);

    mockMvc
        .perform(get(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(IS_OWNER_PARAM, true))
        .andExpect(model().attribute(PROMOTION_PARAM, promotion))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddPromotionPageAtAddPromotionPageWhenItOwnerAndHasNotPromotion()
      throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.getPromotion(TEST_PRODUCT_ID)).thenReturn(null);

    mockMvc
        .perform(get(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(IS_OWNER_PARAM, true))
        .andExpect(model().attribute(PROMOTION_PARAM, new Promotion()))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnOkAtDeletePromotion() throws Exception {
    mockMvc
        .perform(delete(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(content().string("ok"));
  }
}
