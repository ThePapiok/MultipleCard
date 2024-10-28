package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.dto.AddressDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AddressConverterTest {

  private static AddressDTO TEST_ADDRESS_DTO;
  private static AddressDTO TEST_OTHER_ADDRESS_DTO;
  private static Address TEST_ADDRESS;
  private static Address TEST_OTHER_ADDRESS;
  private final AddressConverter addressConverter = new AddressConverter();

  @BeforeAll
  public static void setUp() {
    final String provinceTest1 = "province1";
    final String cityTest1 = "city1";
    final String streetTest1 = "street1";
    final String postalCodeTest1 = "postalCode1";
    final String houseNumberTest1 = "houseNumber1";
    final String countryTest1 = "country1";
    final String provinceTest2 = "province2";
    final String cityTest2 = "city2";
    final String streetTest2 = "street2";
    final String postalCodeTest2 = "postalCode2";
    final String houseNumberTest2 = "houseNumber2";
    final String countryTest2 = "country2";
    TEST_ADDRESS_DTO = new AddressDTO();
    TEST_ADDRESS_DTO.setProvince(provinceTest1);
    TEST_ADDRESS_DTO.setCity(cityTest1);
    TEST_ADDRESS_DTO.setStreet(streetTest1);
    TEST_ADDRESS_DTO.setPostalCode(postalCodeTest1);
    TEST_ADDRESS_DTO.setHouseNumber(houseNumberTest1);
    TEST_ADDRESS_DTO.setApartmentNumber("1");
    TEST_ADDRESS_DTO.setCountry(countryTest1);
    TEST_ADDRESS = new Address();
    TEST_ADDRESS.setProvince(provinceTest1);
    TEST_ADDRESS.setCity(cityTest1);
    TEST_ADDRESS.setStreet(streetTest1);
    TEST_ADDRESS.setPostalCode(postalCodeTest1);
    TEST_ADDRESS.setHouseNumber(houseNumberTest1);
    TEST_ADDRESS.setApartmentNumber(1);
    TEST_ADDRESS.setCountry(countryTest1);
    TEST_OTHER_ADDRESS_DTO = new AddressDTO();
    TEST_OTHER_ADDRESS_DTO.setProvince(provinceTest2);
    TEST_OTHER_ADDRESS_DTO.setCity(cityTest2);
    TEST_OTHER_ADDRESS_DTO.setStreet(streetTest2);
    TEST_OTHER_ADDRESS_DTO.setPostalCode(postalCodeTest2);
    TEST_OTHER_ADDRESS_DTO.setHouseNumber(houseNumberTest2);
    TEST_OTHER_ADDRESS_DTO.setApartmentNumber("2");
    TEST_OTHER_ADDRESS_DTO.setCountry(countryTest2);
    TEST_OTHER_ADDRESS = new Address();
    TEST_OTHER_ADDRESS.setProvince(provinceTest2);
    TEST_OTHER_ADDRESS.setCity(cityTest2);
    TEST_OTHER_ADDRESS.setStreet(streetTest2);
    TEST_OTHER_ADDRESS.setPostalCode(postalCodeTest2);
    TEST_OTHER_ADDRESS.setHouseNumber(houseNumberTest2);
    TEST_OTHER_ADDRESS.setApartmentNumber(2);
    TEST_OTHER_ADDRESS.setCountry(countryTest2);
  }

  @Test
  public void shouldReturnAddressDTOAtGetDTOWhenEverythingOk() {
    assertEquals(TEST_ADDRESS_DTO, addressConverter.getDTO(TEST_ADDRESS));
  }

  @Test
  public void shouldReturnAddressEntityAtGetEntityWhenEverythingOk() {
    assertEquals(TEST_ADDRESS, addressConverter.getEntity(TEST_ADDRESS_DTO));
  }

  @Test
  public void shouldReturnListOfAddressEntitiesAtGetEntitiesWhenEverythingOk() {
    assertEquals(
        List.of(TEST_ADDRESS, TEST_OTHER_ADDRESS),
        addressConverter.getEntities(List.of(TEST_ADDRESS_DTO, TEST_OTHER_ADDRESS_DTO)));
  }

  @Test
  public void shouldListOfAddressDTOAtGetDTOsWhenEverythingOk() {
    assertEquals(
        List.of(TEST_ADDRESS_DTO, TEST_OTHER_ADDRESS_DTO),
        addressConverter.getDTOs(List.of(TEST_ADDRESS, TEST_OTHER_ADDRESS)));
  }
}
