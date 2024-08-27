package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
  @Autowired private MockMvc mockMvc;
  @MockBean private CountryService countryService;
  @MockBean private AuthenticationService authenticationService;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private SmsService smsService;
  @MockBean private EmailService emailService;

  private static List<CountryDTO> expectedCountries;
  private static List<CallingCodeDTO> expectedCallingCodes;
  private static List<CountryNamesDTO> expectedCountryNames;

  private static RegisterDTO expectedRegisterDTO;
  private static final String ERROR_AT_SMS_SENDING = "Błąd podczas wysyłania sms";
  private static final String ERROR_AT_EMAIL_SENDING = "Błąd podczas wysyłania emaila";
  private static final String ERROR = "Error!";

  @BeforeAll
  public static void setUp() {
    expectedCountries =
        List.of(
            new CountryDTO("Polska", "PL", "+48"),
            new CountryDTO("Niemcy", "DE", "+49"),
            new CountryDTO("Francja", "FR", "+33"));
    expectedCallingCodes =
        List.of(
            new CallingCodeDTO("+48", "PL"),
            new CallingCodeDTO("+49", "DE"),
            new CallingCodeDTO("+33", "FR"));
    expectedCountryNames =
        List.of(
            new CountryNamesDTO("Polska", "PL"),
            new CountryNamesDTO("Niemcy", "DE"),
            new CountryNamesDTO("Francja", "FR"));
    expectedRegisterDTO =
        new RegisterDTO(
            "Test",
            "Test",
            "Test@Test.pl",
            "Test",
            "Test",
            "1",
            null,
            "00-000",
            "Test",
            "Polska",
            "+48",
            "123456789",
            "Test1!",
            "Test1!");
  }

  @Test
  public void shouldReturnLoginPage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get("/login"))
        .andExpect(model().attribute("callingCodes", expectedCallingCodes))
        .andExpect(view().name("loginPage"));
  }

  @Test
  public void shouldReturnLoginPageWhenParamSuccessButNoMessage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get("/login").param("success", ""))
        .andExpect(model().attribute("callingCodes", expectedCallingCodes))
        .andExpect(view().name("loginPage"));
  }

  @Test
  public void shouldReturnLoginPageWhenParamSuccessWithMessage() throws Exception {
    final String succesMessage = "Sukces!";
    final String phone = "23412341234";
    final String callingCode = "+48";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("successMessage", succesMessage);
    httpSession.setAttribute("phone", phone);
    httpSession.setAttribute("callingCode", callingCode);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get("/login").param("success", "").session(httpSession))
        .andExpect(model().attribute("callingCodes", expectedCallingCodes))
        .andExpect(model().attribute("successMessage", succesMessage))
        .andExpect(model().attribute("phone", phone))
        .andExpect(model().attribute("callingCode", callingCode))
        .andExpect(view().name("loginPage"));
  }

  @Test
  public void shouldReturnLoginPageWhenParamErrorButNoMessage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get("/login").param("error", ""))
        .andExpect(model().attribute("errorMessage", "Niepoprawny login lub hasło"))
        .andExpect(model().attribute("callingCodes", expectedCallingCodes))
        .andExpect(view().name("loginPage"));
  }

  @Test
  public void shouldReturnLoginPageWhenParamErrorWithMessage() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("errorMessage", ERROR);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get("/login").param("error", "").session(httpSession))
        .andExpect(model().attribute("callingCodes", expectedCallingCodes))
        .andExpect(model().attribute("errorMessage", ERROR))
        .andExpect(view().name("loginPage"));
  }

  @Test
  public void shouldReturnRegisterPage() throws Exception {
    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get("/register"))
        .andExpect(model().attribute("countries", expectedCountryNames))
        .andExpect(model().attribute("callingCodes", expectedCallingCodes))
        .andExpect(model().attribute("register", new RegisterDTO()))
        .andExpect(view().name("registerPage"));
  }

  @Test
  public void shouldReturnRegisterPageWhenParamErrorWithMessage() throws Exception {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("12312443");
    registerDTO.setFirstName("TestA");
    registerDTO.setLastName("TestB");
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("errorMessage", ERROR);
    httpSession.setAttribute("register", registerDTO);

    when(countryService.getAll()).thenReturn(expectedCountries);

    mockMvc
        .perform(get("/register").param("error", "").session(httpSession))
        .andExpect(model().attribute("countries", expectedCountryNames))
        .andExpect(model().attribute("callingCodes", expectedCallingCodes))
        .andExpect(model().attribute("register", registerDTO))
        .andExpect(model().attribute("errorMessage", ERROR))
        .andExpect(view().name("registerPage"));
  }

  @Test
  public void shouldRedirectToAccountVerification() throws Exception {
    final String verificationNumber = "123 456";
    final String encodeVerificationNumber = "sad8h121231z#$2";
    final List<String> phones = List.of("+34234234234");
    final List<String> emails = List.of("test@test");

    MockHttpSession httpSession = new MockHttpSession();
    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);
    when(authenticationService.getVerificationNumber()).thenReturn(verificationNumber);
    when(passwordEncoder.encode(verificationNumber)).thenReturn(encodeVerificationNumber);

    performPostRegister(expectedRegisterDTO, httpSession, "/account_verifications");
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute("register")).toString());
    assertEquals(1, httpSession.getAttribute("codeAmountSms"));
    assertEquals(encodeVerificationNumber, httpSession.getAttribute("codeSms"));
    assertEquals(1, httpSession.getAttribute("codeAmountEmail"));
    assertEquals(encodeVerificationNumber, httpSession.getAttribute("codeEmail"));
    assertEquals(0, httpSession.getAttribute("attempts"));
  }

  @Test
  public void shouldRedirectToRegisterByValidationProblem() throws Exception {
    final String errorMessage = "Podane dane są niepoprawne";
    RegisterDTO expectedRegisterDTO = new RegisterDTO();
    expectedRegisterDTO.setPhone("+12312313231");
    MockHttpSession httpSession = new MockHttpSession();

    performPostRegister(expectedRegisterDTO, httpSession, "/register?error");
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute("register")).toString());
    assertEquals(errorMessage, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToRegisterByUsersTheSameByPhone() throws Exception {
    final String errorMessage = "Użytkownik o takim numerze telefonu już istnieje";
    final List<String> phones = List.of("+48123456789", "+1212345673123");
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);

    performPostRegister(expectedRegisterDTO, httpSession, "/register?error");
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute("register")).toString());
    assertEquals(errorMessage, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToRegisterByUsersTheSameByEmail() throws Exception {
    final String errorMessage = "Użytkownik o takim emailu już istnieje";
    final List<String> phones = List.of("+1212345673123");
    final List<String> emails = List.of("Test@Test.pl", "test@test");

    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);

    performPostRegister(expectedRegisterDTO, httpSession, "/register?error");
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute("register")).toString());
    assertEquals(errorMessage, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToVerificationWhenErrorAtGetVerificationSms() throws Exception {
    final String verificationNumber = "123 456";
    final List<String> phones = List.of("+34234234234");
    final List<String> emails = List.of("test@test");
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);
    when(authenticationService.getVerificationNumber()).thenReturn(verificationNumber);
    doThrow(ApiException.class)
        .when(smsService)
        .sendSms(
            "Twój kod weryfikacyjny MultipleCard to: " + verificationNumber,
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone());

    performPostRegister(expectedRegisterDTO, httpSession, "/account_verifications?error");
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute("register")).toString());
    assertEquals(ERROR_AT_SMS_SENDING, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToVerificationWhenErrorAtGetVerificationEmail() throws Exception {
    final String verificationNumber = "123 456";
    final List<String> phones = List.of("+34234234234");
    final List<String> emails = List.of("test@test");
    MockHttpSession httpSession = new MockHttpSession();

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);
    when(authenticationService.getVerificationNumber()).thenReturn(verificationNumber);
    doThrow(ApiException.class)
        .when(emailService)
        .sendEmail(
            "Twój kod weryfikacyjny MultipleCard to: " + verificationNumber,
            expectedRegisterDTO.getEmail());

    performPostRegister(expectedRegisterDTO, httpSession, "/account_verifications?error");
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute("register")).toString());
    assertEquals(ERROR_AT_EMAIL_SENDING, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToRegisterByPasswordsNotTheSame() throws Exception {
    final String errorMessage = "Podane hasła różnią się";
    final List<String> phones = List.of("+34234234234");
    final List<String> emails = List.of("test@test");
    MockHttpSession httpSession = new MockHttpSession();
    RegisterDTO expectedRegisterDTO = new RegisterDTO();
    expectedRegisterDTO.setPhone("12312313231");
    expectedRegisterDTO.setPassword("Test1!");
    expectedRegisterDTO.setRetypedPassword("Test1!2");
    expectedRegisterDTO.setCountry("Polska");
    expectedRegisterDTO.setFirstName("Test");
    expectedRegisterDTO.setLastName("Test");
    expectedRegisterDTO.setEmail("email@email.com");
    expectedRegisterDTO.setProvince("Test");
    expectedRegisterDTO.setCity("Test");
    expectedRegisterDTO.setHouseNumber("1");
    expectedRegisterDTO.setCallingCode("+48");
    expectedRegisterDTO.setPostalCode("00-000");
    expectedRegisterDTO.setStreet("Test");

    when(authenticationService.getPhones()).thenReturn(phones);
    when(authenticationService.getEmails()).thenReturn(emails);

    performPostRegister(expectedRegisterDTO, httpSession, "/register?error");
    assertEquals(
        expectedRegisterDTO.toString(),
        Objects.requireNonNull(httpSession.getAttribute("register")).toString());
    assertEquals(errorMessage, httpSession.getAttribute("errorMessage"));
  }

  private void performPostRegister(
      RegisterDTO expectedRegisterDTO, MockHttpSession httpSession, String redirectUrl)
      throws Exception {
    mockMvc
        .perform(
            post("/register")
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
                .param("callingCode", expectedRegisterDTO.getCallingCode())
                .param("phone", expectedRegisterDTO.getPhone())
                .param("password", expectedRegisterDTO.getPassword())
                .param("retypedPassword", expectedRegisterDTO.getRetypedPassword())
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  public void shouldReturnVerificationPage() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);

    mockMvc
        .perform(get("/account_verifications").session(httpSession))
        .andExpect(view().name("verificationPage"));
  }

  @Test
  public void shouldRedirectToLoginPageWhenNoRegisterDTOAdded() throws Exception {
    mockMvc.perform(get("/account_verifications")).andExpect(redirectedUrl("/login"));
  }

  @Test
  public void shouldReturnVerificationPageWithParamNewCodeSms() throws Exception {
    final String verificationNumber = "123 456";
    final String encodeVerificationNumber = "sad8h121231z#$2";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeAmountSms", 0);

    when(authenticationService.getVerificationNumber()).thenReturn(verificationNumber);
    when(passwordEncoder.encode(verificationNumber)).thenReturn(encodeVerificationNumber);

    mockMvc
        .perform(get("/account_verifications").param("newCodeSms", "").session(httpSession))
        .andExpect(view().name("verificationPage"));
    assertEquals(1, httpSession.getAttribute("codeAmountSms"));
    assertEquals(encodeVerificationNumber, httpSession.getAttribute("codeSms"));
  }

  @Test
  public void shouldRedirectToVerificationPageWhenErrorAtGetVerificationSms() throws Exception {
    final String verificationNumber = "123 456";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeAmountSms", 0);

    when(authenticationService.getVerificationNumber()).thenReturn(verificationNumber);
    doThrow(ApiException.class)
        .when(smsService)
        .sendSms(
            "Twój kod weryfikacyjny MultipleCard to: " + verificationNumber,
            expectedRegisterDTO.getCallingCode() + expectedRegisterDTO.getPhone());

    mockMvc
        .perform(get("/account_verifications").param("newCodeSms", "").session(httpSession))
        .andExpect(redirectedUrl("/account_verifications?error"));
    assertEquals(ERROR_AT_SMS_SENDING, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldReturnVerificationPageWithParamNewCodeEmail() throws Exception {
    final String verificationNumber = "123 456";
    final String encodeVerificationNumber = "sad8h121231z#$2";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeAmountEmail", 0);

    when(authenticationService.getVerificationNumber()).thenReturn(verificationNumber);
    when(passwordEncoder.encode(verificationNumber)).thenReturn(encodeVerificationNumber);

    mockMvc
        .perform(get("/account_verifications").param("newCodeEmail", "").session(httpSession))
        .andExpect(view().name("verificationPage"));
    assertEquals(1, httpSession.getAttribute("codeAmountEmail"));
    assertEquals(encodeVerificationNumber, httpSession.getAttribute("codeEmail"));
  }

  @Test
  public void shouldRedirectToVerificationPageWhenErrorAtGetVerificationEmail() throws Exception {
    final String verificationNumber = "123 456";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeAmountEmail", 0);

    when(authenticationService.getVerificationNumber()).thenReturn(verificationNumber);
    doThrow(ApiException.class)
        .when(emailService)
        .sendEmail(
            "Twój kod weryfikacyjny MultipleCard to: " + verificationNumber,
            expectedRegisterDTO.getEmail());

    mockMvc
        .perform(get("/account_verifications").param("newCodeEmail", "").session(httpSession))
        .andExpect(redirectedUrl("/account_verifications?error"));
    assertEquals(ERROR_AT_EMAIL_SENDING, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToVerificationPageWhenTooMuchCodesSms() throws Exception {
    final int maxCodeAmount = 3;
    final String message = "Za dużo razy poprosiłeś o nowy kod sms";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeAmountSms", maxCodeAmount);

    mockMvc
        .perform(get("/account_verifications").param("newCodeSms", "").session(httpSession))
        .andExpect(redirectedUrl("/account_verifications?error"));
    assertEquals(maxCodeAmount, httpSession.getAttribute("codeAmountSms"));
    assertEquals(message, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToVerificationPageWhenTooMuchCodesEmail() throws Exception {
    final int maxCodeAmount = 3;
    final String message = "Za dużo razy poprosiłeś o nowy kod email";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeAmountEmail", maxCodeAmount);

    mockMvc
        .perform(get("/account_verifications").param("newCodeEmail", "").session(httpSession))
        .andExpect(redirectedUrl("/account_verifications?error"));
    assertEquals(maxCodeAmount, httpSession.getAttribute("codeAmountEmail"));
    assertEquals(message, httpSession.getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToLoginPageWithParamReset() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeAmountSms", 0);
    httpSession.setAttribute("codeSms", "sad8h121231z#$2");
    httpSession.setAttribute("codeAmountEmail", 0);
    httpSession.setAttribute("codeEmail", "sad8h121231z#$2");

    mockMvc
        .perform(get("/account_verifications").param("reset", "").session(httpSession))
        .andExpect(redirectedUrl("/login"));
    assertNull(httpSession.getAttribute("register"));
    assertNull(httpSession.getAttribute("codeAmountSms"));
    assertNull(httpSession.getAttribute("codeSms"));
    assertNull(httpSession.getAttribute("codeAmountEmail"));
    assertNull(httpSession.getAttribute("codeEmail"));
    assertNull(httpSession.getAttribute("attempts"));
  }

  @Test
  public void shouldReturnVerificationPageWithParamError() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("errorMessage", ERROR);

    mockMvc
        .perform(get("/account_verifications").param("error", "").session(httpSession))
        .andExpect(model().attribute("errorMessage", ERROR))
        .andExpect(view().name("verificationPage"));
  }

  @Test
  public void shouldRedirectToLogin() throws Exception {
    final String verificationEmail = "123 456";
    final String codeEmail = "sad8h121231z#$2";
    final String verificationSms = "213 555";
    final String codeSms = "s23141234sdfs";
    final String message = "Pomyślnie zarejestrowano";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeEmail", codeEmail);
    httpSession.setAttribute("codeSms", codeSms);
    httpSession.setAttribute("attempts", 0);

    when(passwordEncoder.matches(verificationEmail, codeEmail)).thenReturn(true);
    when(passwordEncoder.matches(verificationSms, codeSms)).thenReturn(true);

    mockMvc
        .perform(
            post("/account_verifications")
                .session(httpSession)
                .param("verificationNumberEmail", verificationEmail)
                .param("verificationNumberSms", verificationSms))
        .andExpect(redirectedUrl("/login?success"));
    assertEquals(expectedRegisterDTO.getPhone(), httpSession.getAttribute("phone"));
    assertEquals(expectedRegisterDTO.getCallingCode(), httpSession.getAttribute("callingCode"));
    assertEquals(message, httpSession.getAttribute("successMessage"));
    assertNull(httpSession.getAttribute("register"));
    assertNull(httpSession.getAttribute("codeSms"));
    assertNull(httpSession.getAttribute("codeAmountSms"));
    assertNull(httpSession.getAttribute("codeEmail"));
    assertNull(httpSession.getAttribute("codeAmountEmail"));
    assertNull(httpSession.getAttribute("attempts"));
  }

  @Test
  public void shouldRedirectToVerificationWhenTooManyAttempts() throws Exception {
    final String message = "Za dużo razy wpisałeś niepoprawny kod";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("attempts", 3);

    mockMvc
        .perform(
            post("/account_verifications")
                .session(httpSession)
                .param("verificationNumberEmail", "123 234")
                .param("verificationNumberSms", "123 323"))
        .andExpect(redirectedUrl("/login?error"));
    assertEquals(message, httpSession.getAttribute("errorMessage"));
    assertNull(httpSession.getAttribute("register"));
    assertNull(httpSession.getAttribute("codeSms"));
    assertNull(httpSession.getAttribute("codeAmountSms"));
    assertNull(httpSession.getAttribute("codeEmail"));
    assertNull(httpSession.getAttribute("codeAmountEmail"));
    assertNull(httpSession.getAttribute("attempts"));
  }

  @Test
  public void shouldRedirectToVerificationWhenVerificationEmailIsBad() throws Exception {
    final String verificationEmail = "123 456";
    final String codeEmail = "sad8h121231z#$2";
    final String message = "Nieprawidłowy kod email";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeEmail", codeEmail);
    httpSession.setAttribute("attempts", 0);

    when(passwordEncoder.matches(verificationEmail, codeEmail)).thenReturn(false);

    mockMvc
        .perform(
            post("/account_verifications")
                .session(httpSession)
                .param("verificationNumberEmail", verificationEmail)
                .param("verificationNumberSms", "123 123"))
        .andExpect(redirectedUrl("/account_verifications?error"));
    assertEquals(message, httpSession.getAttribute("errorMessage"));
    assertEquals(1, httpSession.getAttribute("attempts"));
  }

  @Test
  public void shouldRedirectToVerificationWhenVerificationSmsIsBad() throws Exception {
    final String verificationEmail = "123 456";
    final String codeEmail = "sad8h121231z#$2";
    final String verificationSms = "443 411";
    final String codeSms = "ssadfsadfsadf123";
    final String message = "Nieprawidłowy kod sms";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeEmail", codeEmail);
    httpSession.setAttribute("codeSms", codeSms);
    httpSession.setAttribute("attempts", 0);

    when(passwordEncoder.matches(verificationEmail, codeEmail)).thenReturn(true);
    when(passwordEncoder.matches(verificationSms, codeSms)).thenReturn(false);

    mockMvc
        .perform(
            post("/account_verifications")
                .session(httpSession)
                .param("verificationNumberEmail", verificationEmail)
                .param("verificationNumberSms", verificationSms))
        .andExpect(redirectedUrl("/account_verifications?error"));
    assertEquals(message, httpSession.getAttribute("errorMessage"));
    assertEquals(1, httpSession.getAttribute("attempts"));
  }

  @Test
  public void shouldRedirectToLoginWhenErrorAtCreateUser() throws Exception {
    final String verificationEmail = "123 456";
    final String codeEmail = "sad8h121231z#$2";
    final String verificationSms = "443 411";
    final String codeSms = "ssadfsadfsadf123";
    final String message = "Nieoczekiwany błąd";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute("register", expectedRegisterDTO);
    httpSession.setAttribute("codeEmail", codeEmail);
    httpSession.setAttribute("codeSms", codeSms);
    httpSession.setAttribute("attempts", 0);

    when(passwordEncoder.matches(verificationEmail, codeEmail)).thenReturn(true);
    when(passwordEncoder.matches(verificationSms, codeSms)).thenReturn(true);
    doThrow(MongoWriteException.class).when(authenticationService).createUser(expectedRegisterDTO);

    mockMvc
        .perform(
            post("/account_verifications")
                .session(httpSession)
                .param("verificationNumberEmail", verificationEmail)
                .param("verificationNumberSms", verificationSms))
        .andExpect(redirectedUrl("/login?error"));
    assertEquals(message, httpSession.getAttribute("errorMessage"));
    assertNull(httpSession.getAttribute("register"));
    assertNull(httpSession.getAttribute("codeSms"));
    assertNull(httpSession.getAttribute("codeAmountSms"));
    assertNull(httpSession.getAttribute("codeEmail"));
    assertNull(httpSession.getAttribute("codeAmountEmail"));
  }
}
