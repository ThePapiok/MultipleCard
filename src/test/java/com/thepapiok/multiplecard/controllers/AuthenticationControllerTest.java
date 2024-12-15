package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.CallingCodeDTO;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import com.thepapiok.multiplecard.dto.ResetPasswordDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.EmailService;
import com.thepapiok.multiplecard.services.ShopService;
import com.thepapiok.multiplecard.services.SmsService;
import com.twilio.exception.ApiException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

  private static final Locale LOCALE = new Locale.Builder().setLanguage("pl").build();
  private static final String ERROR_AT_SMS_SENDING_MESSAGE = "Błąd podczas wysyłania sms";
  private static final String ERROR_AT_EMAIL_SENDING_MESSAGE = "Błąd podczas wysyłania emailu";
  private static final String ERROR_UNEXPECTED_MESSAGE = "Nieoczekiwany błąd";
  private static final String ERROR_BAD_SMS_CODE_MESSAGE = "Nieprawidłowy kod sms";
  private static final String ERROR_TOO_MANY_ATTEMPTS_MESSAGE =
      "Za dużo razy podałeś niepoprawne dane";
  private static final String ERROR_INCORRECT_DATA_MESSAGE = "Podane dane są niepoprawne";
  private static final String ERROR_MESSAGE = "Error!";
  private static final String PL_NAME = "Polska";
  private static final String PL_CALLING_CODE = "+48";
  private static final String TEST_TEXT = "Test";
  private static final String TEST_MAIL = "Test@Test.pl";
  private static final String TEST_HOUSE_NUMBER = "1";
  private static final String TEST_POSTAL_CODE = "00-000";
  private static final String TEST_PASSWORD = "Test1!";
  private static final String LOGIN_URL = "/login";
  private static final String SHOP_VERIFICATIONS_URL = "/shop_verifications";
  private static final String CALLING_CODES_PARAM = "callingCodes";
  private static final String LOGIN_PAGE = "loginPage";
  private static final String PARAM_PARAM = "param";
  private static final String NEW_USER_PARAM = "newUser";
  private static final String TEST_NEW_USER = "false";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String PHONE_PARAM = "phone";
  private static final String CALLING_CODE_PARAM = "callingCode";
  private static final String FIRST_NAME_PARAM = "firstName";
  private static final String LAST_NAME_PARAM = "lastName";
  private static final String PROVINCE_PARAM = "province";
  private static final String EMAIL_PARAM = "email";
  private static final String STREET_PARAM = "street";
  private static final String HOUSE_NUMBER_PARAM = "houseNumber";
  private static final String APARTMENT_NUMBER_PARAM = "apartmentNumber";
  private static final String CITY_PARAM = "city";
  private static final String COUNTRY_PARAM = "country";
  private static final String POSTAL_CODE_PARAM = "postalCode";
  private static final String SHOP_NAME_PARAM = "name";
  private static final String ACCOUNT_NUMBER_PARAM = "accountNumber";
  private static final String ERROR_PARAM = "error";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String REGISTER_URL = "/register";
  private static final String PASSWORD_RESET_URL = "/password_reset";
  private static final String COUNTRIES_PARAM = "countries";
  private static final String REGISTER_PARAM = "register";
  private static final String RESET_PASSWORD_PAGE = "resetPasswordPage";
  private static final String TEST_CODE = "123 456";
  private static final String TEST_ENCODE_CODE = "sad8h121231z#$2";
  private static final String TEST_APARTMENT_NUMBER = "2";
  private static final String VERIFICATION_MESSAGE = "Twój kod weryfikacyjny MultipleCard: ";
  private static final String REDIRECT_VERIFICATION_ERROR = "/account_verifications?error";
  private static final String VERIFICATION_PAGE = "verificationPage";
  private static final String FILE_PATH_PARAM = "filePath";
  private static final String VERIFICATION_NUMBER_EMAIL_PARAM = "verificationNumberEmail";
  private static final String VERIFICATION_NUMBER_SMS_PARAM = "verificationNumberSms";
  private static final String REDIRECT_LOGIN_ERROR = "/login?error";
  private static final String REDIRECT_LOGIN_SUCCESS = "/login?success";
  private static final String TEST_OTHER_CODE = "443 411";
  private static final String TEST_OTHER_ENCODE_CODE = "ssadfsadfsadf123";
  private static final String TEST_PHONE = "1231231231234";
  private static final String VERIFICATION_URL = "/account_verifications";
  private static final String VERIFICATION_SHOP_ERROR_URL = "/shop_verifications?error";
  private static final String REGISTER_SHOP_ERROR_URL = "/register_shop?error";
  private static final String REGISTER_SHOP_URL = "/register_shop";
  private static final String REGISTER_SHOP_PAGE = "registerShopPage";
  private static final String VERIFICATION_SHOP_PAGE = "verificationShopPage";
  private static final String GET_VERIFICATION_SMS_URL = "/get_verification_sms";
  private static final String GET_VERIFICATION_EMAIL_URL = "/get_verification_email";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  private static final String CODE_SMS_PARAM_REGISTER = "codeSmsRegister";
  private static final String CODE_AMOUNT_EMAIL_PARAM = "codeAmountEmail";
  private static final String CODE_EMAIL_PARAM = "codeEmail";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String PASSWORD_PARAM = "password";
  private static final String RETYPED_PASSWORD_PARAM = "retypedPassword";
  private static final String CODE_SMS_PARAM_RESET = "codeSmsReset";
  private static final String RESET_PARAM = "reset";
  private static final String IS_SENT_PARAM = "isSent";
  private static final String PARAM_ADDRESS_PREFIX0 = "address[0].";
  private static final String PARAM_ADDRESS_PREFIX1 = "address[1].";
  private static final String PARAM_ADDRESS_PREFIX = "address.";
  private static final String CODE_SMS_PARAM_REGISTER_SHOP = "codeSmsRegisterShop";
  private static List<CountryDTO> expectedCountries;
  private static List<CallingCodeDTO> expectedCallingCodes;
  private static List<CountryNamesDTO> expectedCountryNames;
  private static RegisterDTO expectedRegisterDTO;
  @Autowired private MockMvc mockMvc;
  @MockBean private CountryService countryService;
  @MockBean private AuthenticationService authenticationService;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private SmsService smsService;
  @MockBean private EmailService emailService;
  @MockBean private ShopService shopService;

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
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCountry(PL_NAME);
    addressDTO.setCity(TEST_TEXT);
    addressDTO.setStreet(TEST_TEXT);
    addressDTO.setProvince(TEST_TEXT);
    addressDTO.setApartmentNumber(null);
    addressDTO.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO.setPostalCode(TEST_POSTAL_CODE);
    expectedRegisterDTO =
        new RegisterDTO(
            TEST_TEXT,
            TEST_TEXT,
            TEST_MAIL,
            addressDTO,
            PL_CALLING_CODE,
            "123456789",
            TEST_PASSWORD,
            TEST_PASSWORD);
  }

  @Test
  public void shouldReturnLoginPageAtLoginPageWhenEverythingOk() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageAtLoginPageWhenParamSuccessButNoMessage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL).param("success", ""))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageAtLoginPageWhenParamSuccessWithMessage() throws Exception {
    final String successMessage = "Sukces!";
    final String phone = "23412341234";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, successMessage);
    httpSession.setAttribute(PHONE_PARAM, phone);
    httpSession.setAttribute(CALLING_CODE_PARAM, PL_CALLING_CODE);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL).param("success", "").session(httpSession))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(SUCCESS_MESSAGE_PARAM, successMessage))
        .andExpect(model().attribute(PHONE_PARAM, phone))
        .andExpect(model().attribute(CALLING_CODE_PARAM, PL_CALLING_CODE))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageAtLoginPageWhenParamErrorButNoMessage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(LOGIN_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(LOGIN_PAGE));
  }

  @Test
  public void shouldReturnLoginPageAtLoginPageWhenParamErrorWithMessage() throws Exception {
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
  public void shouldReturnRegisterPageAtRegisterPageWhenEverythingOk() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(REGISTER_URL))
        .andExpect(model().attribute(COUNTRIES_PARAM, expectedCountryNames))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(REGISTER_PARAM, new RegisterDTO()))
        .andExpect(view().name("registerPage"));
  }

  @Test
  public void shouldReturnRegisterPageAtRegisterPageWhenParamErrorWithMessage() throws Exception {
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
        .andExpect(view().name("registerPage"));
  }

  @Test
  public void shouldRedirectToAccountVerificationAtCreateUserWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.emailExists(expectedRegisterDTO.getEmail())).thenReturn(false);
    when(authenticationService.phoneExists(
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);

    performPostRegister(expectedRegisterDTO, httpSession, VERIFICATION_URL);
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute(REGISTER_PARAM)).toString());
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_SMS_PARAM_REGISTER));
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertEquals(0, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterAtCreateUserByValidationProblem() throws Exception {
    RegisterDTO expectedRegisterDTO = new RegisterDTO();
    expectedRegisterDTO.setPhone("+12312313231");
    expectedRegisterDTO.setAddress(new AddressDTO());
    MockHttpSession httpSession = new MockHttpSession();

    performPostRegisterAndRedirectRegister(
        expectedRegisterDTO, httpSession, ERROR_INCORRECT_DATA_MESSAGE);
  }

  @Test
  public void shouldRedirectToRegisterAtCreateUserByUsersTheSameByPhone() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .thenReturn(true);

    performPostRegisterAndRedirectRegister(
        expectedRegisterDTO, httpSession, "Użytkownik o takim numerze telefonu już istnieje");
  }

  @Test
  public void shouldRedirectToRegisterAtCreateUserByUsersTheSameByEmail() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(expectedRegisterDTO.getEmail())).thenReturn(true);

    performPostRegisterAndRedirectRegister(
        expectedRegisterDTO, httpSession, "Użytkownik o takim emailu już istnieje");
  }

  @Test
  public void shouldRedirectToVerificationAtCreateUserWhenErrorAtGenerateVerificationSms()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(expectedRegisterDTO.getEmail())).thenReturn(false);
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
  public void shouldRedirectToVerificationAtCreateUserWhenErrorAtGetVerificationEmail()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(expectedRegisterDTO.getEmail())).thenReturn(false);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(ApiException.class)
        .when(emailService)
        .sendEmail(
            VERIFICATION_MESSAGE + TEST_CODE, expectedRegisterDTO.getEmail(), "Weryfikacja konta");

    performPostRegister(expectedRegisterDTO, httpSession, REDIRECT_VERIFICATION_ERROR);
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute(REGISTER_PARAM)).toString());
    assertEquals(ERROR_AT_EMAIL_SENDING_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterAtCreateUserByPasswordsNotTheSame() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCountry(PL_NAME);
    addressDTO.setProvince(TEST_TEXT);
    addressDTO.setCity(TEST_TEXT);
    addressDTO.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO.setPostalCode(TEST_POSTAL_CODE);
    addressDTO.setStreet(TEST_TEXT);
    RegisterDTO expectedRegisterDTO = new RegisterDTO();
    expectedRegisterDTO.setPhone("12312313231");
    expectedRegisterDTO.setPassword(TEST_PASSWORD);
    expectedRegisterDTO.setRetypedPassword("Werfadsf!1");
    expectedRegisterDTO.setFirstName(TEST_TEXT);
    expectedRegisterDTO.setLastName(TEST_TEXT);
    expectedRegisterDTO.setEmail("email@email.com");
    expectedRegisterDTO.setCallingCode(PL_CALLING_CODE);
    expectedRegisterDTO.setAddress(addressDTO);

    when(authenticationService.phoneExists(
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(expectedRegisterDTO.getEmail())).thenReturn(false);

    performPostRegisterAndRedirectRegister(
        expectedRegisterDTO, httpSession, "Podane hasła różnią się");
  }

  private void performPostRegisterAndRedirectRegister(
      RegisterDTO expectedRegisterDTO, MockHttpSession httpSession, String errorMessage)
      throws Exception {
    performPostRegister(expectedRegisterDTO, httpSession, "/register?error");
    assertEquals(
        expectedRegisterDTO, Objects.requireNonNull(httpSession.getAttribute(REGISTER_PARAM)));
    assertEquals(errorMessage, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  private void performPostRegister(
      RegisterDTO expectedRegisterDTO, MockHttpSession httpSession, String redirectUrl)
      throws Exception {
    mockMvc
        .perform(
            post(REGISTER_URL)
                .param(FIRST_NAME_PARAM, expectedRegisterDTO.getFirstName())
                .param(LAST_NAME_PARAM, expectedRegisterDTO.getLastName())
                .param(EMAIL_PARAM, expectedRegisterDTO.getEmail())
                .param(
                    PARAM_ADDRESS_PREFIX + PROVINCE_PARAM,
                    expectedRegisterDTO.getAddress().getProvince())
                .param(
                    PARAM_ADDRESS_PREFIX + STREET_PARAM,
                    expectedRegisterDTO.getAddress().getStreet())
                .param(
                    PARAM_ADDRESS_PREFIX + HOUSE_NUMBER_PARAM,
                    expectedRegisterDTO.getAddress().getHouseNumber())
                .param(
                    PARAM_ADDRESS_PREFIX + APARTMENT_NUMBER_PARAM,
                    expectedRegisterDTO.getAddress().getApartmentNumber())
                .param(
                    PARAM_ADDRESS_PREFIX + POSTAL_CODE_PARAM,
                    expectedRegisterDTO.getAddress().getPostalCode())
                .param(
                    PARAM_ADDRESS_PREFIX + CITY_PARAM, expectedRegisterDTO.getAddress().getCity())
                .param(
                    PARAM_ADDRESS_PREFIX + COUNTRY_PARAM,
                    expectedRegisterDTO.getAddress().getCountry())
                .param(CALLING_CODE_PARAM, expectedRegisterDTO.getCallingCode())
                .param(PHONE_PARAM, expectedRegisterDTO.getPhone())
                .param(PASSWORD_PARAM, expectedRegisterDTO.getPassword())
                .param(RETYPED_PASSWORD_PARAM, expectedRegisterDTO.getRetypedPassword())
                .session(httpSession)
                .locale(LOCALE))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  public void shouldReturnVerificationPageAtVerificationPageWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);

    mockMvc
        .perform(get(VERIFICATION_URL).session(httpSession).locale(LOCALE))
        .andExpect(
            model()
                .attribute(
                    PHONE_PARAM,
                    expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .andExpect(model().attribute(EMAIL_PARAM, expectedRegisterDTO.getEmail()))
        .andExpect(view().name(VERIFICATION_PAGE));
  }

  @Test
  public void shouldRedirectToLoginPageAtVerificationPageWhenNoRegisterDTOAdded() throws Exception {
    mockMvc.perform(get(VERIFICATION_URL)).andExpect(redirectedUrl(LOGIN_URL));
  }

  @Test
  public void shouldRedirectToLoginPageAtVerificationPageWithParamReset() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_PARAM_REGISTER, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 0);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);

    mockMvc
        .perform(get(VERIFICATION_URL).param(RESET_PARAM, "").session(httpSession).locale(LOCALE))
        .andExpect(redirectedUrl(LOGIN_URL));
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_REGISTER));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldReturnVerificationPageAtVerificationPageWithParamErrorWithMessage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    mockMvc
        .perform(get(VERIFICATION_URL).param(ERROR_PARAM, "").session(httpSession).locale(LOCALE))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(
            model()
                .attribute(
                    PHONE_PARAM,
                    expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .andExpect(model().attribute(EMAIL_PARAM, expectedRegisterDTO.getEmail()))
        .andExpect(view().name(VERIFICATION_PAGE));
  }

  @Test
  public void shouldReturnVerificationPageAtVerificationPageWithParamErrorWithoutMessage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);

    mockMvc
        .perform(get(VERIFICATION_URL).param(ERROR_PARAM, "").session(httpSession).locale(LOCALE))
        .andExpect(
            model()
                .attribute(
                    PHONE_PARAM,
                    expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone()))
        .andExpect(model().attribute(EMAIL_PARAM, expectedRegisterDTO.getEmail()))
        .andExpect(view().name(VERIFICATION_PAGE));
  }

  @Test
  public void shouldRedirectToLoginAtVerificationWhenEverythingOk() throws Exception {
    final String verificationSms = "213 555";
    final String codeSms = "s23141234sdfs";
    final String message = "Pomyślnie zarejestrowano";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM_REGISTER, codeSms);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(authenticationService.createUser(expectedRegisterDTO)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(verificationSms, codeSms)).thenReturn(true);

    mockMvc
        .perform(
            post(VERIFICATION_URL)
                .session(httpSession)
                .locale(LOCALE)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_CODE)
                .param(VERIFICATION_NUMBER_SMS_PARAM, verificationSms))
        .andExpect(redirectedUrl(REDIRECT_LOGIN_SUCCESS));
    assertEquals(expectedRegisterDTO.getPhone(), httpSession.getAttribute(PHONE_PARAM));
    assertEquals(
        expectedRegisterDTO.getCallingCode(), httpSession.getAttribute(CALLING_CODE_PARAM));
    assertEquals(message, httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_REGISTER));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToVerificationAtVerificationWhenTooManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);

    redirectLoginErrorAndReset(httpSession, ERROR_TOO_MANY_ATTEMPTS_MESSAGE);
  }

  @Test
  public void shouldRedirectToVerificationAtVerificationWhenVerificationEmailIsBad()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    redirectVerificationErrorWhenVerificationNumberIsBad(httpSession, "Nieprawidłowy kod email");
  }

  @Test
  public void shouldRedirectToVerificationAtVerificationWhenVerificationSmsIsBad()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM_REGISTER, TEST_OTHER_ENCODE_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(false);

    redirectVerificationErrorWhenVerificationNumberIsBad(httpSession, ERROR_BAD_SMS_CODE_MESSAGE);
  }

  private void redirectVerificationErrorWhenVerificationNumberIsBad(
      MockHttpSession httpSession, String message) throws Exception {
    mockMvc
        .perform(
            post(VERIFICATION_URL)
                .session(httpSession)
                .locale(LOCALE)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_CODE)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_OTHER_CODE))
        .andExpect(redirectedUrl(REDIRECT_VERIFICATION_ERROR));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToLoginAtVerificationWhenErrorAtCreateUser() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, expectedRegisterDTO);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM_REGISTER, TEST_OTHER_ENCODE_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.createUser(expectedRegisterDTO)).thenReturn(false);

    redirectLoginErrorAndReset(httpSession, ERROR_UNEXPECTED_MESSAGE);
  }

  private void redirectLoginErrorAndReset(MockHttpSession httpSession, String message)
      throws Exception {
    mockMvc
        .perform(
            post(VERIFICATION_URL)
                .session(httpSession)
                .locale(LOCALE)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_CODE)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_OTHER_CODE))
        .andExpect(redirectedUrl(REDIRECT_LOGIN_ERROR));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_REGISTER));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldReturnPasswordResetPageAtPasswordResetPageWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSessionAtResetPasswordPage(httpSession);

    mockMvc
        .perform(get(PASSWORD_RESET_URL).session(httpSession))
        .andExpect(model().attribute(RESET_PARAM, new ResetPasswordDTO()))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(RESET_PARAM, new ResetPasswordDTO()))
        .andExpect(view().name(RESET_PASSWORD_PAGE));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(RESET_PARAM));
  }

  @Test
  public void shouldReturnPasswordResetPageAtPasswordResetPageWhenErrorParamButNoMessage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSessionAtResetPasswordPage(httpSession);

    mockMvc
        .perform(get(PASSWORD_RESET_URL).session(httpSession).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(RESET_PARAM, new ResetPasswordDTO()))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(RESET_PASSWORD_PAGE));
  }

  @Test
  public void shouldReturnPasswordResetPageAtPasswordResetPageWhenErrorParamWithMessage()
      throws Exception {
    final int codeAmount = 2;
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    setSessionAtResetPasswordPage(httpSession);
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    httpSession.setAttribute(RESET_PARAM, resetPasswordDTO);

    mockMvc
        .perform(get(PASSWORD_RESET_URL).session(httpSession).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(RESET_PARAM, new ResetPasswordDTO()))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(ERROR_PARAM, ERROR_MESSAGE))
        .andExpect(model().attribute(IS_SENT_PARAM, true))
        .andExpect(model().attribute(RESET_PARAM, resetPasswordDTO))
        .andExpect(view().name(RESET_PASSWORD_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertTrue((Boolean) httpSession.getAttribute(IS_SENT_PARAM));
    assertEquals(codeAmount, httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(RESET_PARAM));
  }

  @Test
  public void shouldRedirectToLoginAtPasswordResetPageWithParamReset() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSessionAtResetPasswordPage(httpSession);

    mockMvc
        .perform(get(PASSWORD_RESET_URL).session(httpSession).param(RESET_PARAM, ""))
        .andExpect(redirectedUrl(LOGIN_URL));
  }

  private void setSessionAtResetPasswordPage(MockHttpSession httpSession) {
    final int codeAmount = 2;
    httpSession.setAttribute(CODE_SMS_PARAM_RESET, TEST_CODE);
    httpSession.setAttribute(IS_SENT_PARAM, true);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, codeAmount);

    when(countryService.getAll()).thenReturn(expectedCountries);
  }

  @Test
  public void shouldRedirectToLoginSuccessAtResetPasswordWhenEverythingOk() throws Exception {
    final String fullPhone = PL_CALLING_CODE + TEST_PHONE;
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    setResetPasswordAndSession(resetPasswordDTO, httpSession, TEST_PASSWORD, 1);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.getAccountByPhone(fullPhone)).thenReturn(true);
    when(authenticationService.changePassword(fullPhone, TEST_PASSWORD)).thenReturn(true);

    redirectAndResetPasswordReset(
        "Pomyślnie zresetowano hasło",
        SUCCESS_MESSAGE_PARAM,
        httpSession,
        resetPasswordDTO,
        REDIRECT_LOGIN_SUCCESS);
  }

  @Test
  public void shouldRedirectPasswordResetErrorAtResetPasswordWhenErrorAtValidation()
      throws Exception {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    setResetPasswordAndSession(new ResetPasswordDTO(), httpSession, TEST_PASSWORD, 1);

    redirectPasswordResetError(resetPasswordDTO, httpSession, ERROR_INCORRECT_DATA_MESSAGE);
  }

  @Test
  public void shouldRedirectToLoginErrorAtResetPasswordWhenTooManyAttempts() throws Exception {
    final int maxCodeAmount = 3;
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    setResetPasswordAndSession(resetPasswordDTO, httpSession, TEST_PASSWORD, maxCodeAmount);

    redirectAndResetPasswordReset(
        ERROR_TOO_MANY_ATTEMPTS_MESSAGE,
        ERROR_MESSAGE_PARAM,
        httpSession,
        resetPasswordDTO,
        REDIRECT_LOGIN_ERROR);
  }

  @Test
  public void shouldRedirectToPasswordResetErrorAtResetPasswordWhenBadCode() throws Exception {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    setResetPasswordAndSession(resetPasswordDTO, httpSession, TEST_PASSWORD, 1);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    redirectPasswordResetError(resetPasswordDTO, httpSession, ERROR_BAD_SMS_CODE_MESSAGE);
  }

  @Test
  public void shouldRedirectToPasswordResetErrorAtResetPasswordWhenNotTheSamePasswords()
      throws Exception {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    setResetPasswordAndSession(resetPasswordDTO, httpSession, TEST_PASSWORD + "1123", 1);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);

    redirectPasswordResetError(resetPasswordDTO, httpSession, "Podane hasła różnią się");
  }

  @Test
  public void shouldRedirectToPasswordResetErrorAtResetPasswordWhenUserNotFound() throws Exception {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    setResetPasswordAndSession(resetPasswordDTO, httpSession, TEST_PASSWORD, 1);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.getAccountByPhone(PL_CALLING_CODE + TEST_PHONE)).thenReturn(false);

    redirectPasswordResetError(resetPasswordDTO, httpSession, "Nie ma takiego użytkownika");
  }

  private void redirectPasswordResetError(
      ResetPasswordDTO resetPasswordDTO, MockHttpSession httpSession, String message)
      throws Exception {
    final int amount = 2;
    mockMvc
        .perform(
            post(PASSWORD_RESET_URL)
                .param(PHONE_PARAM, resetPasswordDTO.getPhone())
                .param(CALLING_CODE_PARAM, resetPasswordDTO.getCallingCode())
                .param(PASSWORD_PARAM, resetPasswordDTO.getPassword())
                .param(RETYPED_PASSWORD_PARAM, resetPasswordDTO.getRetypedPassword())
                .param("code", resetPasswordDTO.getCode())
                .session(httpSession))
        .andExpect(redirectedUrl("/password_reset?error"));
    assertEquals(amount, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(resetPasswordDTO, httpSession.getAttribute(RESET_PARAM));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToLoginErrorAtResetPasswordWhenErrorAtChangePassword()
      throws Exception {
    final String fullPhone = PL_CALLING_CODE + TEST_PHONE;
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    MockHttpSession httpSession = new MockHttpSession();
    setResetPasswordAndSession(resetPasswordDTO, httpSession, TEST_PASSWORD, 1);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.getAccountByPhone(fullPhone)).thenReturn(true);
    when(authenticationService.changePassword(fullPhone, TEST_PASSWORD)).thenReturn(false);

    redirectAndResetPasswordReset(
        ERROR_UNEXPECTED_MESSAGE,
        ERROR_MESSAGE_PARAM,
        httpSession,
        resetPasswordDTO,
        REDIRECT_LOGIN_ERROR);
  }

  private void redirectAndResetPasswordReset(
      String message,
      String param,
      MockHttpSession httpSession,
      ResetPasswordDTO resetPasswordDTO,
      String redirectUrl)
      throws Exception {
    mockMvc
        .perform(
            post(PASSWORD_RESET_URL)
                .param(PHONE_PARAM, resetPasswordDTO.getPhone())
                .param(CALLING_CODE_PARAM, resetPasswordDTO.getCallingCode())
                .param(PASSWORD_PARAM, resetPasswordDTO.getPassword())
                .param(RETYPED_PASSWORD_PARAM, resetPasswordDTO.getRetypedPassword())
                .param("code", resetPasswordDTO.getCode())
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_RESET));
    assertNull(httpSession.getAttribute(RESET_PARAM));
    assertEquals(message, httpSession.getAttribute(param));
  }

  private void setResetPasswordAndSession(
      ResetPasswordDTO resetPasswordDTO, MockHttpSession httpSession, String password, int amount) {
    resetPasswordDTO.setPhone(TEST_PHONE);
    resetPasswordDTO.setCallingCode(PL_CALLING_CODE);
    resetPasswordDTO.setPassword(TEST_PASSWORD);
    resetPasswordDTO.setRetypedPassword(password);
    resetPasswordDTO.setCode(TEST_CODE);
    httpSession.setAttribute(ATTEMPTS_PARAM, amount);
    httpSession.setAttribute(CODE_SMS_PARAM_RESET, TEST_ENCODE_CODE);
    httpSession.setAttribute(IS_SENT_PARAM, true);
    httpSession.setAttribute(RESET_PARAM, resetPasswordDTO);
  }

  @Test
  public void shouldReturnOkAtGetVerificationSmsWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);
    when(authenticationService.getAccountByPhone(PL_CALLING_CODE + TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(GET_VERIFICATION_SMS_URL)
                .session(httpSession)
                .param(PHONE_PARAM, PL_CALLING_CODE + TEST_PHONE)
                .param(PARAM_PARAM, CODE_SMS_PARAM_RESET)
                .param(NEW_USER_PARAM, TEST_NEW_USER))
        .andExpect(content().string("ok"));
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_SMS_PARAM_RESET));
  }

  @Test
  public void shouldReturnTooManyAttemptsMessageAtGetVerificationSmsWhenTooManyAttempts()
      throws Exception {
    final int maxAmount = 3;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, maxAmount);

    mockMvc
        .perform(
            post(GET_VERIFICATION_SMS_URL)
                .session(httpSession)
                .param(PHONE_PARAM, PL_CALLING_CODE + TEST_PHONE)
                .param(PARAM_PARAM, CODE_SMS_PARAM_RESET)
                .param(NEW_USER_PARAM, TEST_NEW_USER))
        .andExpect(content().string("Za dużo razy poprosiłeś o nowy kod sms"));
  }

  @Test
  public void shouldReturnValidationMessageAtGetVerificationSmsWhenGetErrorValidation()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    mockMvc
        .perform(
            post(GET_VERIFICATION_SMS_URL)
                .param(PHONE_PARAM, "")
                .param(PARAM_PARAM, "")
                .param(NEW_USER_PARAM, TEST_NEW_USER)
                .session(httpSession))
        .andExpect(content().string(ERROR_INCORRECT_DATA_MESSAGE));
  }

  @Test
  public void shouldReturnUserNotFoundMessageAtGetVerificationSmsWhenUserNotFound()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    when(authenticationService.getAccountByPhone(PL_CALLING_CODE + TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(GET_VERIFICATION_SMS_URL)
                .param(PHONE_PARAM, PL_CALLING_CODE + TEST_PHONE)
                .param(PARAM_PARAM, CODE_SMS_PARAM_RESET)
                .param(NEW_USER_PARAM, TEST_NEW_USER)
                .session(httpSession))
        .andExpect(content().string("Nie ma takiego użytkownika"));
  }

  @Test
  public void shouldReturnSendSmsErrorMessageAtGetVerificationSmsWhenErrorAtSendSms()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(authenticationService.getAccountByPhone(PL_CALLING_CODE + TEST_PHONE)).thenReturn(true);
    doThrow(ApiException.class)
        .when(smsService)
        .sendSms(VERIFICATION_MESSAGE + TEST_CODE, PL_CALLING_CODE + TEST_PHONE);

    mockMvc
        .perform(
            post(GET_VERIFICATION_SMS_URL)
                .session(httpSession)
                .param(PHONE_PARAM, PL_CALLING_CODE + TEST_PHONE)
                .param(PARAM_PARAM, CODE_SMS_PARAM_RESET)
                .param(NEW_USER_PARAM, TEST_NEW_USER))
        .andExpect(content().string(ERROR_AT_SMS_SENDING_MESSAGE));
  }

  @Test
  public void shouldReturnTooManyAttemptsMessageAtGetVerificationEmailWhenTooManyAttempts()
      throws Exception {
    final int maxAmount = 3;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, maxAmount);

    mockMvc
        .perform(
            post(GET_VERIFICATION_EMAIL_URL).session(httpSession).param(EMAIL_PARAM, TEST_MAIL))
        .andExpect(content().string("Za dużo razy poprosiłeś o nowy kod email"));
  }

  @Test
  public void shouldReturnValidationMessageAtGetVerificationEmailWhenErrorAtValidation()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 0);

    mockMvc
        .perform(post(GET_VERIFICATION_EMAIL_URL).session(httpSession).param(EMAIL_PARAM, "a"))
        .andExpect(content().string(ERROR_INCORRECT_DATA_MESSAGE));
  }

  @Test
  public void shouldReturnSendEmailErrorMessageAtGetVerificationEmailWhenErrorAtSendingEmail()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 0);

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(ApiException.class)
        .when(emailService)
        .sendEmail(eq(VERIFICATION_MESSAGE + TEST_CODE), eq(TEST_MAIL), any());

    mockMvc
        .perform(
            post(GET_VERIFICATION_EMAIL_URL).session(httpSession).param(EMAIL_PARAM, TEST_MAIL))
        .andExpect(content().string(ERROR_AT_EMAIL_SENDING_MESSAGE));
  }

  @Test
  public void shouldReturnOkMessageAtGetVerificationEmailWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 0);

    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);

    mockMvc
        .perform(
            post(GET_VERIFICATION_EMAIL_URL).session(httpSession).param(EMAIL_PARAM, TEST_MAIL))
        .andExpect(content().string("ok"));
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_EMAIL_PARAM));
  }

  @Test
  public void shouldReturnRegisterShopPageAtRegisterShopPageWhenEverythingOk() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(REGISTER_SHOP_URL))
        .andExpect(model().attribute(REGISTER_PARAM, new RegisterShopDTO()))
        .andExpect(model().attribute(COUNTRIES_PARAM, expectedCountryNames))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(REGISTER_SHOP_PAGE));
  }

  @Test
  public void shouldReturnRegisterShopPageAtRegisterShopPageWhenParamErrorWithoutMessage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(REGISTER_SHOP_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(REGISTER_PARAM, new RegisterShopDTO()))
        .andExpect(model().attribute(COUNTRIES_PARAM, expectedCountryNames))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(view().name(REGISTER_SHOP_PAGE));
  }

  @Test
  public void shouldReturnRegisterShopPageAtRegisterShopPageWhenParamErrorWithMessage()
      throws Exception {
    RegisterShopDTO registerShopDTO = new RegisterShopDTO();
    registerShopDTO.setName(SHOP_NAME_PARAM);
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    httpSession.setAttribute(REGISTER_PARAM, registerShopDTO);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get(REGISTER_SHOP_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(REGISTER_PARAM, registerShopDTO))
        .andExpect(model().attribute(COUNTRIES_PARAM, expectedCountryNames))
        .andExpect(model().attribute(CALLING_CODES_PARAM, expectedCallingCodes))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(REGISTER_SHOP_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
  }

  @Test
  public void shouldRedirectToShopVerificationsAtRegisterShopWhenEverythingOk() throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkPointsExists(registerShopDTO.getAddress(), null)).thenReturn(false);
    when(shopService.checkImage(registerShopDTO.getFile())).thenReturn(true);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);
    when(shopService.saveTempFile(registerShopDTO.getFile())).thenReturn(FILE_PATH_PARAM);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, SHOP_VERIFICATIONS_URL);
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertEquals(1, httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertEquals(0, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertEquals(TEST_ENCODE_CODE, httpSession.getAttribute(CODE_SMS_PARAM_REGISTER_SHOP));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenErrorAtValidation()
      throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    registerShopDTO.setFirstName(registerShopDTO.getFirstName() + "!");
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals(ERROR_INCORRECT_DATA_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenPhoneExists() throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(true);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals(
        "Użytkownik o takim numerze telefonu już istnieje",
        httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenEmailExists() throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(true);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals(
        "Użytkownik o takim emailu już istnieje", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenShopNameExists() throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(true);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals("Taka nazwa lokalu już istnieje", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenAccountNumberExists()
      throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(true);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals(
        "Taki numer rachunku bankowego już istnieje",
        httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenAccountNumberBad()
      throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(false);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals("Zły numer rachunku bankowego", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenBadSize() throws Exception {
    final String prefixAddress2 = "address[2].";
    final String prefixAddress3 = "address[3].";
    final String prefixAddress4 = "address[4].";
    final String prefixAddress5 = "address[5].";
    final String prefixAddress6 = "address[6].";
    AddressDTO addressDTO0 = new AddressDTO();
    addressDTO0.setCountry(TEST_TEXT);
    addressDTO0.setCity(TEST_TEXT);
    addressDTO0.setApartmentNumber(TEST_APARTMENT_NUMBER);
    addressDTO0.setStreet(TEST_TEXT);
    addressDTO0.setProvince(TEST_TEXT);
    addressDTO0.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO0.setPostalCode(TEST_POSTAL_CODE);
    AddressDTO addressDTO1 = new AddressDTO();
    addressDTO1.setCountry(TEST_TEXT);
    addressDTO1.setCity(TEST_TEXT);
    addressDTO1.setApartmentNumber("3");
    addressDTO1.setStreet(TEST_TEXT);
    addressDTO1.setProvince(TEST_TEXT);
    addressDTO1.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO1.setPostalCode(TEST_POSTAL_CODE);
    AddressDTO addressDTO2 = new AddressDTO();
    addressDTO2.setCountry(TEST_TEXT);
    addressDTO2.setCity(TEST_TEXT);
    addressDTO2.setApartmentNumber("4");
    addressDTO2.setStreet(TEST_TEXT);
    addressDTO2.setProvince(TEST_TEXT);
    addressDTO2.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO2.setPostalCode(TEST_POSTAL_CODE);
    AddressDTO addressDTO3 = new AddressDTO();
    addressDTO3.setCountry(TEST_TEXT);
    addressDTO3.setCity(TEST_TEXT);
    addressDTO3.setApartmentNumber("5");
    addressDTO3.setStreet(TEST_TEXT);
    addressDTO3.setProvince(TEST_TEXT);
    addressDTO3.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO3.setPostalCode(TEST_POSTAL_CODE);
    AddressDTO addressDTO4 = new AddressDTO();
    addressDTO4.setCountry(TEST_TEXT);
    addressDTO4.setCity(TEST_TEXT);
    addressDTO4.setApartmentNumber("6");
    addressDTO4.setStreet(TEST_TEXT);
    addressDTO4.setProvince(TEST_TEXT);
    addressDTO4.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO4.setPostalCode(TEST_POSTAL_CODE);
    AddressDTO addressDTO5 = new AddressDTO();
    addressDTO5.setCountry(TEST_TEXT);
    addressDTO5.setCity(TEST_TEXT);
    addressDTO5.setApartmentNumber("7");
    addressDTO5.setStreet(TEST_TEXT);
    addressDTO5.setProvince(TEST_TEXT);
    addressDTO5.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO5.setPostalCode(TEST_POSTAL_CODE);
    AddressDTO addressDTO6 = new AddressDTO();
    addressDTO6.setCountry(TEST_TEXT);
    addressDTO6.setCity(TEST_TEXT);
    addressDTO6.setApartmentNumber("8");
    addressDTO6.setStreet(TEST_TEXT);
    addressDTO6.setProvince(TEST_TEXT);
    addressDTO6.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO6.setPostalCode(TEST_POSTAL_CODE);
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    registerShopDTO.setAddress(
        List.of(
            addressDTO0,
            addressDTO1,
            addressDTO2,
            addressDTO3,
            addressDTO4,
            addressDTO5,
            addressDTO6));
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);

    mockMvc
        .perform(
            multipart(REGISTER_SHOP_URL)
                .file((MockMultipartFile) registerShopDTO.getFile())
                .session(httpSession)
                .param(FIRST_NAME_PARAM, registerShopDTO.getFirstName())
                .param(LAST_NAME_PARAM, registerShopDTO.getLastName())
                .param(EMAIL_PARAM, registerShopDTO.getEmail())
                .param(CALLING_CODE_PARAM, registerShopDTO.getCallingCode())
                .param(PHONE_PARAM, registerShopDTO.getPhone())
                .param(PASSWORD_PARAM, registerShopDTO.getPassword())
                .param(RETYPED_PASSWORD_PARAM, registerShopDTO.getRetypedPassword())
                .param(SHOP_NAME_PARAM, registerShopDTO.getName())
                .param(ACCOUNT_NUMBER_PARAM, registerShopDTO.getAccountNumber())
                .param(PARAM_ADDRESS_PREFIX0 + COUNTRY_PARAM, addressDTO0.getCountry())
                .param(PARAM_ADDRESS_PREFIX0 + CITY_PARAM, addressDTO0.getCity())
                .param(PARAM_ADDRESS_PREFIX0 + POSTAL_CODE_PARAM, addressDTO0.getPostalCode())
                .param(PARAM_ADDRESS_PREFIX0 + STREET_PARAM, addressDTO0.getStreet())
                .param(PARAM_ADDRESS_PREFIX0 + HOUSE_NUMBER_PARAM, addressDTO0.getHouseNumber())
                .param(
                    PARAM_ADDRESS_PREFIX0 + APARTMENT_NUMBER_PARAM,
                    addressDTO0.getApartmentNumber())
                .param(PARAM_ADDRESS_PREFIX0 + PROVINCE_PARAM, addressDTO0.getProvince())
                .param(PARAM_ADDRESS_PREFIX1 + COUNTRY_PARAM, addressDTO1.getCountry())
                .param(PARAM_ADDRESS_PREFIX1 + CITY_PARAM, addressDTO1.getCity())
                .param(PARAM_ADDRESS_PREFIX1 + POSTAL_CODE_PARAM, addressDTO1.getPostalCode())
                .param(PARAM_ADDRESS_PREFIX1 + STREET_PARAM, addressDTO1.getStreet())
                .param(PARAM_ADDRESS_PREFIX1 + HOUSE_NUMBER_PARAM, addressDTO1.getHouseNumber())
                .param(
                    PARAM_ADDRESS_PREFIX1 + APARTMENT_NUMBER_PARAM,
                    addressDTO1.getApartmentNumber())
                .param(PARAM_ADDRESS_PREFIX1 + PROVINCE_PARAM, addressDTO1.getProvince())
                .param(prefixAddress2 + COUNTRY_PARAM, addressDTO2.getCountry())
                .param(prefixAddress2 + CITY_PARAM, addressDTO2.getCity())
                .param(prefixAddress2 + POSTAL_CODE_PARAM, addressDTO2.getPostalCode())
                .param(prefixAddress2 + STREET_PARAM, addressDTO2.getStreet())
                .param(prefixAddress2 + HOUSE_NUMBER_PARAM, addressDTO2.getHouseNumber())
                .param(prefixAddress2 + APARTMENT_NUMBER_PARAM, addressDTO2.getApartmentNumber())
                .param(prefixAddress2 + PROVINCE_PARAM, addressDTO2.getProvince())
                .param(prefixAddress3 + COUNTRY_PARAM, addressDTO3.getCountry())
                .param(prefixAddress3 + CITY_PARAM, addressDTO3.getCity())
                .param(prefixAddress3 + POSTAL_CODE_PARAM, addressDTO3.getPostalCode())
                .param(prefixAddress3 + STREET_PARAM, addressDTO3.getStreet())
                .param(prefixAddress3 + HOUSE_NUMBER_PARAM, addressDTO3.getHouseNumber())
                .param(prefixAddress3 + APARTMENT_NUMBER_PARAM, addressDTO3.getApartmentNumber())
                .param(prefixAddress3 + PROVINCE_PARAM, addressDTO3.getProvince())
                .param(prefixAddress4 + COUNTRY_PARAM, addressDTO4.getCountry())
                .param(prefixAddress4 + CITY_PARAM, addressDTO4.getCity())
                .param(prefixAddress4 + POSTAL_CODE_PARAM, addressDTO4.getPostalCode())
                .param(prefixAddress4 + STREET_PARAM, addressDTO4.getStreet())
                .param(prefixAddress4 + HOUSE_NUMBER_PARAM, addressDTO4.getHouseNumber())
                .param(prefixAddress4 + APARTMENT_NUMBER_PARAM, addressDTO4.getApartmentNumber())
                .param(prefixAddress4 + PROVINCE_PARAM, addressDTO4.getProvince())
                .param(prefixAddress5 + COUNTRY_PARAM, addressDTO5.getCountry())
                .param(prefixAddress5 + CITY_PARAM, addressDTO5.getCity())
                .param(prefixAddress5 + POSTAL_CODE_PARAM, addressDTO5.getPostalCode())
                .param(prefixAddress5 + STREET_PARAM, addressDTO5.getStreet())
                .param(prefixAddress5 + HOUSE_NUMBER_PARAM, addressDTO5.getHouseNumber())
                .param(prefixAddress5 + APARTMENT_NUMBER_PARAM, addressDTO5.getApartmentNumber())
                .param(prefixAddress5 + PROVINCE_PARAM, addressDTO5.getProvince())
                .param(prefixAddress6 + COUNTRY_PARAM, addressDTO6.getCountry())
                .param(prefixAddress6 + CITY_PARAM, addressDTO6.getCity())
                .param(prefixAddress6 + POSTAL_CODE_PARAM, addressDTO6.getPostalCode())
                .param(prefixAddress6 + STREET_PARAM, addressDTO6.getStreet())
                .param(prefixAddress6 + HOUSE_NUMBER_PARAM, addressDTO6.getHouseNumber())
                .param(prefixAddress6 + APARTMENT_NUMBER_PARAM, addressDTO6.getApartmentNumber())
                .param(prefixAddress6 + PROVINCE_PARAM, addressDTO6.getProvince()))
        .andExpect(redirectedUrl(REGISTER_SHOP_ERROR_URL));
    assertEquals("Nieprawidłowa ilość lokali", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(registerShopDTO, httpSession.getAttribute(REGISTER_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenNotUnique() throws Exception {
    AddressDTO addressDTO0 = new AddressDTO();
    addressDTO0.setCountry(TEST_TEXT);
    addressDTO0.setCity(TEST_TEXT);
    addressDTO0.setApartmentNumber(TEST_APARTMENT_NUMBER);
    addressDTO0.setStreet(TEST_TEXT);
    addressDTO0.setProvince(TEST_TEXT);
    addressDTO0.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO0.setPostalCode(TEST_POSTAL_CODE);
    AddressDTO addressDTO1 = new AddressDTO();
    addressDTO1.setCountry(TEST_TEXT);
    addressDTO1.setCity(TEST_TEXT);
    addressDTO1.setApartmentNumber(TEST_APARTMENT_NUMBER);
    addressDTO1.setStreet(TEST_TEXT);
    addressDTO1.setProvince(TEST_TEXT);
    addressDTO1.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO1.setPostalCode(TEST_POSTAL_CODE);
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    registerShopDTO.setAddress(List.of(addressDTO0, addressDTO1));
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);

    mockMvc
        .perform(
            multipart(REGISTER_SHOP_URL)
                .file((MockMultipartFile) registerShopDTO.getFile())
                .session(httpSession)
                .param(FIRST_NAME_PARAM, registerShopDTO.getFirstName())
                .param(LAST_NAME_PARAM, registerShopDTO.getLastName())
                .param(EMAIL_PARAM, registerShopDTO.getEmail())
                .param(CALLING_CODE_PARAM, registerShopDTO.getCallingCode())
                .param(PHONE_PARAM, registerShopDTO.getPhone())
                .param(PASSWORD_PARAM, registerShopDTO.getPassword())
                .param(RETYPED_PASSWORD_PARAM, registerShopDTO.getRetypedPassword())
                .param(SHOP_NAME_PARAM, registerShopDTO.getName())
                .param(ACCOUNT_NUMBER_PARAM, registerShopDTO.getAccountNumber())
                .param(PARAM_ADDRESS_PREFIX0 + COUNTRY_PARAM, addressDTO0.getCountry())
                .param(PARAM_ADDRESS_PREFIX0 + CITY_PARAM, addressDTO0.getCity())
                .param(PARAM_ADDRESS_PREFIX0 + POSTAL_CODE_PARAM, addressDTO0.getPostalCode())
                .param(PARAM_ADDRESS_PREFIX0 + STREET_PARAM, addressDTO0.getStreet())
                .param(PARAM_ADDRESS_PREFIX0 + HOUSE_NUMBER_PARAM, addressDTO0.getHouseNumber())
                .param(
                    PARAM_ADDRESS_PREFIX0 + APARTMENT_NUMBER_PARAM,
                    addressDTO0.getApartmentNumber())
                .param(PARAM_ADDRESS_PREFIX0 + PROVINCE_PARAM, addressDTO0.getProvince())
                .param(PARAM_ADDRESS_PREFIX1 + COUNTRY_PARAM, addressDTO1.getCountry())
                .param(PARAM_ADDRESS_PREFIX1 + CITY_PARAM, addressDTO1.getCity())
                .param(PARAM_ADDRESS_PREFIX1 + POSTAL_CODE_PARAM, addressDTO1.getPostalCode())
                .param(PARAM_ADDRESS_PREFIX1 + STREET_PARAM, addressDTO1.getStreet())
                .param(PARAM_ADDRESS_PREFIX1 + HOUSE_NUMBER_PARAM, addressDTO1.getHouseNumber())
                .param(
                    PARAM_ADDRESS_PREFIX1 + APARTMENT_NUMBER_PARAM,
                    addressDTO1.getApartmentNumber())
                .param(PARAM_ADDRESS_PREFIX1 + PROVINCE_PARAM, addressDTO1.getProvince()))
        .andExpect(redirectedUrl(REGISTER_SHOP_ERROR_URL));
    assertEquals("Lokale muszą być unikalne", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(registerShopDTO, httpSession.getAttribute(REGISTER_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenPointsExists() throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkPointsExists(List.of(addressDTO), null)).thenReturn(true);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals("Takie lokale już istnieją", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenBadFile() throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkPointsExists(List.of(addressDTO), null)).thenReturn(false);
    when(shopService.checkImage(registerShopDTO.getFile())).thenReturn(false);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REGISTER_SHOP_ERROR_URL);
    assertEquals("Niepoprawny plik", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToLoginErrorAtRegisterShopWhenErrorAtSaveTempFile() throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkPointsExists(registerShopDTO.getAddress(), null)).thenReturn(false);
    when(shopService.checkImage(registerShopDTO.getFile())).thenReturn(true);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    when(passwordEncoder.encode(TEST_CODE)).thenReturn(TEST_ENCODE_CODE);

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, REDIRECT_LOGIN_ERROR);
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenExceptionAtSmsSending()
      throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkPointsExists(List.of(addressDTO), null)).thenReturn(false);
    when(shopService.checkImage(registerShopDTO.getFile())).thenReturn(true);
    when(shopService.saveTempFile(registerShopDTO.getFile())).thenReturn(FILE_PATH_PARAM);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(MongoWriteException.class)
        .when(smsService)
        .sendSms(
            VERIFICATION_MESSAGE + TEST_CODE,
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone());

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, VERIFICATION_SHOP_ERROR_URL);
    assertEquals(ERROR_AT_SMS_SENDING_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToRegisterShopErrorAtRegisterShopWhenExceptionAtEmailSending()
      throws Exception {
    RegisterShopDTO registerShopDTO = setRegisterShopDTO();
    AddressDTO addressDTO = registerShopDTO.getAddress().get(0);
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.phoneExists(
            registerShopDTO.getCallingCode() + registerShopDTO.getPhone()))
        .thenReturn(false);
    when(authenticationService.emailExists(registerShopDTO.getEmail())).thenReturn(false);
    when(shopService.checkShopNameExists(registerShopDTO.getName(), null)).thenReturn(false);
    when(shopService.checkAccountNumberExists(registerShopDTO.getAccountNumber(), null))
        .thenReturn(false);
    when(shopService.checkAccountNumber(registerShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkPointsExists(List.of(addressDTO), null)).thenReturn(false);
    when(shopService.checkImage(registerShopDTO.getFile())).thenReturn(true);
    when(shopService.saveTempFile(registerShopDTO.getFile())).thenReturn(FILE_PATH_PARAM);
    when(authenticationService.getVerificationNumber()).thenReturn(TEST_CODE);
    doThrow(MongoWriteException.class)
        .when(emailService)
        .sendEmail(eq(VERIFICATION_MESSAGE + TEST_CODE), eq(registerShopDTO.getEmail()), any());

    performPostRegisterShop(registerShopDTO, addressDTO, httpSession, VERIFICATION_SHOP_ERROR_URL);
    assertEquals(ERROR_AT_EMAIL_SENDING_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  private RegisterShopDTO setRegisterShopDTO() {
    final String shopName = "shop";
    final String accountNumber = "12342314321142312434324444";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCountry(TEST_TEXT);
    addressDTO.setCity(TEST_TEXT);
    addressDTO.setApartmentNumber(TEST_APARTMENT_NUMBER);
    addressDTO.setStreet(TEST_TEXT);
    addressDTO.setProvince(TEST_TEXT);
    addressDTO.setHouseNumber(TEST_HOUSE_NUMBER);
    addressDTO.setPostalCode(TEST_POSTAL_CODE);
    MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[0]);
    List<AddressDTO> addressDTOList = List.of(addressDTO);
    RegisterShopDTO registerShopDTO = new RegisterShopDTO();
    registerShopDTO.setPassword(TEST_PASSWORD);
    registerShopDTO.setAccountNumber(accountNumber);
    registerShopDTO.setEmail(TEST_MAIL);
    registerShopDTO.setPhone(TEST_PHONE);
    registerShopDTO.setCallingCode(PL_CALLING_CODE);
    registerShopDTO.setRetypedPassword(TEST_PASSWORD);
    registerShopDTO.setFirstName(TEST_TEXT);
    registerShopDTO.setLastName(TEST_TEXT);
    registerShopDTO.setFile(multipartFile);
    registerShopDTO.setAddress(addressDTOList);
    registerShopDTO.setName(shopName);
    return registerShopDTO;
  }

  private void performPostRegisterShop(
      RegisterShopDTO registerShopDTO,
      AddressDTO addressDTO,
      MockHttpSession httpSession,
      String redirectUrl)
      throws Exception {

    mockMvc
        .perform(
            multipart(REGISTER_SHOP_URL)
                .file((MockMultipartFile) registerShopDTO.getFile())
                .session(httpSession)
                .param(FIRST_NAME_PARAM, registerShopDTO.getFirstName())
                .param(LAST_NAME_PARAM, registerShopDTO.getLastName())
                .param(EMAIL_PARAM, registerShopDTO.getEmail())
                .param(CALLING_CODE_PARAM, registerShopDTO.getCallingCode())
                .param(PHONE_PARAM, registerShopDTO.getPhone())
                .param(PASSWORD_PARAM, registerShopDTO.getPassword())
                .param(RETYPED_PASSWORD_PARAM, registerShopDTO.getRetypedPassword())
                .param(SHOP_NAME_PARAM, registerShopDTO.getName())
                .param(ACCOUNT_NUMBER_PARAM, registerShopDTO.getAccountNumber())
                .param(PARAM_ADDRESS_PREFIX0 + COUNTRY_PARAM, addressDTO.getCountry())
                .param(PARAM_ADDRESS_PREFIX0 + CITY_PARAM, addressDTO.getCity())
                .param(PARAM_ADDRESS_PREFIX0 + POSTAL_CODE_PARAM, addressDTO.getPostalCode())
                .param(PARAM_ADDRESS_PREFIX0 + STREET_PARAM, addressDTO.getStreet())
                .param(PARAM_ADDRESS_PREFIX0 + HOUSE_NUMBER_PARAM, addressDTO.getHouseNumber())
                .param(
                    PARAM_ADDRESS_PREFIX0 + APARTMENT_NUMBER_PARAM, addressDTO.getApartmentNumber())
                .param(PARAM_ADDRESS_PREFIX0 + PROVINCE_PARAM, addressDTO.getProvince()))
        .andExpect(redirectedUrl(redirectUrl));
    assertEquals(registerShopDTO, httpSession.getAttribute(REGISTER_PARAM));
  }

  @Test
  public void shouldReturnVerificationShopPageAtVerificationShopPageWhenEverythingOk()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, new RegisterShopDTO());

    mockMvc
        .perform(get(SHOP_VERIFICATIONS_URL).session(httpSession))
        .andExpect(view().name(VERIFICATION_SHOP_PAGE));
  }

  @Test
  public void shouldRedirectToLoginAtVerificationShopPageWhenNoRegisterShopPage() throws Exception {
    mockMvc.perform(get(SHOP_VERIFICATIONS_URL)).andExpect(redirectedUrl(LOGIN_URL));
  }

  @Test
  public void shouldRedirectToLoginAtVerificationShopPageWhenParamReset() throws Exception {
    MultipartFile multipartFile = new MockMultipartFile("file", new byte[0]);
    Path path = Files.createTempFile("_upload", ".tmp");
    Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(FILE_PATH_PARAM, path.toString());
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 1);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM_REGISTER_SHOP, TEST_ENCODE_CODE);
    httpSession.setAttribute(REGISTER_PARAM, new RegisterShopDTO());

    mockMvc
        .perform(get(SHOP_VERIFICATIONS_URL).param(RESET_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(LOGIN_URL));
    assertNull(httpSession.getAttribute(FILE_PATH_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_REGISTER_SHOP));
  }

  @Test
  public void shouldReturnVerificationShopPageAtVerificationShopPageWhenParamErrorWithMessage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, new RegisterShopDTO());
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    mockMvc
        .perform(get(SHOP_VERIFICATIONS_URL).session(httpSession).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(VERIFICATION_SHOP_PAGE));
  }

  @Test
  public void shouldReturnVerificationShopPageAtVerificationShopPageWhenParamErrorWithoutMessage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, new RegisterShopDTO());

    mockMvc
        .perform(get(SHOP_VERIFICATIONS_URL).session(httpSession).param(ERROR_PARAM, ""))
        .andExpect(view().name(VERIFICATION_SHOP_PAGE));
  }

  @Test
  public void shouldRedirectToLoginErrorAtVerificationShopWhenTooManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = setSessionAtVerificationShop(maxAttempts);

    performPostAtVerificationShop(
        httpSession,
        ERROR_TOO_MANY_ATTEMPTS_MESSAGE,
        REDIRECT_LOGIN_ERROR,
        TEST_CODE,
        ERROR_MESSAGE_PARAM);
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_REGISTER_SHOP));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(FILE_PATH_PARAM));
  }

  @Test
  public void shouldRedirectToShopVerificationErrorAtVerificationShopWhenErrorAtValidation()
      throws Exception {
    MockHttpSession httpSession = setSessionAtVerificationShop(0);

    performPostAtVerificationShop(
        httpSession,
        ERROR_INCORRECT_DATA_MESSAGE,
        VERIFICATION_SHOP_ERROR_URL,
        "10",
        ERROR_MESSAGE_PARAM);
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToShopVerificationErrorAtVerificationShopWhenBadSmsCode()
      throws Exception {
    MockHttpSession httpSession = setSessionAtVerificationShop(0);

    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(false);

    performPostAtVerificationShop(
        httpSession,
        "Nieprawidłowy kod email",
        VERIFICATION_SHOP_ERROR_URL,
        TEST_CODE,
        ERROR_MESSAGE_PARAM);
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToShopVerificationErrorAtVerificationShopWhenBadEmailCode()
      throws Exception {
    MockHttpSession httpSession = setSessionAtVerificationShop(0);

    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    performPostAtVerificationShop(
        httpSession,
        ERROR_BAD_SMS_CODE_MESSAGE,
        VERIFICATION_SHOP_ERROR_URL,
        TEST_CODE,
        ERROR_MESSAGE_PARAM);
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToShopVerificationErrorAtVerificationShopWhenBadSize()
      throws Exception {
    MockHttpSession httpSession = setSessionAtVerificationShop(0);

    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);

    MockMultipartFile multipartFile1 = new MockMultipartFile("file1", new byte[0]);
    MockMultipartFile multipartFile2 = new MockMultipartFile("file2", new byte[0]);
    MockMultipartFile multipartFile3 = new MockMultipartFile("file3", new byte[0]);
    MockMultipartFile multipartFile4 = new MockMultipartFile("file4", new byte[0]);
    MockMultipartFile multipartFile5 = new MockMultipartFile("file5", new byte[0]);
    MockMultipartFile multipartFile6 = new MockMultipartFile("file6", new byte[0]);
    MockMultipartFile multipartFile7 = new MockMultipartFile("file7", new byte[0]);
    MockMultipartFile multipartFile8 = new MockMultipartFile("file8", new byte[0]);
    MockMultipartFile multipartFile9 = new MockMultipartFile("file9", new byte[0]);
    MockMultipartFile multipartFile10 = new MockMultipartFile("file10", new byte[0]);

    mockMvc
        .perform(
            multipart(SHOP_VERIFICATIONS_URL)
                .file("file[0]", multipartFile1.getBytes())
                .file("file[1]", multipartFile2.getBytes())
                .file("file[2]", multipartFile3.getBytes())
                .file("file[3]", multipartFile4.getBytes())
                .file("file[4]", multipartFile5.getBytes())
                .file("file[5]", multipartFile6.getBytes())
                .file("file[6]", multipartFile7.getBytes())
                .file("file[7]", multipartFile8.getBytes())
                .file("file[8]", multipartFile9.getBytes())
                .file("file[9]", multipartFile10.getBytes())
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_OTHER_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(VERIFICATION_SHOP_ERROR_URL));
    assertEquals(
        "Nieprawidłowa ilość przesłanych plików", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToShopVerificationErrorAtVerificationShopWhenErrorAtCheckFiles()
      throws Exception {
    MockHttpSession httpSession = setSessionAtVerificationShop(0);

    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(shopService.checkFiles(any())).thenReturn(false);

    performPostAtVerificationShop(
        httpSession,
        "Nieprawidłowe pliki",
        VERIFICATION_SHOP_ERROR_URL,
        TEST_CODE,
        ERROR_MESSAGE_PARAM);
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  public void shouldRedirectToLoginErrorAtVerificationShopWhenErrorAtCreateShop() throws Exception {
    MockHttpSession httpSession = setSessionAtVerificationShop(0);

    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(shopService.checkFiles(any())).thenReturn(true);
    when(authenticationService.createShop(
            eq((RegisterShopDTO) httpSession.getAttribute(REGISTER_PARAM)),
            eq((String) httpSession.getAttribute(FILE_PATH_PARAM)),
            anyList(),
            any()))
        .thenReturn(false);

    performPostAtVerificationShop(
        httpSession,
        ERROR_UNEXPECTED_MESSAGE,
        REDIRECT_LOGIN_ERROR,
        TEST_CODE,
        ERROR_MESSAGE_PARAM);
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_REGISTER_SHOP));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(FILE_PATH_PARAM));
  }

  @Test
  public void shouldRedirectToLoginSuccessAtVerificationShopWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = setSessionAtVerificationShop(0);

    when(passwordEncoder.matches(TEST_OTHER_CODE, TEST_OTHER_ENCODE_CODE)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(shopService.checkFiles(any())).thenReturn(true);
    when(authenticationService.createShop(
            eq((RegisterShopDTO) httpSession.getAttribute(REGISTER_PARAM)),
            eq((String) httpSession.getAttribute(FILE_PATH_PARAM)),
            anyList(),
            any()))
        .thenReturn(true);

    performPostAtVerificationShop(
        httpSession,
        "Powiadomimy cię o pomyślnej weryfikacji lub ewentualnych błędach",
        REDIRECT_LOGIN_SUCCESS,
        TEST_CODE,
        SUCCESS_MESSAGE_PARAM);
    assertNull(httpSession.getAttribute(REGISTER_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_PARAM_REGISTER_SHOP));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM));
    assertNull(httpSession.getAttribute(FILE_PATH_PARAM));
  }

  private MockHttpSession setSessionAtVerificationShop(int attempts) throws IOException {
    MockMultipartFile multipartFile3 = new MockMultipartFile("file3", new byte[0]);
    Path path = Files.createTempFile("upload_", ".tmp");
    Files.copy(multipartFile3.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(REGISTER_PARAM, new RegisterShopDTO());
    httpSession.setAttribute(ATTEMPTS_PARAM, attempts);
    httpSession.setAttribute(CODE_EMAIL_PARAM, TEST_OTHER_ENCODE_CODE);
    httpSession.setAttribute(CODE_SMS_PARAM_REGISTER_SHOP, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 1);
    httpSession.setAttribute(FILE_PATH_PARAM, path.toString());
    return httpSession;
  }

  private void performPostAtVerificationShop(
      MockHttpSession httpSession,
      String message,
      String redirectUrl,
      String codeSms,
      String messageParam)
      throws Exception {
    MockMultipartFile multipartFile1 = new MockMultipartFile("file1", new byte[0]);
    MockMultipartFile multipartFile2 = new MockMultipartFile("file2", new byte[0]);

    mockMvc
        .perform(
            multipart(SHOP_VERIFICATIONS_URL)
                .file("file[0]", multipartFile1.getBytes())
                .file("file[1]", multipartFile2.getBytes())
                .param(VERIFICATION_NUMBER_SMS_PARAM, codeSms)
                .param(VERIFICATION_NUMBER_EMAIL_PARAM, TEST_OTHER_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
    assertEquals(message, httpSession.getAttribute(messageParam));
  }
}
