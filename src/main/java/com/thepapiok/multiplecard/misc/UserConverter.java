package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.repositories.CountryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

  private final CountryRepository countryRepository;

  @Autowired
  public UserConverter(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  public User getEntity(RegisterDTO registerDTO) {
    Address address = new Address();
    address.setCity(registerDTO.getCity());
    address.setStreet(registerDTO.getStreet());
    address.setPostalCode(registerDTO.getPostalCode());
    address.setHouseNumber(registerDTO.getHouseNumber());
    String apartmentNumber = registerDTO.getApartmentNumber();
    if ("".equals(apartmentNumber)) {
      address.setApartmentNumber(null);
    } else {
      address.setApartmentNumber(Integer.parseInt(apartmentNumber));
    }
    address.setCountryId(
        new ObjectId(countryRepository.findByName(registerDTO.getCountry()).getId()));
    // TODO maybe add default value
    User user = new User();
    user.setFirstName(registerDTO.getFirstName());
    user.setLastName(registerDTO.getLastName());
    user.setAddress(address);
    return user;
  }
}
