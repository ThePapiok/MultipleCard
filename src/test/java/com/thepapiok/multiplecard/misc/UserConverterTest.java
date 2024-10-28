package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserConverterTest {
  private static final String TEST_HOUSE_APARTMENT_NUMBER = "1";
  private static final String TEST_CITY = "City";
  private static final String TEST_STREET = "Street";
  private static final String TEST_POSTAL_CODE = "Postal Code";
  private static final String TEST_COUNTRY = "Country";
  private static final String TEST_FIRST_NAME = "First Name";
  private static final String TEST_LAST_NAME = "Last Name";
  private static final String TEST_PROVINCE = "Province";

  @Mock private AddressConverter addressConverter;

  private UserConverter userConverter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userConverter = new UserConverter(addressConverter);
  }

  @Test
  public void shouldReturnUserEntityAtGetEntityWithoutApartmentNumber() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(TEST_CITY);
    addressDTO.setApartmentNumber("");
    addressDTO.setProvince(TEST_PROVINCE);
    addressDTO.setStreet(TEST_STREET);
    addressDTO.setPostalCode(TEST_POSTAL_CODE);
    addressDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER);
    addressDTO.setCountry(TEST_COUNTRY);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName(TEST_FIRST_NAME);
    registerDTO.setLastName(TEST_LAST_NAME);
    registerDTO.setAddress(addressDTO);
    Address address = new Address();
    address.setCity(addressDTO.getCity());
    address.setStreet(addressDTO.getStreet());
    address.setPostalCode(addressDTO.getPostalCode());
    address.setHouseNumber(addressDTO.getHouseNumber());
    address.setCountry(addressDTO.getCountry());
    address.setProvince(addressDTO.getProvince());
    User expectedUser = new User();
    expectedUser.setAddress(address);
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());

    when(addressConverter.getEntity(addressDTO)).thenReturn(address);

    assertEquals(expectedUser, userConverter.getEntity(registerDTO));
  }

  @Test
  public void shouldReturnUserEntityAtGetEntityWhenEverythingOk() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(TEST_CITY);
    addressDTO.setApartmentNumber(TEST_HOUSE_APARTMENT_NUMBER);
    addressDTO.setProvince(TEST_PROVINCE);
    addressDTO.setStreet(TEST_STREET);
    addressDTO.setPostalCode(TEST_POSTAL_CODE);
    addressDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER);
    addressDTO.setCountry(TEST_COUNTRY);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName(TEST_FIRST_NAME);
    registerDTO.setLastName(TEST_LAST_NAME);
    registerDTO.setAddress(addressDTO);
    Address address = new Address();
    address.setCity(addressDTO.getCity());
    address.setStreet(addressDTO.getStreet());
    address.setPostalCode(addressDTO.getPostalCode());
    address.setHouseNumber(addressDTO.getHouseNumber());
    address.setCountry(addressDTO.getCountry());
    address.setApartmentNumber(1);
    address.setProvince(addressDTO.getProvince());
    User expectedUser = new User();
    expectedUser.setAddress(address);
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());

    when(addressConverter.getEntity(addressDTO)).thenReturn(address);

    assertEquals(expectedUser, userConverter.getEntity(registerDTO));
  }
}
