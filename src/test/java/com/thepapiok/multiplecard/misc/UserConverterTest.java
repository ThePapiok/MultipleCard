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
    addressDTO.setCity("City");
    addressDTO.setApartmentNumber("");
    addressDTO.setProvince("Province");
    addressDTO.setStreet("Street");
    addressDTO.setPostalCode("Postal Code");
    addressDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER);
    addressDTO.setCountry("Country");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("First Name");
    registerDTO.setLastName("Last Name");
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
    addressDTO.setCity("City");
    addressDTO.setApartmentNumber(TEST_HOUSE_APARTMENT_NUMBER);
    addressDTO.setProvince("Province");
    addressDTO.setStreet("Street");
    addressDTO.setPostalCode("Postal Code");
    addressDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER);
    addressDTO.setCountry("Country");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("First Name");
    registerDTO.setLastName("Last Name");
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
