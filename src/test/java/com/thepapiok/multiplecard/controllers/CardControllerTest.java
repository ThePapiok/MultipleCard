package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.dto.PageOwnerProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsWithShopDTO;
import com.thepapiok.multiplecard.dto.ProductAtCardDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.dto.SearchCardDTO;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.EmailService;
import com.thepapiok.multiplecard.services.PayUService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ProfileService;
import com.thepapiok.multiplecard.services.RefundService;
import com.thepapiok.multiplecard.services.ResultService;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CardControllerTest {
  private static final String ERROR_VALIDATION_MESSAGE = "Podane dane są niepoprawne";
  private static final String TEST_ENCODE_CODE = "sadfas123sdfcvcxfdf";
  private static final String CART_URL = "/cart";
  private static final String CART_PAGE = "cartPage";
  private static final String TEST_CODE = "111 222";
  private static final String TEST_PIN = "1234";
  private static final String TEST_NAME = "test";
  private static final String ID_PARAM = "id";
  private static final String PIN_PARAM = "pin";
  private static final String CARD_ID_PARAM = "cardId";
  private static final String IS_BUY_PARAM = "isBuy";
  private static final String FIELD_PARAM = "field";
  private static final String IS_DESCENDING_PARAM = "isDescending";
  private static final String COUNT_FIELD = "count";
  private static final String CARD_EXISTS_PARAM = "cardExists";
  private static final String TEST_PHONE = "12312312312323";
  private static final String TEST_CARD_ID = "523956189032345658901294";
  private static final String PHONE_PARAM = "phone";
  private static final String STEP_PARAM = "step";
  private static final String CARD_PARAM = "card";
  private static final String ERROR_PARAM = "error";
  private static final String NEW_CARD_URL = "/new_card";
  private static final String BUY_CARD_URL = "/buy_card";
  private static final String CHECK_PIN_URL = "/check_pin";
  private static final String ORDERS_ERROR_URL = "/orders?error";
  private static final String PROFILE_ERROR_URL = "/profile?error";
  private static final String CARDS_URL = "/cards";
  private static final String CHECK_CREDENTIALS_URL = "/check_credentials";
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
  private static final String TEST_SIGNATURE = "signature=132132123123";
  private static final String TEST_PAYU_ORDER_ID = "safdfasdfsads12312";
  private static final String QUOTATION_MARK_WITH_COMMA = "\",";
  private static final String TEST_ENCRYPTED_PIN = "fasasd2134132faas";
  private static final String TEST_CARD_NAME = "cardName";
  private static final String CARD_NAME_PARAM = "cardName";
  private static final String PAYU_STATUS_COMPLETED = "COMPLETED";
  private static final String TEST_LANGUAGE = "pl";
  private static final String TEST_EMAIL = "multiplecard@gmail.com";
  private static final int STATUS_OK = 200;
  @Autowired private MockMvc mockMvc;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private CardService cardService;
  @MockBean private ProductService productService;
  @MockBean private ResultService resultService;
  @MockBean private PayUService payUService;
  @MockBean private RefundService refundService;
  @MockBean private EmailService emailService;
  @MockBean private ProfileService profileService;
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
  public void shouldReturnStatusUnauthorizedAtBuyCardWhenNoPayuSend() throws Exception {
    final int unauthorizedStatus = 401;
    when(payUService.checkNotification("{bad}", TEST_SIGNATURE)).thenReturn(false);

    performPostAtBuyCard("{bad}", unauthorizedStatus);
  }

  @Test
  public void shouldReturnStatusOkAtBuyCardWhenIsRefund() throws Exception {
    final String body =
        """
                {
                  "orderId": \""""
            + TEST_PAYU_ORDER_ID
            + QUOTATION_MARK_WITH_COMMA
            + """
              "refund": {
                "status": "FINALIZED"
              }
            }

            """;
    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostAtBuyCard(body, STATUS_OK);
    verify(refundService).updateRefund(TEST_PAYU_ORDER_ID);
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusPending() throws Exception {
    final String body = setBodyForBuyCard("PENDING");

    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostAtBuyCard(body, STATUS_OK);
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCanceled() throws Exception {
    final String body = setBodyForBuyCard("CANCELED");

    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostAtBuyCard(body, STATUS_OK);
  }

  @Test
  public void
      shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedAndSuccessWithCreateCard()
          throws Exception {
    final String body = setBodyForBuyCard(PAYU_STATUS_COMPLETED);

    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);
    when(cardService.createCard(TEST_PHONE, TEST_CARD_ID, TEST_ENCRYPTED_PIN, TEST_CARD_NAME))
        .thenReturn(true);

    performPostAtBuyCard(body, STATUS_OK);
  }

  @Test
  public void
      shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedAndErrorWithCreateCardAndSuccessAtMakeRefund()
          throws Exception {
    final String body = setBodyForBuyCard(PAYU_STATUS_COMPLETED);

    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);
    when(cardService.createCard(TEST_PHONE, TEST_CARD_ID, TEST_ENCRYPTED_PIN, TEST_CARD_NAME))
        .thenReturn(false);
    when(payUService.makeRefund(TEST_PAYU_ORDER_ID)).thenReturn(true);

    performPostAtBuyCard(body, STATUS_OK);
    verify(refundService).createRefund(TEST_PAYU_ORDER_ID, TEST_LANGUAGE, TEST_EMAIL);
  }

  @Test
  public void
      shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedAndErrorWithCreateCardAndErrorAtMakeRefund()
          throws Exception {
    final String body = setBodyForBuyCard(PAYU_STATUS_COMPLETED);

    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);
    when(cardService.createCard(TEST_PHONE, TEST_CARD_ID, TEST_ENCRYPTED_PIN, TEST_CARD_NAME))
        .thenReturn(false);
    when(payUService.makeRefund(TEST_PAYU_ORDER_ID)).thenReturn(false);

    performPostAtBuyCard(body, STATUS_OK);
    verify(refundService).createRefund(TEST_PAYU_ORDER_ID, TEST_LANGUAGE, TEST_EMAIL);
    verify(emailService).sendEmail(body, TEST_EMAIL, "Błąd zwrotu - " + TEST_PAYU_ORDER_ID);
  }

  private String setBodyForBuyCard(String status) {
    return new StringBuilder(
            """
                {
                  "order": {
                    "status": \""""
                + status
                + QUOTATION_MARK_WITH_COMMA
                + """
                    "orderId": \"""")
        .append(TEST_PAYU_ORDER_ID)
        .append(QUOTATION_MARK_WITH_COMMA)
        .append("\"extOrderId\": \"")
        .append(TEST_CARD_ID)
        .append(QUOTATION_MARK_WITH_COMMA)
        .append(
            """
                                    "description": \"""")
        .append(TEST_PHONE)
        .append(QUOTATION_MARK_WITH_COMMA)
        .append(
            """
                                    "additionalDescription": "pl",
                                    "customerIp": "127.0.0.1",
                                    "buyer": {
                                      "email": "multiplecard@gmail.com"
                                    },
                                    "products": [{
                                      "name": \"""")
        .append("{\\\"encryptedPin\\\": \\\"")
        .append(TEST_ENCRYPTED_PIN)
        .append("\\\", \\\"name\\\": \\\"")
        .append(TEST_CARD_NAME)
        .append("\\\"}\",")
        .append(
            """
                                        "unitPrice" : "500",
                                        "quantity": 2
                                        }]
                                  }
                                }
                                """)
        .toString();
  }

  private void performPostAtBuyCard(String body, int status) throws Exception {

    mockMvc
        .perform(
            post(BUY_CARD_URL)
                .header("OpenPayu-Signature", TEST_SIGNATURE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().is(status));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtOrderCardWhenTooManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);

    performPostAtOrderCard(
        httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, PROFILE_ERROR_URL);
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
    assertEquals(
        "Za dużo razy podałeś niepoprawne dane", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToNewCardErrorAtOrderCardWhenErrorAtValidation() throws Exception {
    final String name = "!231";
    final String pin = "dsadas";
    OrderCardDTO expectedOrder = new OrderCardDTO();
    expectedOrder.setPin(pin);
    expectedOrder.setName(name);
    expectedOrder.setRetypedPin(pin);
    expectedOrder.setCode("");
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);

    performPostAtOrderCard(httpSession, name, pin, pin, "123sada12", NEW_CARD_ERROR_URL);
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(expectedOrder, httpSession.getAttribute(ORDER_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToNewCardErrorAtOrderCardWhenBadCode() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO expectedOrder = new OrderCardDTO();
    expectedOrder.setCode("");
    expectedOrder.setName(TEST_NAME);
    expectedOrder.setPin(TEST_PIN);
    expectedOrder.setRetypedPin(TEST_PIN);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    performPostAtOrderCard(
        httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, NEW_CARD_ERROR_URL);
    assertEquals("Nieprawidłowy kod sms", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(expectedOrder, httpSession.getAttribute(ORDER_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToNewCardErrorAtOrderCardWhenPinsNotTheSame() throws Exception {
    final String badPin = "0000";
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO expectedOrder = new OrderCardDTO();
    expectedOrder.setCode("");
    expectedOrder.setName(TEST_NAME);
    expectedOrder.setPin(TEST_PIN);
    expectedOrder.setRetypedPin(badPin);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);

    performPostAtOrderCard(httpSession, TEST_NAME, TEST_PIN, badPin, TEST_CODE, NEW_CARD_ERROR_URL);
    assertEquals("Podane PINy różnią się", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(expectedOrder, httpSession.getAttribute(ORDER_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtOrderCardWhenErrorAtOrderCard() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setCode(TEST_CODE);
    orderCardDTO.setName(TEST_NAME);
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setRetypedPin(TEST_PIN);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(payUService.cardOrder(
            anyString(),
            anyString(),
            eq(new Locale.Builder().setLanguage(TEST_LANGUAGE).build()),
            eq(TEST_PHONE),
            eq(orderCardDTO)))
        .thenReturn(null);

    performPostAtOrderCard(
        httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, PROFILE_ERROR_URL);
    assertEquals("Nieoczekiwany błąd", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserSuccessAtOrderCardWhenEverythingOk() throws Exception {
    final String redirectUrl = "payu.com";
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setCode(TEST_CODE);
    orderCardDTO.setName(TEST_NAME);
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setRetypedPin(TEST_PIN);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(payUService.cardOrder(
            anyString(),
            anyString(),
            eq(new Locale.Builder().setLanguage(TEST_LANGUAGE).build()),
            eq(TEST_PHONE),
            eq(orderCardDTO)))
        .thenReturn(redirectUrl);

    performPostAtOrderCard(httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, redirectUrl);
    assertEquals("Pomyślnie zakupiono nową kartę", httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
    checkResetSession(httpSession, CODE_SMS_ORDER_PARAM);
  }

  private void performPostAtOrderCard(
      MockHttpSession httpSession,
      String name,
      String pin,
      String retypedPin,
      String code,
      String redirectUrl)
      throws Exception {
    mockMvc
        .perform(
            post("/order_card")
                .session(httpSession)
                .locale(Locale.getDefault())
                .param("name", name)
                .param(PIN_PARAM, pin)
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
    when(cardService.isBlocked(TEST_PHONE)).thenReturn(false);

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
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
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
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
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
    when(cardService.isBlocked(TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    assertEquals("Karta jest już zastrzeżona", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtBlockCardWhenErrorAtBlockCard() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.isBlocked(TEST_PHONE)).thenReturn(false);
    when(cardService.blockCard(TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
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
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenEverythingOkAndNoOwner()
      throws Exception {
    final int size = 3;
    final PageProductsWithShopDTO page = getPageOfProductsForBuyProducts();

    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(false);

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(CARD_EXISTS_PARAM, true))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, List.of(1)))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, page.getProducts()))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(IS_BUY_PARAM, false))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenEverythingAndOwnerWithBuyParam()
      throws Exception {
    final int size = 3;
    final PageProductsWithShopDTO page = getPageOfProductsForBuyProducts();

    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(true);

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID).param("buy", ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(CARD_EXISTS_PARAM, true))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, List.of(1)))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, page.getProducts()))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(IS_BUY_PARAM, true))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnChoiceOwnerCardPageAtBuyProductsPageWhenEverythingAndOwnerWithNoParam()
      throws Exception {
    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(true);
    when(cardService.cardExists(TEST_CARD_ID)).thenReturn(true);

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(IS_BUY_PARAM, false))
        .andExpect(view().name("choiceOwnerCardPage"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldReturnChoiceOwnerCardPageAtBuyProductsPageWhenEverythingAndOwnerWithProductsParam()
          throws Exception {
    final int size = 2;
    ProductAtCardDTO product1 = new ProductAtCardDTO();
    product1.setProductName(TEST_NAME);
    ProductAtCardDTO product2 = new ProductAtCardDTO();
    product2.setProductName(TEST_NAME);
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    page.setProducts(List.of(product1, product2));
    page.setMaxPage(1);

    when(resultService.getPages(1, 1)).thenReturn(List.of(1));
    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(true);
    when(cardService.cardExists(TEST_CARD_ID)).thenReturn(true);
    when(productService.getProductsByOwnerCard(0, COUNT_FIELD, true, "", "", "", TEST_CARD_ID))
        .thenReturn(page);

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID).param("products", ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(IS_BUY_PARAM, false))
        .andExpect(model().attribute(PAGES_PARAM, List.of(1)))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(view().name("ownerProductsPage"));
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenErrorParamWithoutMessageAndNoOwner()
      throws Exception {
    final PageProductsWithShopDTO page = getPageOfProductsForBuyProducts();

    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(false);

    performPostAtBuyProductsWithParamWithoutMessage(page, "error");
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenErrorParamWithMessageAndNoOwner()
      throws Exception {
    final PageProductsWithShopDTO page = getPageOfProductsForBuyProducts();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(false);

    performPostAtBuyProductsWithParamWithMessage(page, ERROR_PARAM, ERROR_MESSAGE, httpSession);
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenSuccessParamWithoutMessageAndNoOwner()
      throws Exception {
    final PageProductsWithShopDTO page = getPageOfProductsForBuyProducts();

    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(false);

    performPostAtBuyProductsWithParamWithoutMessage(page, "success");
  }

  @Test
  public void shouldReturnBuyProductsPageAtBuyProductsPageWhenSuccessParamWithMessageAndNoOwner()
      throws Exception {
    final PageProductsWithShopDTO page = getPageOfProductsForBuyProducts();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, "success!");

    when(cardService.isOwner(TEST_PHONE, TEST_CARD_ID)).thenReturn(false);

    performPostAtBuyProductsWithParamWithMessage(page, "success", "success!", httpSession);
  }

  private void performPostAtBuyProductsWithParamWithoutMessage(
      PageProductsWithShopDTO page, String param) throws Exception {
    final int size = 3;

    mockMvc
        .perform(get(CARDS_URL).param(ID_PARAM, TEST_CARD_ID).param(param, ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(CARD_EXISTS_PARAM, true))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, List.of(1)))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, page.getProducts()))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(IS_BUY_PARAM, false))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
  }

  private void performPostAtBuyProductsWithParamWithMessage(
      PageProductsWithShopDTO page, String param, String message, MockHttpSession httpSession)
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
        .andExpect(model().attribute(PRODUCTS_PARAM, page.getProducts()))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, size == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ID_PARAM, TEST_CARD_ID))
        .andExpect(model().attribute(paramMessage, message))
        .andExpect(model().attribute(IS_BUY_PARAM, false))
        .andExpect(view().name(BUY_PRODUCTS_PAGE));
    assertNull(httpSession.getAttribute(paramMessage));
  }

  private PageProductsWithShopDTO getPageOfProductsForBuyProducts() {
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
    PageProductsWithShopDTO page = new PageProductsWithShopDTO();
    page.setProducts(List.of(productDTO1, productDTO2, productDTO3));
    page.setMaxPage(1);

    when(productService.getProductsWithShops(0, COUNT_FIELD, true, "", "", "")).thenReturn(page);
    when(resultService.getPages(1, 1)).thenReturn(List.of(1));
    when(cardService.cardExists(TEST_CARD_ID)).thenReturn(true);

    return page;
  }

  @Test
  public void shouldReturnCartPageAtCartPageWithoutPrincipal() throws Exception {
    mockMvc.perform(get(CART_URL)).andExpect(view().name(CART_PAGE));
  }

  @Test
  @WithMockUser(roles = "USER", username = TEST_PHONE)
  public void shouldReturnCartPageAtCartPageWithPrincipalRoleOtherThanUser() throws Exception {
    when(profileService.checkRole(TEST_PHONE, Role.ROLE_USER)).thenReturn(false);

    mockMvc.perform(get(CART_URL)).andExpect(view().name(CART_PAGE));
  }

  @Test
  @WithMockUser(roles = "USER", username = TEST_PHONE)
  public void shouldReturnCartPageAtCartPageWithPrincipalRoleUser() throws Exception {
    final int testPoints = 3454;

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_USER)).thenReturn(true);
    when(profileService.getPoints(TEST_PHONE)).thenReturn(testPoints);

    mockMvc
        .perform(get(CART_URL))
        .andExpect(model().attribute("points", testPoints))
        .andExpect(view().name(CART_PAGE));
  }

  @Test
  @WithMockUser(roles = "SHOP")
  public void shouldRedirectToOrdersErrorAtSearchCardWhenHasErrorAtValidation() throws Exception {
    SearchCardDTO searchCardDTO = new SearchCardDTO();
    searchCardDTO.setCardName("3123fs!a");
    searchCardDTO.setCardId("fsdafdzs12312312faffsad");
    MockHttpSession httpSession = new MockHttpSession();

    mockMvc
        .perform(
            post(CHECK_CREDENTIALS_URL)
                .param(CARD_NAME_PARAM, searchCardDTO.getCardName())
                .param(CARD_ID_PARAM, searchCardDTO.getCardId())
                .session(httpSession))
        .andExpect(redirectedUrl(ORDERS_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(roles = "SHOP")
  public void shouldRedirectToOrdersErrorAtSearchCardWhenCredentialsAreInvalid() throws Exception {
    SearchCardDTO searchCardDTO = new SearchCardDTO();
    searchCardDTO.setCardName("abcde");
    searchCardDTO.setCardId(new ObjectId().toString());
    MockHttpSession httpSession = new MockHttpSession();

    when(cardService.checkIdAndNameIsValid(searchCardDTO)).thenReturn(false);

    mockMvc
        .perform(
            post(CHECK_CREDENTIALS_URL)
                .param(CARD_NAME_PARAM, searchCardDTO.getCardName())
                .param(CARD_ID_PARAM, searchCardDTO.getCardId())
                .session(httpSession))
        .andExpect(redirectedUrl(ORDERS_ERROR_URL));
    assertEquals("Złe id lub nazwa karty", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(roles = "SHOP")
  public void shouldRedirectToOrdersAtSearchCardWhenEverythingIsOk() throws Exception {
    SearchCardDTO searchCardDTO = new SearchCardDTO();
    searchCardDTO.setCardName("abcde");
    searchCardDTO.setCardId(new ObjectId().toString());
    MockHttpSession httpSession = new MockHttpSession();

    when(cardService.checkIdAndNameIsValid(searchCardDTO)).thenReturn(true);

    mockMvc
        .perform(
            post(CHECK_CREDENTIALS_URL)
                .param(CARD_NAME_PARAM, searchCardDTO.getCardName())
                .param(CARD_ID_PARAM, searchCardDTO.getCardId())
                .session(httpSession))
        .andExpect(redirectedUrl("/orders"));
    assertEquals(searchCardDTO.getCardId(), httpSession.getAttribute(CARD_ID_PARAM));
    assertEquals(1, httpSession.getAttribute(STEP_PARAM));
  }

  @Test
  @WithMockUser(roles = "SHOP")
  public void shouldReturnOrdersErrorAtCheckPinWhenErrorAtValidation() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    mockMvc
        .perform(post(CHECK_PIN_URL).session(httpSession).param(PIN_PARAM, "asbs"))
        .andExpect(content().string(ORDERS_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(roles = "SHOP")
  public void shouldReturnOrdersErrorAtCheckPinWhenCardIsBlocked() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CARD_ID_PARAM, TEST_CARD_ID);

    when(cardService.isBlocked(new ObjectId(TEST_CARD_ID))).thenReturn(true);

    mockMvc
        .perform(post(CHECK_PIN_URL).session(httpSession).param(PIN_PARAM, TEST_PIN))
        .andExpect(content().string(ORDERS_ERROR_URL));
    assertEquals("Karta jest zablokowana", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(roles = "SHOP")
  public void shouldReturnOrdersErrorAtCheckPinWhenPinIsBad() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CARD_ID_PARAM, TEST_CARD_ID);

    when(cardService.isBlocked(new ObjectId(TEST_CARD_ID))).thenReturn(false);
    when(cardService.checkPin(TEST_CARD_ID, TEST_PIN)).thenReturn(false);

    mockMvc
        .perform(post(CHECK_PIN_URL).session(httpSession).param(PIN_PARAM, TEST_PIN))
        .andExpect(content().string(ORDERS_ERROR_URL));
    assertEquals("Niepoprawny kod PIN", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(roles = "SHOP")
  public void shouldReturnOrdersAtCheckPinWhenEverythingOk() throws Exception {
    final int testStep = 2;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CARD_ID_PARAM, TEST_CARD_ID);

    when(cardService.isBlocked(new ObjectId(TEST_CARD_ID))).thenReturn(false);
    when(cardService.checkPin(TEST_CARD_ID, TEST_PIN)).thenReturn(true);

    mockMvc
        .perform(post(CHECK_PIN_URL).session(httpSession).param(PIN_PARAM, TEST_PIN))
        .andExpect(content().string("/orders"));
    assertEquals(testStep, httpSession.getAttribute(STEP_PARAM));
  }

  @Test
  public void shouldRedirectToLandingPageAtResetCardWhenEverythingOk() throws Exception {
    final int testStep = 2;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CARD_ID_PARAM, TEST_CARD_ID);
    httpSession.setAttribute(STEP_PARAM, testStep);

    mockMvc.perform(get("/reset_card").session(httpSession)).andExpect(redirectedUrl("/"));
    assertNull(httpSession.getAttribute(CARD_ID_PARAM));
    assertNull(httpSession.getAttribute(STEP_PARAM));
  }
}
