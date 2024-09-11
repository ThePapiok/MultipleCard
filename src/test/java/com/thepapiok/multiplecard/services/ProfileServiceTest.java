package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.misc.ProfileConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProfileServiceTest {
  private static final String TEST_PHONE = "+48755775676767";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private ProfileConverter profileConverter;
  private ProfileService profileService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    profileService = new ProfileService(accountRepository, userRepository, profileConverter);
  }

  @Test
  public void shouldSuccessAtGetProfile() {
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
    Account account = new Account();
    account.setId(TEST_ID);
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

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(profileConverter.getDTO(user)).thenReturn(expectedProfileDTO);

    assertEquals(expectedProfileDTO, profileService.getProfile(TEST_PHONE));
  }

  @Test
  public void shouldFailAtGetProfileWhenUserNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertNull(profileService.getProfile(TEST_PHONE));
  }
}
