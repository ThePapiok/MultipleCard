package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter {
  public ProfileDTO getDTO(User user) {
    Address address = user.getAddress();
    String apartmentNumber = String.valueOf(address.getApartmentNumber());
    if ("null".equals(apartmentNumber)) {
      apartmentNumber = "";
    }
    ProfileDTO profileDTO = new ProfileDTO();
    profileDTO.setFirstName(user.getFirstName());
    profileDTO.setLastName(user.getLastName());
    profileDTO.setCountry(address.getCountry());
    profileDTO.setCity(address.getCity());
    profileDTO.setProvince(address.getProvince());
    profileDTO.setHouseNumber(address.getHouseNumber());
    profileDTO.setApartmentNumber(apartmentNumber);
    profileDTO.setStreet(address.getStreet());
    profileDTO.setPostalCode(address.getPostalCode());
    return profileDTO;
  }
}
