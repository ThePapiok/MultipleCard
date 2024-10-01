package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.dto.AddressDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AddressConverterTest {

  private static AddressDTO TEST_ADDRESS_DTO;
  private static Address TEST_ADDRESS;
  private final AddressConverter addressConverter = new AddressConverter();

  @BeforeAll
  public static void setUp() {
    final String province = "province1";
    final String city = "city1";
    final String street = "street1";
    final String postalCode = "postalCode1";
    final String houseNumber = "houseNumber1";
    final String country = "country1";
    TEST_ADDRESS_DTO = new AddressDTO();
    TEST_ADDRESS_DTO.setProvince(province);
    TEST_ADDRESS_DTO.setCity(city);
    TEST_ADDRESS_DTO.setStreet(street);
    TEST_ADDRESS_DTO.setPostalCode(postalCode);
    TEST_ADDRESS_DTO.setHouseNumber(houseNumber);
    TEST_ADDRESS_DTO.setApartmentNumber("1");
    TEST_ADDRESS_DTO.setCountry(country);
    TEST_ADDRESS = new Address();
    TEST_ADDRESS.setProvince(province);
    TEST_ADDRESS.setCity(city);
    TEST_ADDRESS.setStreet(street);
    TEST_ADDRESS.setPostalCode(postalCode);
    TEST_ADDRESS.setHouseNumber(houseNumber);
    TEST_ADDRESS.setApartmentNumber(1);
    TEST_ADDRESS.setCountry(country);
  }

  @Test
  public void shouldSuccessAtGetDTO() {
    assertEquals(TEST_ADDRESS_DTO, addressConverter.getDTO(TEST_ADDRESS));
  }

  @Test
  public void shouldSuccessAtGetEntity() {
    assertEquals(TEST_ADDRESS, addressConverter.getEntity(TEST_ADDRESS_DTO));
  }

  @Test
  public void shouldSuccessAtGetEntities() {
    final String province = "province2";
    final String city = "city2";
    final String street = "street2";
    final String postalCode = "postalCode2";
    final String houseNumber = "houseNumber2";
    final String country = "country2";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setProvince(province);
    addressDTO.setCity(city);
    addressDTO.setStreet(street);
    addressDTO.setPostalCode(postalCode);
    addressDTO.setHouseNumber(houseNumber);
    addressDTO.setApartmentNumber("2");
    addressDTO.setCountry(country);
    Address address = new Address();
    address.setProvince(province);
    address.setCity(city);
    address.setStreet(street);
    address.setPostalCode(postalCode);
    address.setHouseNumber(houseNumber);
    address.setApartmentNumber(2);
    address.setCountry(country);

    assertEquals(
        List.of(TEST_ADDRESS, address),
        addressConverter.getEntities(List.of(TEST_ADDRESS_DTO, addressDTO)));
  }
}
