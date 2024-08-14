package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Country;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.misc.CountryConverter;
import com.thepapiok.multiplecard.repositories.CountryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryService {

  private final CountryRepository countryRepository;
  private final CountryConverter countryConverter;

  @Autowired
  public CountryService(CountryRepository countryRepository, CountryConverter countryConverter) {
    this.countryRepository = countryRepository;
    this.countryConverter = countryConverter;
  }

  public List<Country> getAll() {
    return countryRepository.findAll();
  }

  public List<CountryDTO> getDTOs() {
    return countryConverter.getDTOs(getAll());
  }
}
