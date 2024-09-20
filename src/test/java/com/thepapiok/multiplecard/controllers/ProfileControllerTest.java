package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.dto.ChangePasswordDTO;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.ProfileService;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProfileControllerTest {
  private static final String TEST_PHONE = "+4823232342342342";
  private static final String FIRST_NAME_PARAM = "firstName";
  private static final String LAST_NAME_PARAM = "lastName";
  private static final String PROVINCE_PARAM = "province";
  private static final String STREET_PARAM = "street";
  private static final String HOUSE_NUMBER_PARAM = "houseNumber";
  private static final String APARTMENT_NUMBER_PARAM = "apartmentNumber";
  private static final String POSTAL_CODE_PARAM = "postalCode";
  private static final String CITY_PARAM = "city";
  private static final String COUNTRY_PARAM = "country";
  private static final String PROFILE_PARAM = "profile";
  private static final String CODE_PARAM = "code";
  private static final String RETYPED_PASSWORD_PARAM = "retypedPassword";
  private static final String NEW_PASSWORD_PARAM = "newPassword";
  private static final String OLD_PASSWORD_PARAM = "oldPassword";
  private static final String COUNTRIES_PARAM = "countries";
  private static final String USER_URL = "/user";
  private static final String LOGIN_SUCCESS_URL = "/login?success";
  private static final String PASSWORD_CHANGE_URL = "/password_change";
  private static final String USER_ERROR_URL = "/user?error";
  private static final String DELETE_ACCOUNT_URL = "/delete_account";
  private static final String DELETE_ACCOUNT_ERROR_URL = "/delete_account?error";
  private static final String DELETE_ACCOUNT_PAGE = "deleteAccountPage";
  private static final String PROFILE_PAGE = "profilePage";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String CARD_PARAM = "card";
  private static final String ERROR_MESSAGE = "error!";
  private static final String SUCCESS_UPDATE_MESSAGE = "Pomyślnie zaktualizowano dane";
  private static final String ERROR_UNEXPECTED_MESSAGE = "Nieoczekiwany błąd";
  private static final String ERROR_TOO_MANY_ATTEMPTS_MESSAGE =
      "Za dużo razy podałeś niepoprawne dane";
  private static final String ERROR_BAD_SMS_CODE_MESSAGE = "Nieprawidłowy kod sms";
  private static final String ERROR_PARAM = "error";
  private static final String RESET_PARAM = "reset";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String VERIFICATION_NUMBER_SMS_PARAM = "verificationNumberSms";
  private static final String PHONE_PARAM = "phone";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  private static final String CODE_SMS_CHANGE_PARAM = "codeSmsChange";
  private static final String CODE_SMS_DELETE_PARAM = "codeSmsDelete";
  private static final String CHANGE_PASSWORD_PARAM = "changePassword";
  private static final String CHANGE_PASSWORD_PAGE = "changePasswordPage";
  private static final String ERROR_VALIDATION_MESSAGE = "Podane dane są niepoprawne";
  private static final String TEST_CODE = "123 123";
  private static final String TEST_OLD_PASSWORD = "Test123!";
  private static final String TEST_NEW_PASSWORD = "Test123!!";
  private static final String TEST_ENCODE_CODE = "312fdasfdsaffsd";
  private static ProfileDTO profileDTO;
  private static List<CountryDTO> countryDTOS;
  private static List<CountryNamesDTO> countryNamesDTOS;

  @Autowired private MockMvc mockMvc;
  @MockBean private ProfileService profileService;
  @MockBean private CountryService countryService;
  @MockBean private CardService cardService;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private AuthenticationService authenticationService;

  @BeforeAll
  public static void setUp() {
    final String countryName1 = "Poland";
    final String countryName2 = "sdfsdf";
    final String countryCode1 = "PL";
    final String countryCode2 = "ds";
    profileDTO = new ProfileDTO();
    profileDTO.setPostalCode("11-111");
    profileDTO.setApartmentNumber("2");
    profileDTO.setCountry("Country");
    profileDTO.setCity("City");
    profileDTO.setStreet("Street");
    profileDTO.setProvince("Province");
    profileDTO.setHouseNumber("1");
    profileDTO.setFirstName("Firstname");
    profileDTO.setLastName("Last Name");
    countryDTOS =
        List.of(
            new CountryDTO(countryName1, countryCode1, "+48"),
            new CountryDTO(countryName2, countryCode2, "+123"));
    countryNamesDTOS =
        List.of(
            new CountryNamesDTO(countryName1, countryCode1),
            new CountryNamesDTO(countryName2, countryCode2));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageAtGetProfile() throws Exception {
    successReturnProfilePage();
  }

  private void successReturnProfilePage() throws Exception {
    Card card = new Card();

    when(cardService.getCard(TEST_PHONE)).thenReturn(card);
    when(profileService.getProfile(TEST_PHONE)).thenReturn(profileDTO);
    when(countryService.getAll()).thenReturn(countryDTOS);

    mockMvc
        .perform(get(USER_URL))
        .andExpect(model().attribute(CARD_PARAM, card))
        .andExpect(model().attribute(PROFILE_PARAM, profileDTO))
        .andExpect(model().attribute(COUNTRIES_PARAM, countryNamesDTOS))
        .andExpect(view().name(PROFILE_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageWithErrorAtGetProfileWhenParamAndMessage() throws Exception {
    returnProfilePageWithParamAndMessage(ERROR_PARAM, ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageWithErrorAtGetProfileWhenParamWithoutMessage()
      throws Exception {
    successReturnProfilePage();
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageWithSuccessAtGetProfileWhenParamAndMessage() throws Exception {
    returnProfilePageWithParamAndMessage("success", SUCCESS_MESSAGE_PARAM, SUCCESS_UPDATE_MESSAGE);
  }

  private void returnProfilePageWithParamAndMessage(
      String param, String messageParam, String message) throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(messageParam, message);
    Card card = new Card();

    when(cardService.getCard(TEST_PHONE)).thenReturn(card);
    when(profileService.getProfile(TEST_PHONE)).thenReturn(profileDTO);
    when(countryService.getAll()).thenReturn(countryDTOS);

    mockMvc
        .perform(get(USER_URL).param(param, "").session(httpSession))
        .andExpect(model().attribute(CARD_PARAM, card))
        .andExpect(model().attribute(PROFILE_PARAM, profileDTO))
        .andExpect(model().attribute(COUNTRIES_PARAM, countryNamesDTOS))
        .andExpect(model().attribute(messageParam, message))
        .andExpect(view().name(PROFILE_PAGE));
    assertNull(httpSession.getAttribute(messageParam));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserSuccessAtEditProfile() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(profileService.editProfile(profileDTO, TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(USER_URL)
                .param(FIRST_NAME_PARAM, profileDTO.getFirstName())
                .param(LAST_NAME_PARAM, profileDTO.getLastName())
                .param(PROVINCE_PARAM, profileDTO.getProvince())
                .param(STREET_PARAM, profileDTO.getStreet())
                .param(HOUSE_NUMBER_PARAM, profileDTO.getHouseNumber())
                .param(APARTMENT_NUMBER_PARAM, profileDTO.getApartmentNumber())
                .param(POSTAL_CODE_PARAM, profileDTO.getPostalCode())
                .param(CITY_PARAM, profileDTO.getCity())
                .param(COUNTRY_PARAM, profileDTO.getCountry())
                .session(httpSession))
        .andExpect(redirectedUrl("/user?success"));
    assertEquals(SUCCESS_UPDATE_MESSAGE, httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtEditProfileWhenValidationProblems() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(profileService.editProfile(profileDTO, TEST_PHONE)).thenReturn(true);

    mockMvc.perform(post(USER_URL).session(httpSession)).andExpect(redirectedUrl(USER_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtEditProfileWhenErrorAtEditProfile() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(profileService.editProfile(profileDTO, TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(USER_URL)
                .param(FIRST_NAME_PARAM, profileDTO.getFirstName())
                .param(LAST_NAME_PARAM, profileDTO.getLastName())
                .param(PROVINCE_PARAM, profileDTO.getProvince())
                .param(STREET_PARAM, profileDTO.getStreet())
                .param(HOUSE_NUMBER_PARAM, profileDTO.getHouseNumber())
                .param(APARTMENT_NUMBER_PARAM, profileDTO.getApartmentNumber())
                .param(POSTAL_CODE_PARAM, profileDTO.getPostalCode())
                .param(CITY_PARAM, profileDTO.getCity())
                .param(COUNTRY_PARAM, profileDTO.getCountry())
                .session(httpSession))
        .andExpect(redirectedUrl(USER_ERROR_URL));
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnChangePasswordPageAtChangePasswordPage() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_CHANGE_PARAM, "12312sdfsdfsdf");

    mockMvc
        .perform(get(PASSWORD_CHANGE_URL).session(httpSession))
        .andExpect(model().attribute(CHANGE_PASSWORD_PARAM, new ChangePasswordDTO()))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(CHANGE_PASSWORD_PAGE));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_CHANGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnChangePasswordPageAtChangePasswordPageWhenParamWithoutMessage()
      throws Exception {
    mockMvc
        .perform(get(PASSWORD_CHANGE_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(CHANGE_PASSWORD_PARAM, new ChangePasswordDTO()))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(CHANGE_PASSWORD_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnChangePasswordPageAtChangePasswordPageWhenParamError() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    mockMvc
        .perform(get(PASSWORD_CHANGE_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(CHANGE_PASSWORD_PARAM, new ChangePasswordDTO()))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(CHANGE_PASSWORD_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserAtChangePasswordPageWhenParamReset() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_CHANGE_PARAM, "12312sdfsdfsdaff");

    mockMvc
        .perform(get(PASSWORD_CHANGE_URL).param(RESET_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(USER_URL));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_CHANGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLoginSuccessAtChangePassword() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.checkPassword(TEST_OLD_PASSWORD, TEST_PHONE)).thenReturn(true);
    when(authenticationService.changePassword(TEST_PHONE, TEST_NEW_PASSWORD)).thenReturn(true);

    redirectUserErrorAndLoginSuccess(httpSession, LOGIN_SUCCESS_URL);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtChangePasswordWhenToManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = new MockHttpSession();
    setSession(maxAttempts, httpSession);

    redirectUserErrorAndLoginSuccess(httpSession, USER_ERROR_URL);
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertEquals(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  private void redirectUserErrorAndLoginSuccess(MockHttpSession httpSession, String redirectUrl)
      throws Exception {
    mockMvc
        .perform(
            post(PASSWORD_CHANGE_URL)
                .param(OLD_PASSWORD_PARAM, TEST_OLD_PASSWORD)
                .param(NEW_PASSWORD_PARAM, TEST_NEW_PASSWORD)
                .param(RETYPED_PASSWORD_PARAM, TEST_NEW_PASSWORD)
                .param(CODE_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToPasswordChangeErrorAtChangePasswordWhenErrorValidation()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    redirectPasswordChangeError(httpSession, ERROR_VALIDATION_MESSAGE, "", "");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToPasswordChangeErrorAtChangePasswordWhenBadCode() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    redirectPasswordChangeError(
        httpSession, ERROR_BAD_SMS_CODE_MESSAGE, TEST_NEW_PASSWORD, TEST_NEW_PASSWORD);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToPasswordChangeErrorAtChangePasswordWhenBadOldPassword()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.checkPassword(TEST_OLD_PASSWORD, TEST_PHONE)).thenReturn(false);

    redirectPasswordChangeError(
        httpSession, "Podane stare hasło jest błędne", TEST_NEW_PASSWORD, TEST_NEW_PASSWORD);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToPasswordChangeErrorAtChangePasswordWhenPasswordsAreNotTheSame()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.checkPassword(TEST_OLD_PASSWORD, TEST_PHONE)).thenReturn(true);

    redirectPasswordChangeError(
        httpSession, "Podane hasła różnią się", TEST_NEW_PASSWORD, "Test123!!!");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToPasswordChangeErrorAtChangePasswordWhenPasswordsAreTheSame()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.checkPassword(TEST_OLD_PASSWORD, TEST_PHONE)).thenReturn(true);

    redirectPasswordChangeError(
        httpSession,
        "Stare hasło i nowe hasło jest takie samo",
        TEST_OLD_PASSWORD,
        TEST_OLD_PASSWORD);
  }

  private void redirectPasswordChangeError(
      MockHttpSession httpSession, String message, String newPassword, String retypedPassword)
      throws Exception {
    mockMvc
        .perform(
            post(PASSWORD_CHANGE_URL)
                .param(OLD_PASSWORD_PARAM, TEST_OLD_PASSWORD)
                .param(NEW_PASSWORD_PARAM, newPassword)
                .param(RETYPED_PASSWORD_PARAM, retypedPassword)
                .param(CODE_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl("/password_change?error"));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(2, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtChangePasswordWhenErrorAtChangePassword()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.checkPassword(TEST_OLD_PASSWORD, TEST_PHONE)).thenReturn(true);
    when(authenticationService.changePassword(TEST_PHONE, TEST_NEW_PASSWORD)).thenReturn(false);

    redirectUserErrorAndLoginSuccess(httpSession, USER_ERROR_URL);
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
  }

  private void setSession(int attempts, MockHttpSession httpSession) {
    httpSession.setAttribute(ATTEMPTS_PARAM, attempts);
    httpSession.setAttribute(CODE_SMS_CHANGE_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnDeleteAccountPageAtDeleteAccountPage() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_DELETE_PARAM, TEST_ENCODE_CODE);

    mockMvc
        .perform(get(DELETE_ACCOUNT_URL).session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(DELETE_ACCOUNT_PAGE));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_DELETE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnDeleteAccountPageAtDeleteAccountPageWhenParamErrorButNoMessage()
      throws Exception {
    mockMvc
        .perform(get(DELETE_ACCOUNT_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(DELETE_ACCOUNT_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnDeleteAccountPageAtDeleteAccountPageWhenParamErrorWithMessage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    mockMvc
        .perform(get(DELETE_ACCOUNT_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(DELETE_ACCOUNT_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnDeleteAccountPageAtDeleteAccountPageWhenParamReset() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_DELETE_PARAM, TEST_ENCODE_CODE);

    mockMvc
        .perform(get(DELETE_ACCOUNT_URL).param(RESET_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(USER_URL));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_DELETE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLoginSuccessAtDeleteAccount() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(CODE_SMS_DELETE_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(profileService.deleteAccount(TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(DELETE_ACCOUNT_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(LOGIN_SUCCESS_URL));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtDeleteAccountWhenTooManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);
    httpSession.setAttribute(CODE_SMS_DELETE_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);

    mockMvc
        .perform(
            post(DELETE_ACCOUNT_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(USER_ERROR_URL));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_DELETE_PARAM));
    assertEquals(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToDeleteAccountErrorAtDeleteAccountWhenErrorAtValidation()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    mockMvc
        .perform(
            post(DELETE_ACCOUNT_URL).param(VERIFICATION_NUMBER_SMS_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(DELETE_ACCOUNT_ERROR_URL));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToDeleteAccountErrorAtDeleteAccountWhenBadCode() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_DELETE_PARAM, TEST_ENCODE_CODE);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    mockMvc
        .perform(
            post(DELETE_ACCOUNT_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(DELETE_ACCOUNT_ERROR_URL));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
    assertEquals(ERROR_BAD_SMS_CODE_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToDeleteAccountErrorAtDeleteAccountWhenErrorAtDeleteAccount()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_DELETE_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(profileService.deleteAccount(TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(DELETE_ACCOUNT_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(USER_ERROR_URL));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_DELETE_PARAM));
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }
}
