package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Country;
import com.thepapiok.multiplecard.dto.CountryDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CountryConverter {

  public CountryDTO getDTO(Country country) {
    CountryDTO countryDTO = new CountryDTO();
    countryDTO.setName(country.getName());
    countryDTO.setCode(country.getCode());
    return countryDTO;
  }

  public List<CountryDTO> getDTOs(List<Country> countries) {
    List<CountryDTO> countryDTOs = new ArrayList<>();
    for (Country country : countries) {
      countryDTOs.add(getDTO(country));
    }
    return countryDTOs;
  }
}
