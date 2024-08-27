package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryGetDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class CountryServiceTest {
  private static final String COUNTRIES_URL =
      "https://restcountries.com/v3.1/all?fields=idd,cca2,translations,area,population";
  private CountryService countryService;
  @Mock private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    countryService = new CountryService(restTemplate);
  }

  @Test
  public void shouldSuccessGetAll() {
    final String name1 = "Polskaa";
    final String code1 = "PL";
    final String callingCode1 = "+48";
    final String name2 = "vcbsdf";
    final String code2 = "ENG";
    final String callingCode2 = "+5234";
    final String callingCode3 = "+5123";
    final String polCode = "pol";
    final Float area1 = 60000F;
    final int population1 = 1000000;
    final Float area2 = 4563456F;
    final int population2 = 456464566;
    final Float area3 = 10F;
    final int population3 = 456;
    CountryGetDTO.Translation translation1 = new CountryGetDTO.Translation("Polska", name1);
    CountryGetDTO.Translation translation2 = new CountryGetDTO.Translation("SDfsdaf", "dfgadg");
    Map<String, CountryGetDTO.Translation> map1 = new HashMap<>();
    map1.put(polCode, translation1);
    map1.put("eng", translation2);
    String[] suffixes1 = {"8"};
    CountryGetDTO.Idd idd1 = new CountryGetDTO.Idd("+4", suffixes1);
    CountryGetDTO countryGetDTO1 = new CountryGetDTO();
    countryGetDTO1.setArea(area1);
    countryGetDTO1.setPopulation(population1);
    countryGetDTO1.setCca2(code1);
    countryGetDTO1.setTranslations(map1);
    countryGetDTO1.setIdd(idd1);
    CountryGetDTO.Translation translation3 = new CountryGetDTO.Translation("sadfsf", name2);
    Map<String, CountryGetDTO.Translation> map2 = new HashMap<>();
    map2.put(polCode, translation3);
    String[] suffixes2 = {"234", "123"};
    CountryGetDTO.Idd idd2 = new CountryGetDTO.Idd("+5", suffixes2);
    CountryGetDTO countryGetDTO2 = new CountryGetDTO();
    countryGetDTO2.setArea(area2);
    countryGetDTO2.setPopulation(population2);
    countryGetDTO2.setCca2(code2);
    countryGetDTO2.setTranslations(map2);
    countryGetDTO2.setIdd(idd2);
    CountryGetDTO.Translation translation4 = new CountryGetDTO.Translation("dsaffsad", "werqr");
    Map<String, CountryGetDTO.Translation> map3 = new HashMap<>();
    map3.put(polCode, translation4);
    String[] suffixes3 = {"23412", "132423"};
    CountryGetDTO.Idd idd3 = new CountryGetDTO.Idd("+234", suffixes3);
    CountryGetDTO countryGetDTO3 = new CountryGetDTO();
    countryGetDTO3.setArea(area3);
    countryGetDTO3.setPopulation(population3);
    countryGetDTO3.setCca2("SE");
    countryGetDTO3.setTranslations(map3);
    countryGetDTO3.setIdd(idd3);
    CountryGetDTO[] expectedCountriesGET = {countryGetDTO1, countryGetDTO2, countryGetDTO3};
    List<CountryDTO> expectedCountries =
        List.of(
            new CountryDTO(name1, code1, callingCode1),
            new CountryDTO(name2, code2, callingCode2),
            new CountryDTO(name2, code2, callingCode3));

    when(restTemplate.getForObject(COUNTRIES_URL, CountryGetDTO[].class))
        .thenReturn(expectedCountriesGET);

    assertEquals(expectedCountries, countryService.getAll());
  }

  @Test
  public void shouldFailGetAllWhenGetNull() {
    when(restTemplate.getForObject(COUNTRIES_URL, CountryGetDTO[].class)).thenReturn(null);

    assertNull(countryService.getAll());
  }

  @Test
  public void shouldFailGetAllWhenGetException() {
    when(restTemplate.getForObject(COUNTRIES_URL, CountryGetDTO[].class))
        .thenThrow(RestClientException.class);

    assertNull(countryService.getAll());
  }
}
