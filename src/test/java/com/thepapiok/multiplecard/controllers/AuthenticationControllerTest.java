package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.dto.CallingCodeDTO;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.EmailService;
import com.thepapiok.multiplecard.services.SmsService;
import com.twilio.exception.ApiException;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
  private static List<CountryDTO> expectedCountries;
  private static List<CallingCodeDTO> expectedCallingCodes;
  private static List<CountryNamesDTO> expectedCountryNames;
  private static RegisterDTO expectedRegisterDTO;
  private static final String ERROR_AT_SMS_SENDING_MESSAGE = "Błąd podczas wysyłania sms";
  private static final String ERROR_AT_EMAIL_SENDING_MESSAGE = "Błąd podczas wysyłania emaila";
  private static final String ERROR_MESSAGE = "Error!";
  private static final String PL_NAME = "Polska";
  private static final String PL_CALLING_CODE = "+48";
  private static final String TEST_TEXT = "Test";
  private static final String TEST_MAIL = "Test@Test.pl";
  private static final String TEST_HOUSE_NUMBER = "1";
  private static final String TEST_POSTAL_CODE = "00-000";
  private static final String TEST_PASSWORD = "Test1!";
  private static final String LOGIN_URL = "/login";
  private static final String CALLING_CODES_PARAM = "callingCodes";
  private static final String LOGIN_PAGE = "loginPage";
  private static final String SUCCESS_PARAM = "success";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String PHONE_PARAM = "phone";
  private static final String CALLING_CODE_PARAM = "callingCode";
  private static final String ERROR_PARAM = "error";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String REGISTER_URL = "/register";
  private static final String COUNTRIES_PARAM = "countries";
  private static final String REGISTER_PARAM = "register";
  private static final String REGISTER_PAGE = "registerPage";
  private static final String TEST_CODE = "123 456";
  private static final String TEST_ENCODE_CODE = "sad8h121231z#$2";
  private static final String TEST_PHONE_NUMBER = "+1212345673123";
  private static final String VERIFICATION_MESSAGE = "Twój kod weryfikacyjny MultipleCard to: ";
  private static final String REDIRECT_VERIFICATION_ERROR = "/account_verifications?error";
  private static final String VERIFICATION_PAGE = "verificationPage";
  private static final String NEW_CODE_SMS_PARAM = "newCodeSms";
  private static final String NEW_CODE_EMAIL_PARAM = "newCodeEmail";
  private static final String VERIFICATION_NUMBER_EMAIL_PARAM = "verificationNumberEmail";
  private static final String VERIFICATION_NUMBER_SMS_PARAM = "verificationNumberSms";
  private static final String REDIRECT_LOGIN_ERROR = "/login?error";
  private static final String TEST_OTHER_CODE = "443 411";
  private static final String TEST_OTHER_ENCODE_CODE = "ssadfsadfsadf123";
  private static final String TEST_OTHER_MAIL = "test@Test";
  private static final String TEST_OTHER_PHONE_NUMBER = "+34234234234";
  private static final String VERIFICATION_URL = "/account_verifications";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  private static final String CODE_SMS_PARAM = "codeSms";
  private static final String CODE_AMOUNT_EMAIL_PARAM = "codeAmountEmail";
  private static final String CODE_EMAIL_PARAM = "codeEmail";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String REDIRECT_REGISTER_ERROR = "/register?error";

  @Autowired private MockMvc mockMvc;
  @MockBean private CountryService countryService;
  @MockBean private AuthenticationService authenticationService;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private SmsService smsService;
  @MockBean private EmailService emailService;

  @BeforeAll
  public static void setUp() {
    final String plCode = "PL";
    final String deName = "Niemcy";
    final String deCode = "DE";
    final String deCallingCode = "+49";
    final String frName = "Francja";
    final String frCode = "FR";
    final String frCallingCode = "+33";
    expectedCountries =
        List.of(
            new CountryDTO(PL_NAME, plCode, PL_CALLING_CODE),
            new CountryDTO(deName, deCode, deCallingCode),
            new CountryDTO(frName, frCode, frCallingCode));
    expectedCallingCodes =
        List.of(
            new CallingCodeDTO(PL_CALLING_CODE, plCode),
            new CallingCodeDTO(deCallingCode, deCode),
            new CallingCodeDTO(frCallingCode, frCode));
    expectedCountryNames =
        List.of(
            new CountryNamesDTO(PL_NAME, plCode),
            new CountryNamesDTO(deName, deCode),
            new CountryNamesDTO(frName, frCode));
    expectedRegisterDTO =
        new RegisterDTO(
            TEST_TEXT,
            TEST_TEXT,
            TEST_MAIL,
            TEST_TEXT,
            TEST_TEXT,
            TEST_HOUSE_NUMBER,
            null,
            TEST_POSTAL_CODE,
            TEST_TEXT,
            PL_NAME,
            PL_CALLING_CODE,
            "123456789",
            TEST_PASSWORD,
            TEST_PASSWORD);
  }

  @Test
  public void shouldReturnLoginPage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageWhenParamSuccessButNoMessage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL).param(SUCCESS_PARAM, ""))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageWhenParamSuccessWithMessage() throws Exception {
    final String succesMessage = "Sukces!";
    final String phone = "23412341234";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, succesMessage);
    httpSession.setAttribute(PHONE_PARAM, phone);
    httpSession.setAttribute(CALLING_CODE_PARAM, PL_CALLING_CODE);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL).param(SUCCESS_PARAM, "").session(httpSession))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(SUCCESS_MESSAGE_PARAM, succesMessage))
        .andExpect(model().attribute(PHONE_PARAM, phone))
        .andExpect(model().attribute(CALLING_CODE_PARAM, PL_CALLING_CODE))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageWhenParamErrorButNoMessage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, "Niepoprawny login lub hasło"))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageWhenParamErrorWithMessage() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnRegisterPage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(REGISTER_URL))
        .andExpect(model().attribute(COUNTRIES_PARAM, expectedCountryNames))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(REGISTER_PARAM, new RegisterDTO()))
        .andExpect(view().name(REGISTER_PAGE));
  }

  @Test
  public void shouldReturnRegisterPageWhenParamErrorWithMessage() throws Exception {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("12312443");
    registerDTO.setFirstName("TestA");
    registerDTO.setLastName("TestB");
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    httpSession.setAttribute(REGISTER_PARAM, registerDTO);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(REGISTER_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(COUNTRIES_PARAM, expectedCountryNames))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(REGISTER_PARAM, registerDTO))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(REGISTER_PAGE));
  }

  @Test
  public void shouldRedirectToAccountVerification() throws Exception {
    final List<String> phones = List.of(TEST_OTHER_PHONE_NUMBER);
    final List<String> emails = List.of(TEST_OTHER_MAIL);

    MockHttpSession httpSession = new MockHttpSession();
    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);

    performPostRegister(expectedRegisterDTO, httpSession, VERIFICATION_URL);
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute(REGISTER_PARAM)).toString());
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_SMS_PARAM));
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertEquals(0, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterByValidationProblem() throws Exception {
    final String errorMessage = "Podane dane są niepoprawne";
    RegisterDTO expectedRegisterDTO = new RegisterDTO();
    expectedRegisterDTO.setPhone("+12312313231");
    MockHttpSession httpSession = new MockHttpSession();

    performPostRegisterAndRedirectRegister(expectedRegisterDTO, httpSession, errorMessage);
  }

  @Test
  public void shouldRedirectToRegisterByUsersTheSameByPhone() throws Exception {
    final String errorMessage = "Użytkownik o takim numerze telefonu już istnieje";
    final List<String> phones = List.of("+48123456789", TEST_PHONE_NUMBER);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);

    performPostRegisterAndRedirectRegister(expectedRegisterDTO, httpSession, errorMessage);
  }

  @Test
  public void shouldRedirectToRegisterByUsersTheSameByEmail() throws Exception {
    final String errorMessage = "Użytkownik o takim emailu już istnieje";
    final List<String> phones = List.of(TEST_PHONE_NUMBER);
    final List<String> emails = List.of(TEST_MAIL, TEST_OTHER_MAIL);

    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);

    performPostRegisterAndRedirectRegister(expectedRegisterDTO, httpSession, errorMessage);
  }

  @Test
  public void shouldRedirectToVerificationWhenErrorAtGetVerificationSms() throws Exception {
    final List<String> phones = List.of(TEST_OTHER_PHONE_NUMBER);
    final List<String> emails = List.of(TEST_OTHER_MAIL);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(ApiException.class)
        .when(smsService)
        .sendSms(
            VERIFICATION_MESSAGE + TEST_CODE,
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone());

    performPostRegister(expectedRegisterDTO, httpSession, REDIRECT_VERIFICATION_ERROR);
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute(REGISTER_PARAM)).toString());
    assertEquals(ERROR_AT_SMS_SENDING_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToVerificationWhenErrorAtGetVerificationEmail() throws Exception {
    final List<String> phones = List.of(TEST_OTHER_PHONE_NUMBER);
    final List<String> emails = List.of(TEST_OTHER_MAIL);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(ApiException.class)
        .when(emailService)
        .sendEmail(VERIFICATION_MESSAGE + TEST_CODE, expectedRegisterDTO.getEmail());

    performPostRegister(expectedRegisterDTO, httpSession, REDIRECT_VERIFICATION_ERROR);
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute(REGISTER_PARAM)).toString());
    assertEquals(ERROR_AT_EMAIL_SENDING_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterByPasswordsNotTheSame() throws Exception {
    final String errorMessage = "Podane hasła różnią się";
    final List<String> phones = List.of(TEST_OTHER_PHONE_NUMBER);
    final List<String> emails = List.of(TEST_OTHER_MAIL);
    MockHttpSession httpSession = new MockHttpSession();
    RegisterDTO expectedRegisterDTO = new RegisterDTO();
    expectedRegisterDTO.setPhone("12312313231");
    expectedRegisterDTO.setPassword(TEST_PASSWORD);
    expectedRegisterDTO.setRetypedPassword("Werfadsf!1");
    expectedRegisterDTO.setCountry(PL_NAME);
    expectedRegisterDTO.setFirstName(TEST_TEXT);
    expectedRegisterDTO.setLastName(TEST_TEXT);
    expectedRegisterDTO.setEmail("email@email.com");
    expectedRegisterDTO.setProvince(TEST_TEXT);
    expectedRegisterDTO.setCity(TEST_TEXT);
    expectedRegisterDTO.setHouseNumber(TEST_HOUSE_NUMBER);
    expectedRegisterDTO.setCallingCode(PL_CALLING_CODE);
    expectedRegisterDTO.setPostalCode(TEST_POSTAL_CODE);
    expectedRegisterDTO.setStreet(TEST_TEXT);

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);

    performPostRegisterAndRedirectRegister(expectedRegisterDTO, httpSession, errorMessage);
  }

  private void performPostRegisterAndRedirectRegister(
      RegisterDTO expectedRegisterDTO, MockHttpSession httpSession, String errorMessage)
      throws Exception {
    performPostRegister(expectedRegisterDTO, httpSession, REDIRECT_REGISTER_ERROR);
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute(REGISTER_PARAM)).toString());
    assertEquals(errorMessage, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  private void performPostRegister(
      RegisterDTO expectedRegisterDTO, MockHttpSession httpSession, String redirectUrl)
      throws Exception {
    mockMvc
        .perform(
            post(REGISTER_URL)
                .param("firstName", expectedRegisterDTO.getFirstName())
                .param("lastName", expectedRegisterDTO.getLastName())
                .param("email", expectedRegisterDTO.getEmail())
                .param("province", expectedRegisterDTO.getProvince())
                .param("street", expectedRegisterDTO.getStreet())
                .param("houseNumber", expectedRegisterDTO.getHouseNumber())
                .param("apartmentNumber", expectedRegisterDTO.getApartmentNumber())
                .param("postalCode", expectedRegisterDTO.getPostalCode())
                .param("city", expectedRegisterDTO.getCity())
                .param("country", expectedRegisterDTO.getCountry())
                .param(CALLING_CODE_PARAM, expectedRegisterDTO.getCallingCode())
                .param(PHONE_PARAM, expectedRegisterDTO.getPhone())
                .param("password", expectedRegisterDTO.getPassword())
                .param("retypedPassword", expectedRegisterDTO.getRetypedPassword())
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  public void shouldReturnVerificationPage() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);

    mockMvc
        .perform(get(VERIFICATION_URL).session(httpSession))
        .andExpect(view().name(VERIFICATION_PAGE));
  }

  @Test
  public void shouldRedirectToLoginPageWhenNoRegisterDTOAdded() throws Exception {
    mockMvc.perform(get(VERIFICATION_URL)).andExpect(redirectedUrl(LOGIN_URL));
  }

  @Test
  public void shouldReturnVerificationPageWithParamNewCodeSms() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);

    returnVerificationWithParamCode(
        NEW_CODE_SMS_PARAM, CODE_AMOUNT_SMS_PARAM, CODE_SMS_PARAM, httpSession);
  }

  @Test
  public void shouldRedirectToVerificationPageWhenErrorAtGetVerificationSms() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(ApiException.class)
        .when(smsService)
        .sendSms(
            VERIFICATION_MESSAGE + TEST_CODE,
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone());

    redirectVerificationErrorWhenErrorAtVerification(
        NEW_CODE_SMS_PARAM, ERROR_AT_SMS_SENDING_MESSAGE, httpSession);
  }

  @Test
  public void shouldReturnVerificationPageWithParamNewCodeEmail() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 0);

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);

    returnVerificationWithParamCode(
        NEW_CODE_EMAIL_PARAM, CODE_AMOUNT_EMAIL_PARAM, CODE_EMAIL_PARAM, httpSession);
  }

  private void returnVerificationWithParamCode(
      String paramCode, String paramAmount, String paramEncode, MockHttpSession httpSession)
      throws Exception {
    mockMvc
        .perform(get(VERIFICATION_URL).param(paramCode, "").session(httpSession))
        .andExpect(view().name(VERIFICATION_PAGE));
    assertEquals(1, httpSession.getAttribute(paramAmount));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(paramEncode));
  }

  @Test
  public void shouldRedirectToVerificationPageWhenErrorAtGetVerificationEmail() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 0);

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(ApiException.class)
        .when(emailService)
        .sendEmail(VERIFICATION_MESSAGE + TEST_CODE, expectedRegisterDTO.getEmail());

    redirectVerificationErrorWhenErrorAtVerification(
        NEW_CODE_EMAIL_PARAM, ERROR_AT_EMAIL_SENDING_MESSAGE, httpSession);
  }

  private void redirectVerificationErrorWhenErrorAtVerification(
      String paramCode, String paramError, MockHttpSession httpSession) throws Exception {
    mockMvc
        .perform(get(VERIFICATION_URL).param(paramCode, "").session(httpSession))
        .andExpect(redirectedUrl(REDIRECT_VERIFICATION_ERROR));
    assertEquals(paramError, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToVerificationPageWhenTooMuchCodesSms() throws Exception {
    final int maxCodeAmount = 3;
    final String message = "Za dużo razy poprosiłeś o nowy kod sms";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, maxCodeAmount);

    redirectVerificationErrorWhenTooMuchCodes(
        CODE_AMOUNT_SMS_PARAM, NEW_CODE_SMS_PARAM, maxCodeAmount, httpSession, message);
  }

  @Test
  public void shouldRedirectToVerificationPageWhenTooMuchCodesEmail() throws Exception {
    final int maxCodeAmount = 3;
    final String message = "Za dużo razy poprosiłeś o nowy kod email";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, maxCodeAmount);

    redirectVerificationErrorWhenTooMuchCodes(
        CODE_AMOUNT_EMAIL_PARAM, NEW_CODE_EMAIL_PARAM, maxCodeAmount, httpSession, message);
  }

  private void redirectVerificationErrorWhenTooMuchCodes(
      String paramAmount,
      String paramCode,
      int maxCodeAmount,
      MockHttpSession httpSession,
      String message)
      throws Exception {
    mockMvc
        .perform(get(VERIFICATION_URL).param(paramCode, "").session(httpSession))
        .andExpect(redirectedUrl(REDIRECT_VERIFICATION_ERROR));
    assertEquals(maxCodeAmount, httpSession.getAttribute(paramAmount));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToLoginPageWithParamReset() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 0);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);

    mockMvc
        .perform(get(VERIFICATION_URL).param("reset", "").session(httpSession))
        .andExpect(redirectedUrl(LOGIN_URL));
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldReturnVerificationPageWithParamError() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    mockMvc
        .perform(get(VERIFICATION_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(VERIFICATION_PAGE));
  }

  @Test
  public void shouldRedirectToLogin() throws Exception {
    final String verificationSms = "213 555";
    final String codeSms = "s23141234sdfs";
    final String message = "Pomyślnie zarejestrowano";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM, codeSms);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(verificationSms, codeSms)).thenReturn(true);

    mockMvc
        .perform(
            post(VERIFICATION_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_CODE)
                .param(VERIFICATION_NUMBER_SMS_PARAM, verificationSms))
        .andExpect(redirectedUrl("/login?success"));
    assertEquals(expectedRegisterDTO.getPhone(), httpSession.getAttribute(PHONE_PARAM));
    assertEquals(
        expectedRegisterDTO.getCallingCode(), httpSession.getAttribute(CALLING_CODE_PARAM));
    assertEquals(message, httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToVerificationWhenTooManyAttempts() throws Exception {
    final int maxAttempts = 3;
    final String message = "Za dużo razy wpisałeś niepoprawny kod";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);

    redirectLoginErrorAndReset(httpSession, message);
  }

  @Test
  public void shouldRedirectToVerificationWhenVerificationEmailIsBad() throws Exception {
    final String message = "Nieprawidłowy kod email";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    redirectVerificationErrorWhenVerificationNumberIsBad(httpSession, message);
  }

  @Test
  public void shouldRedirectToVerificationWhenVerificationSmsIsBad() throws Exception {
    final String message = "Nieprawidłowy kod sms";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM, TEST_OTHER_ENCODE_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(false);

    redirectVerificationErrorWhenVerificationNumberIsBad(httpSession, message);
  }

  private void redirectVerificationErrorWhenVerificationNumberIsBad(
      MockHttpSession httpSession, String message) throws Exception {
    mockMvc
        .perform(
            post(VERIFICATION_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_CODE)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_OTHER_CODE))
        .andExpect(redirectedUrl(REDIRECT_VERIFICATION_ERROR));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToLoginWhenErrorAtCreateUser() throws Exception {
    final String message = "Nieoczekiwany błąd";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM, TEST_OTHER_ENCODE_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(true);
    doThrow(MongoWriteException.class).when(authenticationService).createUser(expectedRegisterDTO);

    redirectLoginErrorAndReset(httpSession, message);
  }

  private void redirectLoginErrorAndReset(MockHttpSession httpSession, String message)
      throws Exception {
    mockMvc
        .perform(
            post(VERIFICATION_URL)
                .session(httpSession)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_CODE)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_OTHER_CODE))
        .andExpect(redirectedUrl(REDIRECT_LOGIN_ERROR));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
  }
}
