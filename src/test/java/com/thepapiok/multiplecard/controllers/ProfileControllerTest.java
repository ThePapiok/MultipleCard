package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
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
  private static final String COUNTRIES_PARAM = "countries";
  private static final String USER_URL = "/user";
  private static final String USER_ERROR_URL = "/user?error";
  private static final String PROFILE_PAGE = "profilePage";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_PARAM = "success";
  private static final String CARD_PARAM = "card";
  private static ProfileDTO profileDTO;
  private static List<CountryDTO> countryDTOS;
  private static List<CountryNamesDTO> countryNamesDTOS;

  @Autowired private MockMvc mockMvc;
  @MockBean private ProfileService profileService;
  @MockBean private CountryService countryService;
  @MockBean private CardService cardService;

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
    final String message = "error!";
    returnProfilePageWithParamAndMessage(
        ERROR_MESSAGE_PARAM, message, "error", ERROR_MESSAGE_PARAM, message);
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
    returnProfilePageWithParamAndMessage(
        SUCCESS_PARAM, true, SUCCESS_PARAM, "successMessage", "Pomyślnie zaktualizowano dane");
  }

  private void returnProfilePageWithParamAndMessage(
      String httpParam, Object valueParam, String param, String messageParam, String message)
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(httpParam, valueParam);
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
    assertNull(httpSession.getAttribute(httpParam));
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
    assertTrue((Boolean) httpSession.getAttribute(SUCCESS_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToUserErrorAtEditProfileWhenValidationProblems() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();

    when(profileService.editProfile(profileDTO, TEST_PHONE)).thenReturn(true);

    mockMvc.perform(post(USER_URL).session(httpSession)).andExpect(redirectedUrl(USER_ERROR_URL));
    assertEquals("Podane dane są niepoprawne", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
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
    assertEquals("Nieoczekiwany błąd", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }
}
