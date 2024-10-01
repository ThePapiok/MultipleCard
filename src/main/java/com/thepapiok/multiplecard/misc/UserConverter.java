package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

  private final AddressConverter addressConverter;

  @Autowired
  public UserConverter(AddressConverter addressConverter) {
    this.addressConverter = addressConverter;
  }

  public User getEntity(RegisterDTO registerDTO) {
    User user = new User();
    user.setFirstName(registerDTO.getFirstName());
    user.setLastName(registerDTO.getLastName());
    user.setAddress(addressConverter.getEntity(registerDTO.getAddress()));
    return user;
  }
}
