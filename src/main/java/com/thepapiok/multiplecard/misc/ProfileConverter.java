package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;

  @Autowired
  public ProfileConverter(UserRepository userRepository, AccountRepository accountRepository) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
  }

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

  public User getEntity(ProfileDTO profileDTO, String phone) {
    Address address = new Address();
    address.setPostalCode(profileDTO.getPostalCode());
    address.setCity(profileDTO.getCity());
    address.setStreet(profileDTO.getStreet());
    address.setCountry(profileDTO.getCountry());
    address.setProvince(profileDTO.getProvince());
    address.setHouseNumber(profileDTO.getHouseNumber());
    String apartmentNumber = profileDTO.getApartmentNumber();
    if ("".equals(apartmentNumber)) {
      address.setApartmentNumber(null);
    } else {
      address.setApartmentNumber(Integer.parseInt(apartmentNumber));
    }
    Optional<User> optionalUser =
        userRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalUser.isEmpty()) {
      return null;
    }
    User user = optionalUser.get();
    user.setAddress(address);
    user.setFirstName(profileDTO.getFirstName());
    user.setLastName(profileDTO.getLastName());
    return user;
  }
}
