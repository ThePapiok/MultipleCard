package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserConverterTest {
  private UserConverter userConverter;

  @BeforeEach
  public void setUp() {
    userConverter = new UserConverter();
  }

  @Test
  public void shouldSuccessGetEntityWithoutApartmentNumber() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCity("City");
    registerDTO.setStreet("Street");
    registerDTO.setPostalCode("Postal Code");
    registerDTO.setHouseNumber("1");
    registerDTO.setCountry("Country");
    registerDTO.setFirstName("First Name");
    registerDTO.setLastName("Last Name");
    registerDTO.setProvince("Province");
    registerDTO.setApartmentNumber("");
    Address address = new Address();
    address.setCity(registerDTO.getCity());
    address.setStreet(registerDTO.getStreet());
    address.setPostalCode(registerDTO.getPostalCode());
    address.setHouseNumber(registerDTO.getHouseNumber());
    address.setCountry(registerDTO.getCountry());
    address.setProvince(registerDTO.getProvince());
    User expectedUser = new User();
    expectedUser.setAddress(address);
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());

    assertEquals(expectedUser, userConverter.getEntity(registerDTO));
  }

  @Test
  public void shouldSuccessGetEntity() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCity("City");
    registerDTO.setStreet("Street");
    registerDTO.setPostalCode("Postal Code");
    registerDTO.setHouseNumber("1");
    registerDTO.setCountry("Country");
    registerDTO.setFirstName("First Name");
    registerDTO.setLastName("Last Name");
    registerDTO.setApartmentNumber("1");
    Address address = new Address();
    address.setCity(registerDTO.getCity());
    address.setStreet(registerDTO.getStreet());
    address.setPostalCode(registerDTO.getPostalCode());
    address.setHouseNumber(registerDTO.getHouseNumber());
    address.setCountry(registerDTO.getCountry());
    address.setApartmentNumber(1);
    User expectedUser = new User();
    expectedUser.setAddress(address);
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());

    assertEquals(expectedUser, userConverter.getEntity(registerDTO));
  }
}
