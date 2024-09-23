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
import com.thepapiok.multiplecard.services.CardService;
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
  private static final String TEST_PHONE = "12312312312323";
  private static final String PHONE_PARAM = "phone";
  private static final String CARD_PARAM = "card";
  private static final String CODE_PARAM = "code";
  private static final String PIN_PARAM = "pin";
  private static final String ERROR_PARAM = "error";
  private static final String RESET_PARAM = "reset";
  private static final String RETYPED_PIN_PARAM = "retypedPin";
  private static final String NAME_PARAM = "name";
  private static final String NEW_CARD_URL = "/new_card";
  private static final String USER_ERROR_URL = "/user?error";
  private static final String USER_SUCCESS_URL = "/user?success";
  private static final String USER_URL = "/user";
  private static final String BLOCK_CARD_URL = "/block_card";
  private static final String NEW_CARD_ERROR_URL = "/new_card?error";
  private static final String BLOCK_CARD_ERROR_URL = "/block_card?error";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String ERROR_TOO_MANY_ATTEMPTS_MESSAGE =
      "Za dużo razy podałeś niepoprawne dane";
  private static final String ERROR_VALIDATION_MESSAGE = "Podane dane są niepoprawne";
  private static final String ERROR_BAD_CODE_MESSAGE = "Nieprawidłowy kod sms";
  private static final String ERROR_UNEXPECTED_MESSAGE = "Nieoczekiwany błąd";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String CODE_SMS_ORDER_PARAM = "codeSmsOrder";
  private static final String CODE_SMS_BLOCK_PARAM = "codeSmsBlock";
  private static final String VERIFICATION_NUMBER_SMS_PARAM = "verificationNumberSms";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String ORDER_PARAM = "order";
  private static final String NEW_CARD_PAGE = "newCardPage";
  private static final String BLOCK_CARD_PAGE = "blockCardPage";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  @Autowired private MockMvc mockMvc;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private CardService cardService;
  @Autowired private MessageSource messageSource;

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnNewCardPageAtNewCardPage() throws Exception {
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
    final String message = "error!";
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
    httpSession.setAttribute(ORDER_PARAM, orderCardDTO);

    mockMvc
        .perform(get(NEW_CARD_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, message))
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
        .perform(get(NEW_CARD_URL).param(RESET_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(USER_URL));
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
  public void shouldRedirectToUserSuccessAtNewCard() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_ORDER_PARAM);
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setCode(TEST_CODE);
    orderCardDTO.setName(TEST_NAME);
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setRetypedPin(TEST_PIN);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.createCard(orderCardDTO, TEST_PHONE)).thenReturn(true);

    performPostAtNewCard(httpSession, TEST_NAME, TEST_PIN, TEST_PIN, TEST_CODE, USER_SUCCESS_URL);
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
    assertEquals(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
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
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
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
    assertEquals(ERROR_BAD_CODE_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
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
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
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
                .param(NAME_PARAM, name)
                .param(PIN_PARAM, pin)
                .param(RETYPED_PIN_PARAM, retypedPin)
                .param(CODE_PARAM, code))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnBlockCardPage() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    mockMvc
        .perform(get(BLOCK_CARD_URL).session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(BLOCK_CARD_PAGE));

    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnBlockCardPageWhenParamErrorButNoMessage() throws Exception {

    mockMvc
        .perform(get(BLOCK_CARD_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(BLOCK_CARD_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnBlockCardPageWhenParamReset() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    mockMvc
        .perform(get(BLOCK_CARD_URL).param(RESET_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(USER_URL));

    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserSuccess() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.blockCard(TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(USER_SUCCESS_URL));
    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    assertEquals("Pomyślnie zastrzeżono kartę", httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorWhenTooManyAttempts() throws Exception {
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
    assertEquals(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToBlockCardErrorWhenErrorValidation() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE + "s"))
        .andExpect(redirectedUrl(BLOCK_CARD_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToBlockCardErrorWhenBadCode() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(BLOCK_CARD_ERROR_URL));
    assertEquals(ERROR_BAD_CODE_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorWhenErrorAtBlockCard() throws Exception {
    MockHttpSession httpSession = setSession(CODE_SMS_BLOCK_PARAM);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(cardService.blockCard(TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(BLOCK_CARD_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE))
        .andExpect(redirectedUrl(USER_ERROR_URL));
    checkResetSession(httpSession, CODE_SMS_BLOCK_PARAM);
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }
}
