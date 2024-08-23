package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryGetDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CountryService {

  private final RestTemplate restTemplate;

  @Autowired
  public CountryService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<CountryDTO> getAll() {
    final int minPopulation = 1000000;
    final int minArea = 50000;
    CountryGetDTO[] countries;
    try {
      countries =
          restTemplate.getForObject(
              "https://restcountries.com/v3.1/all?fields=idd,cca2,translations,area,population",
              CountryGetDTO[].class);
    } catch (Exception e) {
      return null;
    }
    if (countries == null) {
      return null;
    }
    List<CountryDTO> countryDTOS =
        new ArrayList<>(
            Arrays.stream(countries)
                .filter(e -> e.getPopulation() >= minPopulation && e.getArea() >= minArea)
                .flatMap(
                    country ->
                        Arrays.stream(country.getIdd().getSuffixes())
                            .map(
                                suffix ->
                                    new CountryDTO(
                                        country.getTranslations().get("pol").getCommon(),
                                        country.getCca2(),
                                        country.getIdd().getRoot() + suffix)))
                .toList());
    Collections.sort(countryDTOS);
    return countryDTOS;
  }
}
