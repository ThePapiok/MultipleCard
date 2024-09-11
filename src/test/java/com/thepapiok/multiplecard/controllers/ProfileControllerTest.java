package com.thepapiok.multiplecard.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.ProfileService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProfileControllerTest {
  private static final String TEST_PHONE = "+4823232342342342";

  @Autowired private MockMvc mockMvc;
  @MockBean private ProfileService profileService;
  @MockBean private CountryService countryService;

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldSuccessAtGetProfile() throws Exception {
    final String countryName1 = "Poland";
    final String countryName2 = "sdfsdf";
    final String countryCode1 = "PL";
    final String countryCode2 = "ds";
    ProfileDTO profileDTO = new ProfileDTO();
    profileDTO.setPostalCode("postalCode");
    profileDTO.setApartmentNumber("apartmentNumber");
    profileDTO.setCountry("country");
    profileDTO.setCity("city");
    profileDTO.setStreet("street");
    profileDTO.setProvince("province");
    profileDTO.setHouseNumber("houseNumber");
    profileDTO.setFirstName("firstName");
    profileDTO.setLastName("lastName");
    List<CountryDTO> countryDTOS =
        List.of(
            new CountryDTO(countryName1, countryCode1, "+48"),
            new CountryDTO(countryName2, countryCode2, "+123"));
    List<CountryNamesDTO> countryNamesDTOS =
        List.of(
            new CountryNamesDTO(countryName1, countryCode1),
            new CountryNamesDTO(countryName2, countryCode2));

    when(profileService.getProfile(TEST_PHONE)).thenReturn(profileDTO);
    when(countryService.getAll()).thenReturn(countryDTOS);

    mockMvc
        .perform(get("/user"))
        .andExpect(model().attribute("profile", profileDTO))
        .andExpect(model().attribute("countries", countryNamesDTOS))
        .andExpect(view().name("profilePage"));
  }
}
