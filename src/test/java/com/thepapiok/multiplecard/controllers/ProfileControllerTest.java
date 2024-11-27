package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.ChangePasswordDTO;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.ProfileService;
import com.thepapiok.multiplecard.services.ShopService;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProfileControllerTest {
  private static final String TEST_PHONE = "+4823232342342342";
  private static final String SHOP_NAME_PARAM = "name";
  private static final String ACCOUNT_NUMBER_PARAM = "accountNumber";
  private static final String IMAGE_URL_PARAM = "imageUrl";
  private static final String TOTAL_AMOUNT_PARAM = "totalAmount";
  private static final String FIRST_NAME_PARAM = "firstName";
  private static final String LAST_NAME_PARAM = "lastName";
  private static final String PROVINCE_PARAM = "province";
  private static final String STREET_PARAM = "street";
  private static final String HOUSE_NUMBER_PARAM = "houseNumber";
  private static final String APARTMENT_NUMBER_PARAM = "apartmentNumber";
  private static final String POSTAL_CODE_PARAM = "postalCode";
  private static final String CITY_PARAM = "city";
  private static final String COUNTRY_PARAM = "country";
  private static final String COUNTRIES_PARAM = "countries";
  private static final String SHOP_URL = "/shop";
  private static final String PROFILE_URL = "/profile";
  private static final String PASSWORD_CHANGE_URL = "/password_change";
  private static final String EDIT_PROFILE_URL = "/edit_profile";
  private static final String EDIT_PROFILE_ERROR_URL = "/edit_profile?error";
  private static final String PROFILE_ERROR_URL = "/profile?error";
  private static final String DELETE_ACCOUNT_URL = "/delete_account";
  private static final String DELETE_ACCOUNT_PAGE = "deleteAccountPage";
  private static final String EDIT_PROFILE_PAGE = "editProfilePage";
  private static final String EDIT_PROFILE_SHOP_PAGE = "editProfileShopPage";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String EDIT_PARAM = "edit";
  private static final String ERROR_MESSAGE = "error!";
  private static final String SUCCESS_UPDATE_MESSAGE = "Pomyślnie zaktualizowano dane";
  private static final String ERROR_UNEXPECTED_MESSAGE = "Nieoczekiwany błąd";
  private static final String ERROR_TOO_MANY_ATTEMPTS_MESSAGE =
      "Za dużo razy podałeś niepoprawne dane";
  private static final String ERROR_BAD_SMS_CODE_MESSAGE = "Nieprawidłowy kod sms";
  private static final String ERROR_PARAM = "error";
  private static final String FILE_NAME = "file";
  private static final String RESET_PARAM = "reset";
  private static final String EDIT_SHOP_PARAM = "editShop";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String VERIFICATION_NUMBER_SMS_PARAM = "verificationNumberSms";
  private static final String PHONE_PARAM = "phone";
  private static final String PREFIX_ADDRESS1_PARAM = "address[0].";
  private static final String PREFIX_ADDRESS2_PARAM = "address[1].";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  private static final String CODE_SMS_CHANGE_PARAM = "codeSmsChange";
  private static final String CODE_SMS_DELETE_PARAM = "codeSmsDelete";
  private static final String CODE_SMS_EDIT_PARAM = "codeSmsEdit";
  private static final String CODE_SMS_EDIT_SHOP_PARAM = "codeSmsEditShop";
  private static final String CHANGE_PASSWORD_PARAM = "changePassword";
  private static final String CHANGE_PASSWORD_PAGE = "changePasswordPage";
  private static final String ERROR_VALIDATION_MESSAGE = "Podane dane są niepoprawne";
  private static final String TEST_CODE = "123 123";
  private static final String TEST_URL = "url";
  private static final String TEST_CONTENT_TYPE = "text/plain";
  private static final String TEST_OLD_PASSWORD = "Test123!";
  private static final String TEST_NEW_PASSWORD = "Test123!!";
  private static final String TEST_ENCODE_CODE = "312fdasfdsaffsd";
  private static final String PARAM_ADDRESS_PREFIX = "address.";
  private static ProfileDTO profileDTO;
  private static ProfileShopDTO profileShopDTO;
  private static MockMultipartFile file1;
  private static MockMultipartFile file2;
  private static List<CountryDTO> countryDTOS;
  private static List<CountryNamesDTO> countryNamesDTOS;
  @Autowired private MockMvc mockMvc;
  @MockBean private ProfileService profileService;
  @MockBean private CountryService countryService;
  @MockBean private CardService cardService;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private AuthenticationService authenticationService;
  @MockBean private ShopService shopService;

  @BeforeAll
  public static void setUp() {
    final String countryName1 = "Poland";
    final String countryName2 = "sdfsdf";
    final String countryCode1 = "PL";
    final String countryCode2 = "ds";
    final int size = 5;
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("11-111");
    addressDTO.setApartmentNumber("2");
    addressDTO.setCity("City");
    addressDTO.setCountry("Country");
    addressDTO.setStreet("Street");
    addressDTO.setProvince("Province");
    addressDTO.setHouseNumber("1");
    file1 = new MockMultipartFile(FILE_NAME, "file1", TEST_CONTENT_TYPE, new byte[size]);
    file2 = new MockMultipartFile(FILE_NAME, "file2", TEST_CONTENT_TYPE, new byte[size]);
    profileDTO = new ProfileDTO();
    profileDTO.setAddress(addressDTO);
    profileDTO.setFirstName("Firstname");
    profileDTO.setLastName("Lastname");
    profileShopDTO = new ProfileShopDTO();
    profileShopDTO.setTotalAmount("3");
    profileShopDTO.setName("shopName");
    profileShopDTO.setAccountNumber("12312312312321312312312333");
    profileShopDTO.setImageUrl(TEST_URL);
    profileShopDTO.setAddress(List.of(addressDTO));
    profileShopDTO.setFirstName("Firstnameshop");
    profileShopDTO.setLastName("Lastnameshop");
    profileShopDTO.setFile(file1);
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
  public void shouldReturnProfilePageAtGetProfileUserWhenEverythingOk() throws Exception {
    successReturnProfilePage();
  }

  private void successReturnProfilePage() throws Exception {
    Card card = new Card();

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);
    when(cardService.getCard(TEST_PHONE)).thenReturn(card);
    when(profileService.getProfile(TEST_PHONE)).thenReturn(profileDTO);
    when(countryService.getAll()).thenReturn(countryDTOS);

    mockMvc
        .perform(get(PROFILE_URL))
        .andExpect(model().attribute("card", card))
        .andExpect(model().attribute("profile", profileDTO))
        .andExpect(model().attribute(COUNTRIES_PARAM, countryNamesDTOS))
        .andExpect(view().name("profilePage"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void successReturnProfileShopPageAtGetProfileShopWhenEverythingOk() throws Exception {
    ProfileShopDTO profileShopDTO = new ProfileShopDTO();

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);
    when(profileService.getShop(TEST_PHONE)).thenReturn(profileShopDTO);
    when(countryService.getAll()).thenReturn(countryDTOS);

    mockMvc
        .perform(get(PROFILE_URL))
        .andExpect(model().attribute("profileShop", profileShopDTO))
        .andExpect(model().attribute(COUNTRIES_PARAM, countryNamesDTOS))
        .andExpect(view().name("profileShopPage"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageWithErrorAtGetProfileWhenParamErrorAndMessage()
      throws Exception {
    returnProfilePageWithParamAndMessage(ERROR_PARAM, ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageWithErrorAtGetProfileWhenParamErrorAndError501()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, "success!");

    mockMvc
        .perform(get(PROFILE_URL).param(ERROR_PARAM, "501").session(httpSession))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals("Anulowano zakup nowej karty", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageWithErrorAtGetProfileWhenParamErrorWithoutMessage()
      throws Exception {
    successReturnProfilePage();
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnProfilePageWithSuccessAtGetProfileWhenParamSuccessAndMessage()
      throws Exception {
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
        .perform(get(PROFILE_URL).param(param, "").session(httpSession))
        .andExpect(model().attribute("card", card))
        .andExpect(model().attribute("profile", profileDTO))
        .andExpect(model().attribute(COUNTRIES_PARAM, countryNamesDTOS))
        .andExpect(model().attribute(messageParam, message))
        .andExpect(view().name("profilePage"));
    assertNull(httpSession.getAttribute(messageParam));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToEditProfileAtEditProfileWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    mockMvc
        .perform(
            post("/user")
                .param(FIRST_NAME_PARAM, profileDTO.getFirstName())
                .param(LAST_NAME_PARAM, profileDTO.getLastName())
                .param(PARAM_ADDRESS_PREFIX + PROVINCE_PARAM, profileDTO.getAddress().getProvince())
                .param(PARAM_ADDRESS_PREFIX + STREET_PARAM, profileDTO.getAddress().getStreet())
                .param(
                    PARAM_ADDRESS_PREFIX + HOUSE_NUMBER_PARAM,
                    profileDTO.getAddress().getHouseNumber())
                .param(
                    PARAM_ADDRESS_PREFIX + APARTMENT_NUMBER_PARAM,
                    profileDTO.getAddress().getApartmentNumber())
                .param(
                    PARAM_ADDRESS_PREFIX + POSTAL_CODE_PARAM,
                    profileDTO.getAddress().getPostalCode())
                .param(PARAM_ADDRESS_PREFIX + CITY_PARAM, profileDTO.getAddress().getCity())
                .param(PARAM_ADDRESS_PREFIX + COUNTRY_PARAM, profileDTO.getAddress().getCountry())
                .session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_URL));
    assertEquals(profileDTO, httpSession.getAttribute(EDIT_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtEditProfileWhenValidationProblems() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    mockMvc.perform(post("/user").session(httpSession)).andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenErrorAtValidation()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    performPostEditProfileShop(httpSession, profileShopDTO, "!", PROFILE_ERROR_URL);
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenShopNameExists() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(true);

    performPostEditProfileShop(
        httpSession,
        profileShopDTO,
        profileShopDTO.getAddress().get(0).getCity(),
        PROFILE_ERROR_URL);
    assertEquals("Taka nazwa lokalu już istnieje", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenAccountNumberExists()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(true);

    performPostEditProfileShop(
        httpSession,
        profileShopDTO,
        profileShopDTO.getAddress().get(0).getCity(),
        PROFILE_ERROR_URL);
    assertEquals(
        "Taki numer rachunku bankowego już istnieje",
        httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenBadAccountNumber() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(false);
    when(shopService.checkAccountNumber(profileShopDTO.getAccountNumber())).thenReturn(false);

    performPostEditProfileShop(
        httpSession,
        profileShopDTO,
        profileShopDTO.getAddress().get(0).getCity(),
        PROFILE_ERROR_URL);
    assertEquals("Zły numer rachunku bankowego", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenBadSize() throws Exception {
    final String prefixAddress3 = "address[2].";
    final String prefixAddress4 = "address[3].";
    final String prefixAddress5 = "address[4].";
    final String prefixAddress6 = "address[5].";
    MockHttpSession httpSession = new MockHttpSession();
    AddressDTO addressDTO1 = profileShopDTO.getAddress().get(0);
    AddressDTO addressDTO2 = new AddressDTO();
    AddressDTO addressDTO3 = new AddressDTO();
    AddressDTO addressDTO4 = new AddressDTO();
    AddressDTO addressDTO5 = new AddressDTO();
    AddressDTO addressDTO6 = new AddressDTO();
    addressDTO2.setCity("Cityo");
    addressDTO2.setCountry("Countryo");
    addressDTO2.setApartmentNumber("12");
    addressDTO2.setStreet("Streeto");
    addressDTO2.setProvince("Provinceo");
    addressDTO2.setHouseNumber("12B");
    addressDTO2.setPostalCode("11-121");
    addressDTO3.setCity("Cityt");
    addressDTO3.setCountry("Countryt");
    addressDTO3.setApartmentNumber("13");
    addressDTO3.setStreet("Streett");
    addressDTO3.setProvince("Provincet");
    addressDTO3.setHouseNumber("13B");
    addressDTO3.setPostalCode("11-131");
    addressDTO4.setCity("Citytr");
    addressDTO4.setCountry("Countrytr");
    addressDTO4.setApartmentNumber("14");
    addressDTO4.setStreet("Streettr");
    addressDTO4.setProvince("Provincetr");
    addressDTO4.setHouseNumber("14B");
    addressDTO4.setPostalCode("11-141");
    addressDTO5.setCity("Cityf");
    addressDTO5.setCountry("Countryf");
    addressDTO5.setApartmentNumber("15");
    addressDTO5.setStreet("Streetf");
    addressDTO5.setProvince("Provincef");
    addressDTO5.setHouseNumber("15B");
    addressDTO5.setPostalCode("11-151");
    addressDTO6.setCity("Cityfi");
    addressDTO6.setCountry("Countryfi");
    addressDTO6.setApartmentNumber("16");
    addressDTO6.setStreet("Streetfi");
    addressDTO6.setProvince("Provincefi");
    addressDTO6.setHouseNumber("16B");
    addressDTO6.setPostalCode("11-161");

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(false);
    when(shopService.checkAccountNumber(profileShopDTO.getAccountNumber())).thenReturn(true);

    mockMvc
        .perform(
            multipart(SHOP_URL)
                .file((MockMultipartFile) profileShopDTO.getFile())
                .session(httpSession)
                .param(FIRST_NAME_PARAM, profileShopDTO.getFirstName())
                .param(LAST_NAME_PARAM, profileShopDTO.getLastName())
                .param(SHOP_NAME_PARAM, profileShopDTO.getName())
                .param(ACCOUNT_NUMBER_PARAM, profileShopDTO.getAccountNumber())
                .param(IMAGE_URL_PARAM, profileShopDTO.getImageUrl())
                .param(TOTAL_AMOUNT_PARAM, profileShopDTO.getTotalAmount())
                .param(PREFIX_ADDRESS1_PARAM + CITY_PARAM, addressDTO1.getCity())
                .param(PREFIX_ADDRESS1_PARAM + STREET_PARAM, addressDTO1.getStreet())
                .param(PREFIX_ADDRESS1_PARAM + COUNTRY_PARAM, addressDTO1.getCountry())
                .param(PREFIX_ADDRESS1_PARAM + HOUSE_NUMBER_PARAM, addressDTO1.getHouseNumber())
                .param(PREFIX_ADDRESS1_PARAM + POSTAL_CODE_PARAM, addressDTO1.getPostalCode())
                .param(
                    PREFIX_ADDRESS1_PARAM + APARTMENT_NUMBER_PARAM,
                    addressDTO1.getApartmentNumber())
                .param(PREFIX_ADDRESS1_PARAM + PROVINCE_PARAM, addressDTO1.getProvince())
                .param(PREFIX_ADDRESS2_PARAM + CITY_PARAM, addressDTO2.getCity())
                .param(PREFIX_ADDRESS2_PARAM + STREET_PARAM, addressDTO2.getStreet())
                .param(PREFIX_ADDRESS2_PARAM + COUNTRY_PARAM, addressDTO2.getCountry())
                .param(PREFIX_ADDRESS2_PARAM + HOUSE_NUMBER_PARAM, addressDTO2.getHouseNumber())
                .param(PREFIX_ADDRESS2_PARAM + POSTAL_CODE_PARAM, addressDTO2.getPostalCode())
                .param(
                    PREFIX_ADDRESS2_PARAM + APARTMENT_NUMBER_PARAM,
                    addressDTO2.getApartmentNumber())
                .param(PREFIX_ADDRESS2_PARAM + PROVINCE_PARAM, addressDTO2.getProvince())
                .param(prefixAddress3 + CITY_PARAM, addressDTO3.getCity())
                .param(prefixAddress3 + STREET_PARAM, addressDTO3.getStreet())
                .param(prefixAddress3 + COUNTRY_PARAM, addressDTO3.getCountry())
                .param(prefixAddress3 + HOUSE_NUMBER_PARAM, addressDTO3.getHouseNumber())
                .param(prefixAddress3 + POSTAL_CODE_PARAM, addressDTO3.getPostalCode())
                .param(prefixAddress3 + APARTMENT_NUMBER_PARAM, addressDTO3.getApartmentNumber())
                .param(prefixAddress3 + PROVINCE_PARAM, addressDTO3.getProvince())
                .param(prefixAddress4 + CITY_PARAM, addressDTO4.getCity())
                .param(prefixAddress4 + STREET_PARAM, addressDTO4.getStreet())
                .param(prefixAddress4 + COUNTRY_PARAM, addressDTO4.getCountry())
                .param(prefixAddress4 + HOUSE_NUMBER_PARAM, addressDTO4.getHouseNumber())
                .param(prefixAddress4 + POSTAL_CODE_PARAM, addressDTO4.getPostalCode())
                .param(prefixAddress4 + APARTMENT_NUMBER_PARAM, addressDTO4.getApartmentNumber())
                .param(prefixAddress4 + PROVINCE_PARAM, addressDTO4.getProvince())
                .param(prefixAddress5 + CITY_PARAM, addressDTO5.getCity())
                .param(prefixAddress5 + STREET_PARAM, addressDTO5.getStreet())
                .param(prefixAddress5 + COUNTRY_PARAM, addressDTO5.getCountry())
                .param(prefixAddress5 + HOUSE_NUMBER_PARAM, addressDTO5.getHouseNumber())
                .param(prefixAddress5 + POSTAL_CODE_PARAM, addressDTO5.getPostalCode())
                .param(prefixAddress5 + APARTMENT_NUMBER_PARAM, addressDTO5.getApartmentNumber())
                .param(prefixAddress5 + PROVINCE_PARAM, addressDTO5.getProvince())
                .param(prefixAddress6 + CITY_PARAM, addressDTO6.getCity())
                .param(prefixAddress6 + STREET_PARAM, addressDTO6.getStreet())
                .param(prefixAddress6 + COUNTRY_PARAM, addressDTO6.getCountry())
                .param(prefixAddress6 + HOUSE_NUMBER_PARAM, addressDTO6.getHouseNumber())
                .param(prefixAddress6 + POSTAL_CODE_PARAM, addressDTO6.getPostalCode())
                .param(prefixAddress6 + APARTMENT_NUMBER_PARAM, addressDTO6.getApartmentNumber())
                .param(prefixAddress6 + PROVINCE_PARAM, addressDTO6.getProvince()))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals("Nieprawidłowa ilość lokali", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenPointsNotUnique() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    AddressDTO addressDTO1 = profileShopDTO.getAddress().get(0);
    AddressDTO addressDTO2 = new AddressDTO();
    addressDTO2.setCity(addressDTO1.getCity());
    addressDTO2.setCountry(addressDTO1.getCountry());
    addressDTO2.setApartmentNumber(addressDTO1.getApartmentNumber());
    addressDTO2.setStreet(addressDTO1.getStreet());
    addressDTO2.setProvince(addressDTO1.getProvince());
    addressDTO2.setHouseNumber(addressDTO1.getHouseNumber());
    addressDTO2.setPostalCode(addressDTO1.getPostalCode());

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(false);
    when(shopService.checkAccountNumber(profileShopDTO.getAccountNumber())).thenReturn(true);

    mockMvc
        .perform(
            multipart(SHOP_URL)
                .file((MockMultipartFile) profileShopDTO.getFile())
                .session(httpSession)
                .param(FIRST_NAME_PARAM, profileShopDTO.getFirstName())
                .param(LAST_NAME_PARAM, profileShopDTO.getLastName())
                .param(SHOP_NAME_PARAM, profileShopDTO.getName())
                .param(ACCOUNT_NUMBER_PARAM, profileShopDTO.getAccountNumber())
                .param(IMAGE_URL_PARAM, profileShopDTO.getImageUrl())
                .param(TOTAL_AMOUNT_PARAM, profileShopDTO.getTotalAmount())
                .param(PREFIX_ADDRESS1_PARAM + CITY_PARAM, addressDTO1.getCity())
                .param(PREFIX_ADDRESS1_PARAM + STREET_PARAM, addressDTO1.getStreet())
                .param(PREFIX_ADDRESS1_PARAM + COUNTRY_PARAM, addressDTO1.getCountry())
                .param(PREFIX_ADDRESS1_PARAM + HOUSE_NUMBER_PARAM, addressDTO1.getHouseNumber())
                .param(PREFIX_ADDRESS1_PARAM + POSTAL_CODE_PARAM, addressDTO1.getPostalCode())
                .param(
                    PREFIX_ADDRESS1_PARAM + APARTMENT_NUMBER_PARAM,
                    addressDTO1.getApartmentNumber())
                .param(PREFIX_ADDRESS1_PARAM + PROVINCE_PARAM, addressDTO1.getProvince())
                .param(PREFIX_ADDRESS2_PARAM + CITY_PARAM, addressDTO2.getCity())
                .param(PREFIX_ADDRESS2_PARAM + STREET_PARAM, addressDTO2.getStreet())
                .param(PREFIX_ADDRESS2_PARAM + COUNTRY_PARAM, addressDTO2.getCountry())
                .param(PREFIX_ADDRESS2_PARAM + HOUSE_NUMBER_PARAM, addressDTO2.getHouseNumber())
                .param(PREFIX_ADDRESS2_PARAM + POSTAL_CODE_PARAM, addressDTO2.getPostalCode())
                .param(
                    PREFIX_ADDRESS2_PARAM + APARTMENT_NUMBER_PARAM,
                    addressDTO2.getApartmentNumber())
                .param(PREFIX_ADDRESS2_PARAM + PROVINCE_PARAM, addressDTO2.getProvince()))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals("Lokale muszą być unikalne", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenErrorAtCheckImage()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(false);
    when(shopService.checkAccountNumber(profileShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkImage(profileShopDTO.getFile())).thenReturn(false);

    performPostEditProfileShop(
        httpSession,
        profileShopDTO,
        profileShopDTO.getAddress().get(0).getCity(),
        PROFILE_ERROR_URL);
    assertEquals("Niepoprawny plik", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtEditProfileShopWhenErrorAtSaveTempFile()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(false);
    when(shopService.checkAccountNumber(profileShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkImage(profileShopDTO.getFile())).thenReturn(true);
    when(shopService.saveTempFile(profileShopDTO.getFile())).thenReturn(null);

    performPostEditProfileShop(
        httpSession,
        profileShopDTO,
        profileShopDTO.getAddress().get(0).getCity(),
        PROFILE_ERROR_URL);
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToEditProfileAtEditProfileShopWithFileWhenEverythingOk()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(false);
    when(shopService.checkAccountNumber(profileShopDTO.getAccountNumber())).thenReturn(true);
    when(shopService.checkImage(profileShopDTO.getFile())).thenReturn(true);
    when(shopService.saveTempFile(profileShopDTO.getFile())).thenReturn("wwww.cossdf");

    performPostEditProfileShop(
        httpSession,
        profileShopDTO,
        profileShopDTO.getAddress().get(0).getCity(),
        EDIT_PROFILE_URL);
    assertEquals(profileShopDTO, httpSession.getAttribute(EDIT_SHOP_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToEditProfileAtEditProfileShopWithoutFileWhenEverythingOk()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    ProfileShopDTO profileShopDTO1 = new ProfileShopDTO();
    profileShopDTO1.setName(profileShopDTO.getName());
    profileShopDTO1.setAddress(profileShopDTO.getAddress());
    profileShopDTO1.setAccountNumber(profileShopDTO.getAccountNumber());
    profileShopDTO1.setImageUrl(profileShopDTO.getImageUrl());
    profileShopDTO1.setTotalAmount(profileShopDTO.getTotalAmount());
    profileShopDTO1.setFirstName(profileShopDTO.getFirstName());
    profileShopDTO1.setLastName(profileShopDTO.getLastName());
    MockMultipartFile multipartFile = new MockMultipartFile(FILE_NAME, new byte[0]);
    profileShopDTO1.setFile(multipartFile);

    when(shopService.checkShopNameExists(profileShopDTO.getName(), TEST_PHONE)).thenReturn(false);
    when(shopService.checkAccountNumberExists(profileShopDTO.getAccountNumber(), TEST_PHONE))
        .thenReturn(false);
    when(shopService.checkAccountNumber(profileShopDTO.getAccountNumber())).thenReturn(true);

    performPostEditProfileShop(
        httpSession,
        profileShopDTO1,
        profileShopDTO1.getAddress().get(0).getCity(),
        EDIT_PROFILE_URL);
    assertEquals(profileShopDTO1, httpSession.getAttribute(EDIT_SHOP_PARAM));
  }

  private void performPostEditProfileShop(
      MockHttpSession httpSession, ProfileShopDTO profileShopDTO, String city, String redirectUrl)
      throws Exception {
    AddressDTO addressDTO = profileShopDTO.getAddress().get(0);

    mockMvc
        .perform(
            multipart(SHOP_URL)
                .file((MockMultipartFile) profileShopDTO.getFile())
                .session(httpSession)
                .param(FIRST_NAME_PARAM, profileShopDTO.getFirstName())
                .param(LAST_NAME_PARAM, profileShopDTO.getLastName())
                .param(SHOP_NAME_PARAM, profileShopDTO.getName())
                .param(ACCOUNT_NUMBER_PARAM, profileShopDTO.getAccountNumber())
                .param(IMAGE_URL_PARAM, profileShopDTO.getImageUrl())
                .param(TOTAL_AMOUNT_PARAM, profileShopDTO.getTotalAmount())
                .param(PREFIX_ADDRESS1_PARAM + CITY_PARAM, city)
                .param(PREFIX_ADDRESS1_PARAM + STREET_PARAM, addressDTO.getStreet())
                .param(PREFIX_ADDRESS1_PARAM + COUNTRY_PARAM, addressDTO.getCountry())
                .param(PREFIX_ADDRESS1_PARAM + HOUSE_NUMBER_PARAM, addressDTO.getHouseNumber())
                .param(PREFIX_ADDRESS1_PARAM + POSTAL_CODE_PARAM, addressDTO.getPostalCode())
                .param(
                    PREFIX_ADDRESS1_PARAM + APARTMENT_NUMBER_PARAM, addressDTO.getApartmentNumber())
                .param(PREFIX_ADDRESS1_PARAM + PROVINCE_PARAM, addressDTO.getProvince()))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileAtVerificationEditProfilePageWhenNoEditParamAndUserRole()
      throws Exception {
    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);

    mockMvc.perform(get(EDIT_PROFILE_URL)).andExpect(redirectedUrl(PROFILE_URL));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldReturnEditProfilePageAtVerificationEditProfilePageWhenErrorParamWithoutMessageAndUserRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_PARAM, profileDTO);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(EDIT_PROFILE_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldReturnEditProfilePageAtVerificationEditProfilePageWhenErrorParamWithMessageAndUserRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_PARAM, profileDTO);
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(EDIT_PROFILE_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileAtVerificationEditProfilePageWhenResetParamAndUserRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_PARAM, profileDTO);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    httpSession.setAttribute(CODE_SMS_EDIT_PARAM, TEST_ENCODE_CODE);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).param(RESET_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(PROFILE_URL));
    assertNull(httpSession.getAttribute(EDIT_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnEditProfilePageAtVerificationEditProfilePageWhenUserRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_PARAM, profileDTO);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(EDIT_PROFILE_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileAtVerificationEditProfilePageWhenNoEditShopParamAndShopRole()
      throws Exception {
    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc.perform(get(EDIT_PROFILE_URL)).andExpect(redirectedUrl(PROFILE_URL));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldReturnEditProfilePageAtVerificationEditProfilePageWhenErrorParamWithoutMessageAndShopRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(EDIT_PROFILE_SHOP_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldReturnEditProfilePageAtVerificationEditProfilePageWhenErrorParamWithMessageAndShopRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(EDIT_PROFILE_SHOP_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileAtVerificationEditProfilePageWhenResetParamAndShopRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).param(RESET_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(PROFILE_URL));
    assertNull(httpSession.getAttribute(EDIT_SHOP_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_SHOP_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnEditProfileShopPageAtVerificationEditProfilePageWhenShopRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc
        .perform(get(EDIT_PROFILE_URL).session(httpSession))
        .andExpect(model().attribute(PHONE_PARAM, TEST_PHONE))
        .andExpect(view().name(EDIT_PROFILE_SHOP_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtVerificationEditProfileWhenTooManyAttemptsAndRoleUser()
      throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);
    httpSession.setAttribute(EDIT_PARAM, profileDTO);
    httpSession.setAttribute(CODE_SMS_EDIT_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);

    mockMvc
        .perform(
            post(EDIT_PROFILE_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(EDIT_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldRedirectEditProfileErrorAtVerificationEditProfileWhenErrorAtValidationAndUserRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);

    mockMvc
        .perform(
            post(EDIT_PROFILE_URL).param(VERIFICATION_NUMBER_SMS_PARAM, "").session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectEditProfileErrorAtVerificationEditProfileWhenBadCodeAndUserRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_EDIT_PARAM, TEST_ENCODE_CODE);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    mockMvc
        .perform(
            post(EDIT_PROFILE_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_ERROR_URL));
    assertEquals(ERROR_BAD_SMS_CODE_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileSuccessAtVerificationEditProfileWhenUserRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_EDIT_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(EDIT_PARAM, profileDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(profileService.editProfile(profileDTO, TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(EDIT_PROFILE_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl("/profile?success"));
    assertEquals(SUCCESS_UPDATE_MESSAGE, httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(EDIT_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldRedirectToProfileErrorAtVerificationEditProfileWhenErrorAtEditProfileAndUserRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_EDIT_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(EDIT_PARAM, profileDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(false);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(profileService.editProfile(profileDTO, TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(EDIT_PROFILE_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(EDIT_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileErrorAtVerificationEditProfileWhenTooManyAttemptsAndRoleShop()
      throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, maxAttempts);
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .file("file[0]", file1.getBytes())
                .file("file[1]", file2.getBytes())
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(EDIT_SHOP_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_SHOP_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToEditProfileErrorAtVerificationEditProfileWhenNullFileAndRoleShop()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldRedirectEditProfileErrorAtVerificationEditProfileWhenErrorAtValidationAndShopRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .file(file1)
                .file(file2)
                .param(VERIFICATION_NUMBER_SMS_PARAM, "")
                .session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_ERROR_URL));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectEditProfileErrorAtVerificationEditProfileWhenBadCodeAndShopRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(false);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .file(file1)
                .file(file2)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_ERROR_URL));
    assertEquals(ERROR_BAD_SMS_CODE_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectEditProfileErrorAtVerificationEditProfileWhenBadSizeAndShopRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);
    MockMultipartFile file3 =
        new MockMultipartFile(FILE_NAME, "file3", TEST_CONTENT_TYPE, new byte[0]);
    MockMultipartFile file4 =
        new MockMultipartFile(FILE_NAME, "file4", TEST_CONTENT_TYPE, new byte[0]);
    MockMultipartFile file5 =
        new MockMultipartFile(FILE_NAME, "file5", TEST_CONTENT_TYPE, new byte[0]);
    MockMultipartFile file6 =
        new MockMultipartFile(FILE_NAME, "file6", TEST_CONTENT_TYPE, new byte[0]);
    MockMultipartFile file7 =
        new MockMultipartFile(FILE_NAME, "file7", TEST_CONTENT_TYPE, new byte[0]);
    MockMultipartFile file8 =
        new MockMultipartFile(FILE_NAME, "file8", TEST_CONTENT_TYPE, new byte[0]);
    MockMultipartFile file9 =
        new MockMultipartFile(FILE_NAME, "file9", TEST_CONTENT_TYPE, new byte[0]);
    MockMultipartFile file10 =
        new MockMultipartFile(FILE_NAME, "file10", TEST_CONTENT_TYPE, new byte[0]);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .file(file1)
                .file(file2)
                .file(file3)
                .file(file4)
                .file(file5)
                .file(file6)
                .file(file7)
                .file(file8)
                .file(file9)
                .file(file10)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_ERROR_URL));
    assertEquals(
        "Nieprawidłowa ilość przesłanych plików", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldRedirectEditProfileErrorAtVerificationEditProfileWhenErrorAtCheckFilesAndShopRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(shopService.checkFiles(List.of(file1, file2))).thenReturn(false);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .file(file1)
                .file(file2)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(EDIT_PROFILE_ERROR_URL));
    assertEquals("Nieprawidłowe pliki", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertEquals(1, httpSession.getAttribute(ATTEMPTS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void
      shouldRedirectProfileErrorAtVerificationEditProfileWhenErrorAtEditProfileShopAndShopRole()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    httpSession.setAttribute("filePath", TEST_URL);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(shopService.checkFiles(List.of(file1, file2))).thenReturn(true);
    when(profileService.editProfileShop(
            eq(profileShopDTO), eq(TEST_URL), any(), eq(List.of(file1, file2)), eq(TEST_PHONE)))
        .thenReturn(false);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .file(file1)
                .file(file2)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(EDIT_SHOP_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_SHOP_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToProfileSuccessAtVerificationEditProfileWhenShopRole()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    httpSession.setAttribute(CODE_SMS_EDIT_SHOP_PARAM, TEST_ENCODE_CODE);
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShopDTO);
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
    httpSession.setAttribute("filePath", TEST_URL);

    when(profileService.checkRole(TEST_PHONE, Role.ROLE_SHOP)).thenReturn(true);
    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(shopService.checkFiles(List.of(file1, file2))).thenReturn(true);
    when(profileService.editProfileShop(
            eq(profileShopDTO), eq(TEST_URL), any(), eq(List.of(file1, file2)), eq(TEST_PHONE)))
        .thenReturn(true);

    mockMvc
        .perform(
            multipart(EDIT_PROFILE_URL)
                .file(file1)
                .file(file2)
                .param(VERIFICATION_NUMBER_SMS_PARAM, TEST_CODE)
                .session(httpSession))
        .andExpect(redirectedUrl("/profile?success"));
    assertEquals(SUCCESS_UPDATE_MESSAGE, httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(EDIT_SHOP_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_EDIT_SHOP_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnChangePasswordPageAtChangePasswordPageWhenEverythingOk()
      throws Exception {
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
        .andExpect(redirectedUrl(PROFILE_URL));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_CHANGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLoginSuccessAtChangePasswordWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setSession(1, httpSession);

    when(passwordEncoder.matches(TEST_CODE, TEST_ENCODE_CODE)).thenReturn(true);
    when(authenticationService.checkPassword(TEST_OLD_PASSWORD, TEST_PHONE)).thenReturn(true);
    when(authenticationService.changePassword(TEST_PHONE, TEST_NEW_PASSWORD)).thenReturn(true);

    redirectUserErrorAndLoginSuccess(httpSession, "/login?success");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtChangePasswordWhenToManyAttempts() throws Exception {
    final int maxAttempts = 3;
    MockHttpSession httpSession = new MockHttpSession();
    setSession(maxAttempts, httpSession);

    redirectUserErrorAndLoginSuccess(httpSession, PROFILE_ERROR_URL);
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
                .param("oldPassword", TEST_OLD_PASSWORD)
                .param("password", TEST_NEW_PASSWORD)
                .param("retypedPassword", TEST_NEW_PASSWORD)
                .param("code", TEST_CODE)
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
                .param("oldPassword", TEST_OLD_PASSWORD)
                .param("password", newPassword)
                .param("retypedPassword", retypedPassword)
                .param("code", TEST_CODE)
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

    redirectUserErrorAndLoginSuccess(httpSession, PROFILE_ERROR_URL);
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
  public void shouldReturnDeleteAccountPageAtDeleteAccountPageWhenEverythingOk() throws Exception {
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
        .andExpect(redirectedUrl(PROFILE_URL));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_DELETE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLoginSuccessAtDeleteAccountWhenEverythingOk() throws Exception {
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
        .andExpect(redirectedUrl("/login?success"));
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
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
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
        .andExpect(redirectedUrl("/delete_account?error"));
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
        .andExpect(redirectedUrl("/delete_account?error"));
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
        .andExpect(redirectedUrl(PROFILE_ERROR_URL));
    assertNull(httpSession.getAttribute(ATTEMPTS_PARAM));
    assertNull(httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM));
    assertNull(httpSession.getAttribute(CODE_SMS_DELETE_PARAM));
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }
}
