package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.PromotionDTO;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.PromotionService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PromotionControllerTest {
  private static final String TEST_PHONE = "+48421341234213";
  private static final String TEST_AMOUNT = "123.11zł (134.22zł)";
  private static final String TEST_AMOUNT_WITHOUT_CURRENCY = "123.11";
  private static final String TEST_PRODUCT_ID = "123456789012345678901234";
  private static final String IS_OWNER_PARAM = "isOwner";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String ERROR_PARAM = "error";
  private static final String PROMOTION_PARAM = "promotion";
  private static final String ORIGINAL_AMOUNT_PARAM = "originalAmount";
  private static final String PRODUCT_ID_PARAM = "productId";
  private static final String PROMOTIONS_URL = "/promotions";
  private static final String PROMOTIONS_ID_URL = "/promotions?id=";
  private static final String ERROR_URL_PARAM = "&error";
  private static final String ADD_PROMOTION_PAGE = "addPromotionPage";
  private static final String ID_PARAM = "id";
  private static final LocalDate TEST_DATE1 = LocalDate.now();
  private static final LocalDate TEST_DATE2 = LocalDate.now().plusYears(1);

  @Autowired private MockMvc mockMvc;
  @MockBean private ProductService productService;
  @MockBean private PromotionService promotionService;

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddPromotionPageAtAddPromotionPageWhenItNotOwner() throws Exception {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(false);

    mockMvc
        .perform(get(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(IS_OWNER_PARAM, false))
        .andExpect(model().attribute(PROMOTION_PARAM, promotionDTO))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldReturnAddPromotionPageAtAddPromotionPageWhenItNotOwnerAndParamErrorWithoutMessage()
          throws Exception {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(false);

    mockMvc
        .perform(get(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(IS_OWNER_PARAM, false))
        .andExpect(model().attribute(PROMOTION_PARAM, promotionDTO))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddPromotionPageAtAddPromotionPageWhenItNotOwnerAndParamErrorWithMessage()
      throws Exception {
    final String errorMessage = "error!";
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setProductId(TEST_PRODUCT_ID);
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, errorMessage);

    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(false);

    mockMvc
        .perform(
            get(PROMOTIONS_URL)
                .param(ID_PARAM, TEST_PRODUCT_ID)
                .param(ERROR_PARAM, "")
                .session(httpSession))
        .andExpect(model().attribute(IS_OWNER_PARAM, false))
        .andExpect(model().attribute(PROMOTION_PARAM, promotionDTO))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, errorMessage))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldReturnAddPromotionPageAtAddPromotionPageWhenItOwnerHasPromotionAndProductAmountFound()
          throws Exception {
    final Double amount = 30.12;
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setCount("");
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.getPromotionDTO(TEST_PRODUCT_ID)).thenReturn(promotionDTO);
    when(productService.getAmount(TEST_PRODUCT_ID)).thenReturn(amount);

    mockMvc
        .perform(get(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(IS_OWNER_PARAM, true))
        .andExpect(model().attribute(PROMOTION_PARAM, promotionDTO))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(ORIGINAL_AMOUNT_PARAM, amount))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldReturnAddPromotionPageAtAddPromotionPageWhenItOwnerHasNotPromotionAndProductAmountNotFound()
          throws Exception {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.getPromotionDTO(TEST_PRODUCT_ID)).thenReturn(null);
    when(productService.getAmount(TEST_PRODUCT_ID)).thenReturn(null);

    mockMvc
        .perform(get(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(model().attribute(IS_OWNER_PARAM, true))
        .andExpect(model().attribute(PROMOTION_PARAM, promotionDTO))
        .andExpect(model().attribute(PRODUCT_ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(view().name(ADD_PROMOTION_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnOkAtDeletePromotionWhenEverythingOk() throws Exception {
    mockMvc
        .perform(delete(PROMOTIONS_URL).param(ID_PARAM, TEST_PRODUCT_ID))
        .andExpect(content().string("ok"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldRedirectToPromotionsId123456789012345678901234WithErrorAtAddPromotionWhenErrorAtValidation()
          throws Exception {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount("123zł (134zł)");
    promotionDTO.setStartAt(TEST_DATE1);
    promotionDTO.setExpiredAt(TEST_DATE2);
    promotionDTO.setCount("z");
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM,
        "Podane dane są niepoprawne",
        PROMOTIONS_ID_URL + TEST_PRODUCT_ID + ERROR_URL_PARAM,
        promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldRedirectToPromotionsId123456789012345678901234WithErrorAtAddPromotionWhenExpiredDateIsBeforeStartDate()
          throws Exception {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount(TEST_AMOUNT);
    promotionDTO.setStartAt(TEST_DATE2);
    promotionDTO.setExpiredAt(TEST_DATE1);
    promotionDTO.setCount("");
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM,
        "Data końcowa promocji nie może być wcześniejsza od daty rozpoczęcia",
        PROMOTIONS_ID_URL + TEST_PRODUCT_ID + ERROR_URL_PARAM,
        promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldRedirectToPromotionsId123456789012345678901234WithErrorAtAddPromotionWhenStartDateIsTooFar()
          throws Exception {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount(TEST_AMOUNT);
    promotionDTO.setStartAt(TEST_DATE1.plusYears(2));
    promotionDTO.setExpiredAt(TEST_DATE2.plusYears(2));
    promotionDTO.setCount("");
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getStartAt())).thenReturn(false);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM,
        "Data rozpoczęcia promocji może być maksymalnie rok do przodu",
        PROMOTIONS_ID_URL + TEST_PRODUCT_ID + ERROR_URL_PARAM,
        promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldRedirectToPromotionsId123456789012345678901234WithErrorAtAddPromotionWhenExpiredDateIsTooFar()
          throws Exception {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount(TEST_AMOUNT);
    promotionDTO.setStartAt(TEST_DATE1);
    promotionDTO.setExpiredAt(TEST_DATE2.plusYears(2));
    promotionDTO.setCount("");
    promotionDTO.setProductId(TEST_PRODUCT_ID);

    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getStartAt())).thenReturn(true);
    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getExpiredAt())).thenReturn(false);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM,
        "Data zakończenia promocji może być maksymalnie rok do przodu",
        PROMOTIONS_ID_URL + TEST_PRODUCT_ID + ERROR_URL_PARAM,
        promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldRedirectToPromotionsId123456789012345678901234WithErrorAtAddPromotionWhenItsNotOwner()
          throws Exception {
    PromotionDTO promotionDTO = setPromotionDTO();

    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getStartAt())).thenReturn(true);
    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getExpiredAt())).thenReturn(true);
    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(false);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM,
        "Nie posiadasz tego produktu",
        PROMOTIONS_ID_URL + TEST_PRODUCT_ID + ERROR_URL_PARAM,
        promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldRedirectToPromotionsId123456789012345678901234WithErrorAtAddPromotionWhenStartAtIsNotPresent()
          throws Exception {
    PromotionDTO promotionDTO = setPromotionDTO();

    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getStartAt())).thenReturn(true);
    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getExpiredAt())).thenReturn(true);
    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.checkNewStartAtIsPresent(promotionDTO.getStartAt(), TEST_PRODUCT_ID))
        .thenReturn(false);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM,
        "Data rozpoczęcia promocji nie może być przeszłością",
        PROMOTIONS_ID_URL + TEST_PRODUCT_ID + ERROR_URL_PARAM,
        promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void
      shouldRedirectToPromotionsId123456789012345678901234WithErrorAtAddPromotionWhenNewPriceIsTooLess()
          throws Exception {
    PromotionDTO promotionDTO = setPromotionDTO();

    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getStartAt())).thenReturn(true);
    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getExpiredAt())).thenReturn(true);
    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.checkNewStartAtIsPresent(promotionDTO.getStartAt(), TEST_PRODUCT_ID))
        .thenReturn(true);
    when(productService.isLessThanOriginalPrice(TEST_AMOUNT_WITHOUT_CURRENCY, TEST_PRODUCT_ID))
        .thenReturn(false);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM,
        "Nowa cena musi być mniejsza od ceny produktu",
        PROMOTIONS_ID_URL + TEST_PRODUCT_ID + ERROR_URL_PARAM,
        promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsWithErrorAtAddPromotionWhenErrorAtAddPromotion()
      throws Exception {
    PromotionDTO promotionDTO = setPromotionDTO();
    PromotionDTO expectedPromotionDTO = new PromotionDTO();
    expectedPromotionDTO.setAmount(TEST_AMOUNT_WITHOUT_CURRENCY);
    expectedPromotionDTO.setStartAt(TEST_DATE1);
    expectedPromotionDTO.setExpiredAt(TEST_DATE2);
    expectedPromotionDTO.setCount("");
    expectedPromotionDTO.setProductId(TEST_PRODUCT_ID);

    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getStartAt())).thenReturn(true);
    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getExpiredAt())).thenReturn(true);
    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.checkNewStartAtIsPresent(promotionDTO.getStartAt(), TEST_PRODUCT_ID))
        .thenReturn(true);
    when(productService.isLessThanOriginalPrice(TEST_AMOUNT_WITHOUT_CURRENCY, TEST_PRODUCT_ID))
        .thenReturn(true);
    when(promotionService.upsertPromotion(expectedPromotionDTO)).thenReturn(false);

    performPostAddPromotion(
        ERROR_MESSAGE_PARAM, "Nieoczekiwany błąd", "/products?error", promotionDTO);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsWithSuccessAtAddPromotionWhenEverythingOk() throws Exception {
    PromotionDTO promotionDTO = setPromotionDTO();
    PromotionDTO expectedPromotionDTO = new PromotionDTO();
    expectedPromotionDTO.setAmount(TEST_AMOUNT_WITHOUT_CURRENCY);
    expectedPromotionDTO.setStartAt(TEST_DATE1);
    expectedPromotionDTO.setExpiredAt(TEST_DATE2);
    expectedPromotionDTO.setCount("");
    expectedPromotionDTO.setProductId(TEST_PRODUCT_ID);

    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getStartAt())).thenReturn(true);
    when(promotionService.checkDateIsMaxNextYear(promotionDTO.getExpiredAt())).thenReturn(true);
    when(productService.isProductOwner(TEST_PHONE, TEST_PRODUCT_ID)).thenReturn(true);
    when(promotionService.checkNewStartAtIsPresent(promotionDTO.getStartAt(), TEST_PRODUCT_ID))
        .thenReturn(true);
    when(productService.isLessThanOriginalPrice(TEST_AMOUNT_WITHOUT_CURRENCY, TEST_PRODUCT_ID))
        .thenReturn(true);
    when(promotionService.upsertPromotion(expectedPromotionDTO)).thenReturn(true);

    performPostAddPromotion(
        "successMessage", "Pomyślnie dodano promocję", "/products?success", promotionDTO);
  }

  private PromotionDTO setPromotionDTO() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount(TEST_AMOUNT);
    promotionDTO.setStartAt(TEST_DATE1);
    promotionDTO.setExpiredAt(TEST_DATE2);
    promotionDTO.setCount("");
    promotionDTO.setProductId(TEST_PRODUCT_ID);
    return promotionDTO;
  }

  private void performPostAddPromotion(
      String param, String message, String redirectUrl, PromotionDTO promotionDTO)
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    mockMvc
        .perform(
            post(PROMOTIONS_URL)
                .param("startAt", promotionDTO.getStartAt().toString())
                .param("expiredAt", promotionDTO.getExpiredAt().toString())
                .param("amount", promotionDTO.getAmount())
                .param("count", promotionDTO.getCount())
                .param(PRODUCT_ID_PARAM, promotionDTO.getProductId())
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
    assertEquals(message, httpSession.getAttribute(param));
  }
}
