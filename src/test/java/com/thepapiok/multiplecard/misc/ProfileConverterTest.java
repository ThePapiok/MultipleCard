package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import org.junit.jupiter.api.Test;

public class ProfileConverterTest {

  private final ProfileConverter profileConverter = new ProfileConverter();

  @Test
  public void shouldSuccessAtGetDTO() {
    final String street = "street";
    final String city = "city";
    final String country = "pl";
    final String province = "province";
    final String houseNumber = "1";
    final String postalCode = "postalCode";
    final String firstName = "firstName";
    final String lastName = "lastName";
    Address address = new Address();
    address.setStreet(street);
    address.setCity(city);
    address.setCountry(country);
    address.setProvince(province);
    address.setHouseNumber(houseNumber);
    address.setPostalCode(postalCode);
    User user = new User();
    user.setAddress(address);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    ProfileDTO expectedProfileDTO = new ProfileDTO();
    expectedProfileDTO.setPostalCode(postalCode);
    expectedProfileDTO.setApartmentNumber("");
    expectedProfileDTO.setCountry(country);
    expectedProfileDTO.setCity(city);
    expectedProfileDTO.setStreet(street);
    expectedProfileDTO.setProvince(province);
    expectedProfileDTO.setHouseNumber(houseNumber);
    expectedProfileDTO.setFirstName(firstName);
    expectedProfileDTO.setLastName(lastName);

    assertEquals(expectedProfileDTO, profileConverter.getDTO(user));
  }
}
