package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProfileConverterTest {
  private static final String TEST_PHONE = "+14234234123412";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");

  private static Address address;
  private static User user;
  private static ProfileDTO profileDTO;
  @Mock private UserRepository userRepository;
  @Mock private AccountRepository accountRepository;
  private ProfileConverter profileConverter;

  @BeforeAll
  public static void setObjects() {
    final String street = "street";
    final String city = "city";
    final String country = "pl";
    final String province = "province";
    final String houseNumber = "1";
    final String postalCode = "postalCode";
    final String firstName = "firstName";
    final String lastName = "lastName";
    address = new Address();
    address.setStreet(street);
    address.setCity(city);
    address.setCountry(country);
    address.setProvince(province);
    address.setHouseNumber(houseNumber);
    address.setPostalCode(postalCode);
    user = new User();
    user.setAddress(address);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    profileDTO = new ProfileDTO();
    profileDTO.setPostalCode(postalCode);
    profileDTO.setApartmentNumber("");
    profileDTO.setCountry(country);
    profileDTO.setCity(city);
    profileDTO.setStreet(street);
    profileDTO.setProvince(province);
    profileDTO.setHouseNumber(houseNumber);
    profileDTO.setFirstName(firstName);
    profileDTO.setLastName(lastName);
  }

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    profileConverter = new ProfileConverter(userRepository, accountRepository);
  }

  @Test
  public void shouldSuccessAtGetDTO() {
    assertEquals(profileDTO, profileConverter.getDTO(user));
  }

  @Test
  public void shouldSuccessAtGetEntity() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.ofNullable(user));

    assertEquals(user, profileConverter.getEntity(profileDTO, TEST_PHONE));
  }

  @Test
  public void shouldFailGetEntityWhenUserNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertNull(profileConverter.getEntity(profileDTO, TEST_PHONE));
  }
}
