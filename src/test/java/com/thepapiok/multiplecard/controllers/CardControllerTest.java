package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ResultService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CardControllerTest {
  private static final String TEST_ENCODE_CODE = "sadfas123sdfcvcxfdf";
  private static final String TEST_CODE = "111 222";
  private static final String TEST_PIN = "1234";
  private static final String TEST_NAME = "test";
  private static final String ID_PARAM = "id";
  private static final String FIELD_PARAM = "field";
  private static final String IS_DESCENDING_PARAM = "isDescending";
  private static final String COUNT_FIELD = "count";
  private static final String CARD_EXISTS_PARAM = "cardExists";
  private static final String TEST_PHONE = "12312312312323";
  private static final String TEST_CARD_ID = "523956189032345658901294";
  private static final String PHONE_PARAM = "phone";
  private static final String CARD_PARAM = "card";
  private static final String ERROR_PARAM = "error";
  private static final String NEW_CARD_URL = "/new_card";
  private static final String USER_ERROR_URL = "/user?error";
  private static final String CARDS_URL = "/cards";
  private static final String BLOCK_CARD_URL = "/block_card";
  private static final String NEW_CARD_ERROR_URL = "/new_card?error";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String ERROR_MESSAGE = "error!";
  private static final String CODE_SMS_ORDER_PARAM = "codeSmsOrder";
  private static final String CODE_SMS_BLOCK_PARAM = "codeSmsBlock";
  private static final String VERIFICATION_NUMBER_SMS_PARAM = "verificationNumberSms";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String ORDER_PARAM = "order";
  private static final String PAGES_PARAM = "pages";
  private static final String PAGE_SELECTED_PARAM = "pageSelected";
  private static final String PRODUCTS_PARAM = "products";
  private static final String PRODUCTS_EMPTY_PARAM = "productsEmpty";
  private static final String MAX_PAGE_PARAM = "maxPage";
  private static final String NEW_CARD_PAGE = "newCardPage";
  private static final String BUY_PRODUCTS_PAGE = "buyProductsPage";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  @Autowired private MockMvc mockMvc;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private CardService cardService;
  @MockBean private ProductService productService;
  @MockBean private ResultService resultService;
  @Autowired private MessageSource messageSource;

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnNewCardPageAtNewCardPageWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);

    mockMvc
        .perform(get(NEW_CARD_URL).session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(model().attribute(CARD_PARAM, new OrderCardDTO()))
        .andExpect(view().name(NEW_CARD_PAGE));
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnNewCardPageAtNewCardPageWhenParamErrorButNoMessage() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);

    mockMvc
        .perform(get(NEW_CARD_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(model().attribute(CARD_PARAM, new OrderCardDTO()))
        .andExpect(view().name(NEW_CARD_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnNewCardPageAtNewCardPageWhenParamErrorWithMessage() throws Exception {
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    httpSession.setAttribute(ORDER_PARAM, orderCardDTO);

    mockMvc
        .perform(get(NEW_CARD_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(model().attribute(CARD_PARAM, orderCardDTO))
        .andExpect(view().name(NEW_CARD_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnNewCardPageAtNewCardPageWhenParamReset() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);

    mockMvc
        .perform(get(NEW_CARD_URL).param("reset", "").session(httpSession))
        .andExpect(redirectedUrl("/profile"));
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
  }

  private MockHttpSession setSession(String param) {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute(param, TEST_ENCODE_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    return httpSession;
  }

  private void checkResetSession(MockHttpSession httpSession, String param) {
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(param));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserSuccessAtNewCardWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setCode(TEST_CODE);
    orderCardDTO.setName(TEST_NAME);
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setRetypedPin(TEST_PIN);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.createCard(orderCardDTO, TEST_PHONE)).thenReturn(true);

    performPostAtNewCard(httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, "/profile?success");
    assertEquals("Pomyślnie utworzono nową kartę", httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtNewCardWhenTooManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);

    performPostAtNewCard(httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, USER_ERROR_URL);
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
    assertEquals(
        "Za dużo razy podałeś niepoprawne dane", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToNewCardErrorAtNewCardWhenErrorAtValidation() throws Exception {
    final String name = "!231";
    final String pin = "dsadas";
    OrderCardDTO expectedOrder = new OrderCardDTO();
    expectedOrder.setPin(pin);
    expectedOrder.setName(name);
    expectedOrder.setRetypedPin(pin);
    expectedOrder.setCode("");
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);

    performPostAtNewCard(httpSession, name, pin, pin, "123sada12", NEW_CARD_ERROR_URL);
    assertEquals("Podane dane są niepoprawne", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(expectedOrder, httpSession.getAttribute(ORDER_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToNewCardErrorAtNewCardWhenBadCode() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO expectedOrder = new OrderCardDTO();
    expectedOrder.setCode("");
    expectedOrder.setName(TEST_NAME);
    expectedOrder.setPin(TEST_PIN);
    expectedOrder.setRetypedPin(TEST_PIN);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    performPostAtNewCard(httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, NEW_CARD_ERROR_URL);
    assertEquals("Nieprawidłowy kod sms", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(expectedOrder, httpSession.getAttribute(ORDER_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToNewCardErrorAtNewCardWhenPinsNotTheSame() throws Exception {
    final String badPin = "0000";
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO expectedOrder = new OrderCardDTO();
    expectedOrder.setCode("");
    expectedOrder.setName(TEST_NAME);
    expectedOrder.setPin(TEST_PIN);
    expectedOrder.setRetypedPin(badPin);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);

    performPostAtNewCard(httpSession, TEST_NAME, TEST_PIN, badPin, TEST_CODE, NEW_CARD_ERROR_URL);
    assertEquals("Podane PINy różnią się", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(expectedOrder, httpSession.getAttribute(ORDER_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtNewCardWhenErrorAtCreateCard() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setCode(TEST_CODE);
    orderCardDTO.setName(TEST_NAME);
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setRetypedPin(TEST_PIN);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.createCard(orderCardDTO, TEST_PHONE)).thenReturn(false);

    performPostAtNewCard(httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, USER_ERROR_URL);
    assertEquals("Nieoczekiwany błąd", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
  }

  private void performPostAtNewCard(
      MockHttpSession httpSession,
      String name,
      String pin,
      String retypedPin,
      String code,
      String redirectUrl)
      throws Exception {
    mockMvc
        .perform(
            post(NEW_CARD_URL)
                .session(httpSession)
                .param("name", name)
                .param("pin", pin)
                .param("retypedPin", retypedPin)
                .param("code", code))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnBlockCardPageAtBlockCardPageWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    mockMvc
        .perform(get(BLOCK_CARD_URL).session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name("blockCardPage"));

    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnBlockCardPageAtBlockCardPageWhenParamErrorButNoMessage()
      throws Exception {

    mockMvc
        .perform(get(BLOCK_CARD_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name("blockCardPage"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnBlockCardPageAtBlockCardPageWhenParamReset() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    mockMvc
        .perform(get(BLOCK_CARD_URL).param("reset", "").session(httpSession))
        .andExpect(redirectedUrl("/profile"));

    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserSuccessAtBlockCardWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.blockCard(TEST_PHONE)).thenReturn(true);
    when(cardService.isBlocked(TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl("/profile?success"));
    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    assertEquals("Pomyślnie zastrzeżono kartę", httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtBlockCardWhenTooManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(USER_ERROR_URL));
    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    assertEquals(
        "Za dużo razy podałeś niepoprawne dane", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToBlockCardErrorAtBlockCardWhenErrorValidation() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE + "s"))
        .andExpect(redirectedUrl("/block_card?error"));
    assertEquals("Podane dane są niepoprawne", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToBlockCardErrorAtBlockCardWhenBadCode() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl("/block_card?error"));
    assertEquals("Nieprawidłowy kod sms", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtBlockCardWhenCardIsBlocked() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.isBlocked(TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(USER_ERROR_URL));
    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    assertEquals("Karta jest już zastrzeżona", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtBlockCardWhenErrorAtBlockCard() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.isBlocked(TEST_PHONE)).thenReturn(true);
    when(cardService.blockCard(TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(USER_ERROR_URL));
    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    assertEquals("Nieoczekiwany błąd", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsWhenCardNotFound() throws Exception {
    when(cardService.cardExists(TEST_CARD_ID)).thenReturn(false);

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(CARD_EXISTS_PARAM, false))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenEverythingOk() throws Exception {
    final int size = 3;
    final List<ProductWithShopDTO> products = getProductsForBuyProducts();

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(CARD_EXISTS_PARAM, true))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, List.of(1)))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, products))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenErrorParamWithoutMessage()
      throws Exception {
    final List<ProductWithShopDTO> products = getProductsForBuyProducts();

    performPostAtBuyProductsWithParamWithoutMessage(products, "error");
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenErrorParamWithMessage()
      throws Exception {
    final List<ProductWithShopDTO> products = getProductsForBuyProducts();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    performPostAtBuyProductsWithParamWithMessage(products, ERROR_PARAM, ERROR_MESSAGE, httpSession);
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenSuccessParamWithoutMessage()
      throws Exception {
    final List<ProductWithShopDTO> products = getProductsForBuyProducts();

    performPostAtBuyProductsWithParamWithoutMessage(products, "success");
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenSuccessParamWithMessage()
      throws Exception {
    final List<ProductWithShopDTO> products = getProductsForBuyProducts();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, "success!");

    performPostAtBuyProductsWithParamWithMessage(products, "success", "success!", httpSession);
  }

  private void performPostAtBuyProductsWithParamWithoutMessage(
      List<ProductWithShopDTO> products, String param) throws Exception {
    final int size = 3;

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID).param(param, ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(CARD_EXISTS_PARAM, true))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, List.of(1)))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, products))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
  }

  private void performPostAtBuyProductsWithParamWithMessage(
      List<ProductWithShopDTO> products, String param, String message, MockHttpSession httpSession)
      throws Exception {
    final String paramMessage = param + "Message";
    final int size = 3;

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID).param(param, "").session(httpSession))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(CARD_EXISTS_PARAM, true))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, List.of(1)))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, products))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(paramMessage, message))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
    assertNull(httpSession.getAttribute(paramMessage));
  }

  private List<ProductWithShopDTO> getProductsForBuyProducts() {
    final int testPrice = 500;
    final int testQuantity = 5;
    final LocalDate testStartAt = LocalDate.now();
    final LocalDate testExpiredAt = LocalDate.now().plusYears(1);
    final String testShopImageUrl = "url";
    ProductWithShopDTO productDTO1 = new ProductWithShopDTO();
    productDTO1.setProductId("213956789315345618901231");
    productDTO1.setNewPricePromotion(testPrice);
    productDTO1.setQuantityPromotion(testQuantity);
    productDTO1.setExpiredAtPromotion(testExpiredAt);
    productDTO1.setStartAtPromotion(testStartAt);
    productDTO1.setShopName(TEST_NAME);
    productDTO1.setShopImageUrl(testShopImageUrl);
    ProductWithShopDTO productDTO2 = new ProductWithShopDTO();
    productDTO2.setProductId("213956789315345618901242");
    productDTO2.setNewPricePromotion(0);
    productDTO2.setQuantityPromotion(0);
    productDTO2.setExpiredAtPromotion(null);
    productDTO2.setStartAtPromotion(null);
    productDTO2.setShopName(TEST_NAME);
    productDTO2.setShopImageUrl(testShopImageUrl);
    ProductWithShopDTO productDTO3 = new ProductWithShopDTO();
    productDTO3.setProductId("213956789315345618901111");
    productDTO3.setNewPricePromotion(0);
    productDTO3.setQuantityPromotion(0);
    productDTO3.setExpiredAtPromotion(null);
    productDTO3.setStartAtPromotion(null);
    productDTO3.setShopName(TEST_NAME);
    productDTO3.setShopImageUrl(testShopImageUrl);
    List<ProductWithShopDTO> products = List.of(productDTO1, productDTO2, productDTO3);

    when(productService.getProductsWithShops(0, COUNT_FIELD, true, "", "", ""))
        .thenReturn(products);
    when(productService.getMaxPage("", null, "", "")).thenReturn(1);
    when(resultService.getPages(1, 1)).thenReturn(List.of(1));
    when(cardService.cardExists(TEST_CARD_ID)).thenReturn(true);

    return products;
  }

  @Test
  public void shouldReturnCartPageAtCartPageWhenEverythingOk() throws Exception {
    mockMvc
        .perform(get("/cart"))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 0))
        .andExpect(view().name("cartPage"));
  }
}
