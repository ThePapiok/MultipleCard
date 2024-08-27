package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserConverterTest {
  private static final String TEST_HOUSE_APARTMENT_NUMBER = "1";
  private static final String TEST_CITY = "City";
  private static final String TEST_STREET = "Street";
  private static final String TEST_POSTAL_CODE = "Postal Code";
  private static final String TEST_COUNTRY = "Country";
  private static final String TEST_FIRST_NAME = "First Name";
  private static final String TEST_LAST_NAME = "Last Name";
  private static final String TEST_PROVINCE = "Province";

  private UserConverter userConverter;

  @BeforeEach
  public void setUp() {
    userConverter = new UserConverter();
  }

  @Test
  public void shouldSuccessGetEntityWithoutApartmentNumber() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCity(TEST_CITY);
    registerDTO.setStreet(TEST_STREET);
    registerDTO.setPostalCode(TEST_POSTAL_CODE);
    registerDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER);
    registerDTO.setCountry(TEST_COUNTRY);
    registerDTO.setFirstName(TEST_FIRST_NAME);
    registerDTO.setLastName(TEST_LAST_NAME);
    registerDTO.setProvince(TEST_PROVINCE);
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
    registerDTO.setCity(TEST_CITY);
    registerDTO.setStreet(TEST_STREET);
    registerDTO.setPostalCode(TEST_POSTAL_CODE);
    registerDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER);
    registerDTO.setCountry(TEST_COUNTRY);
    registerDTO.setFirstName(TEST_FIRST_NAME);
    registerDTO.setLastName(TEST_LAST_NAME);
    registerDTO.setProvince(TEST_PROVINCE);
    registerDTO.setApartmentNumber(TEST_HOUSE_APARTMENT_NUMBER);
    Address address = new Address();
    address.setCity(registerDTO.getCity());
    address.setStreet(registerDTO.getStreet());
    address.setPostalCode(registerDTO.getPostalCode());
    address.setHouseNumber(registerDTO.getHouseNumber());
    address.setCountry(registerDTO.getCountry());
    address.setApartmentNumber(1);
    address.setProvince(registerDTO.getProvince());
    User expectedUser = new User();
    expectedUser.setAddress(address);
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());

    assertEquals(expectedUser, userConverter.getEntity(registerDTO));
  }
}
