package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProfileConverterTest {
  private static final String TEST_PHONE = "+14234234123412";
  private static final String TEST_FIRST_NAME = "firstName";
  private static final String TEST_LAST_NAME = "lastName";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private static Address address;
  private static AddressDTO addressDTO;
  private static User user;
  private static ProfileDTO profileDTO;
  @Mock private UserRepository userRepository;
  @Mock private AccountRepository accountRepository;
  @Mock private AddressConverter addressConverter;
  @Mock private ShopRepository shopRepository;
  @Mock private OrderRepository orderRepository;
  private ProfileConverter profileConverter;

  @BeforeAll
  public static void setObjects() {
    final String street = "street";
    final String city = "city";
    final String country = "pl";
    final String province = "province";
    final String houseNumber = "1";
    final String postalCode = "postalCode";
    address = new Address();
    address.setStreet(street);
    address.setCity(city);
    address.setCountry(country);
    address.setProvince(province);
    address.setHouseNumber(houseNumber);
    address.setPostalCode(postalCode);
    user = new User();
    user.setAddress(address);
    user.setFirstName(TEST_FIRST_NAME);
    user.setLastName(TEST_LAST_NAME);
    addressDTO = new AddressDTO();
    addressDTO.setPostalCode(postalCode);
    addressDTO.setApartmentNumber("");
    addressDTO.setCountry(country);
    addressDTO.setCity(city);
    addressDTO.setStreet(street);
    addressDTO.setProvince(province);
    addressDTO.setHouseNumber(houseNumber);
    profileDTO = new ProfileDTO();
    profileDTO.setAddress(addressDTO);
    profileDTO.setFirstName(TEST_FIRST_NAME);
    profileDTO.setLastName(TEST_LAST_NAME);
  }

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    profileConverter =
        new ProfileConverter(
            userRepository, accountRepository, addressConverter, shopRepository, orderRepository);
  }

  @Test
  public void shouldReturnProfileDTOAtGetDTOUserWhenEverythingOk() {
    when(addressConverter.getDTO(address)).thenReturn(addressDTO);

    assertEquals(profileDTO, profileConverter.getDTO(user));
  }

  @Test
  public void shouldReturnUserEntityAtGetEntityNotForShopsWhenEverythingOk() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.ofNullable(user));
    when(addressConverter.getEntity(addressDTO)).thenReturn(address);

    assertEquals(user, profileConverter.getEntity(profileDTO, TEST_PHONE));
  }

  @Test
  public void shouldReturnNullAtGetEntityNotForShopsWhenUserNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertNull(profileConverter.getEntity(profileDTO, TEST_PHONE));
  }

  @Test
  public void shouldReturnProfilesShopDTOAtGetDTOShopWhenEverythingOk() {
    final long totalAmount = 3000;
    final double centsPerZloty = 100;
    final String shopNameTest = "name";
    final String accountNumberTest = "accountNumber";
    final String imageUrlTest = "imageUrl";
    Shop shop = new Shop();
    shop.setId(new ObjectId("123456789012345678901234"));
    shop.setFirstName(TEST_FIRST_NAME);
    shop.setLastName(TEST_LAST_NAME);
    shop.setName(shopNameTest);
    shop.setAccountNumber(accountNumberTest);
    shop.setImageUrl(imageUrlTest);
    shop.setPoints(List.of(address));
    ProfileShopDTO expectedProfileShopDTO = new ProfileShopDTO();
    expectedProfileShopDTO.setFirstName(TEST_FIRST_NAME);
    expectedProfileShopDTO.setLastName(TEST_LAST_NAME);
    expectedProfileShopDTO.setName(shopNameTest);
    expectedProfileShopDTO.setAccountNumber(accountNumberTest);
    expectedProfileShopDTO.setImageUrl(imageUrlTest);
    expectedProfileShopDTO.setTotalAmount(String.valueOf(totalAmount / centsPerZloty));
    expectedProfileShopDTO.setAddress(List.of(addressDTO));

    when(orderRepository.sumTotalAmountForShop(shop.getId())).thenReturn(totalAmount);
    when(addressConverter.getDTOs(List.of(address))).thenReturn(List.of(addressDTO));

    assertEquals(expectedProfileShopDTO, profileConverter.getDTO(shop));
  }

  @Test
  public void shouldReturnShopEntityAtGetEntityForShopsWhenEverythingOk() {
    final String newShopNameTest = "newName";
    final String newAccountNumberTest = "newAccountNumber";
    final String newFirstNameTest = "newFirstName";
    final String newLastNameTest = "newLastName";
    final String urlTest = "url";
    Account account = new Account();
    account.setId(TEST_ID);
    Shop shop = new Shop();
    shop.setId(TEST_ID);
    shop.setFirstName(TEST_FIRST_NAME);
    shop.setLastName(TEST_LAST_NAME);
    shop.setName("shop1");
    shop.setPoints(List.of(new Address()));
    shop.setAccountNumber("123123123123123123");
    shop.setImageUrl(urlTest);
    List<AddressDTO> addressDTOList = List.of(addressDTO);
    List<Address> addresses = List.of(address);
    ProfileShopDTO profileShopDTO = new ProfileShopDTO();
    profileShopDTO.setFirstName(newFirstNameTest);
    profileShopDTO.setLastName(newLastNameTest);
    profileShopDTO.setName(newShopNameTest);
    profileShopDTO.setAccountNumber(newAccountNumberTest);
    profileShopDTO.setAddress(addressDTOList);
    Shop expectedShop = new Shop();
    expectedShop.setName(newShopNameTest);
    expectedShop.setPoints(addresses);
    expectedShop.setId(TEST_ID);
    expectedShop.setAccountNumber(newAccountNumberTest);
    expectedShop.setImageUrl(urlTest);
    expectedShop.setFirstName(newFirstNameTest);
    expectedShop.setLastName(newLastNameTest);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(shopRepository.findById(TEST_ID)).thenReturn(Optional.of(shop));
    when(addressConverter.getEntities(addressDTOList)).thenReturn(addresses);

    assertEquals(expectedShop, profileConverter.getEntity(profileShopDTO, TEST_PHONE));
  }

  @Test
  public void shouldReturnNullAtGetEntityForShopsWhenShopNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(shopRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertNull(profileConverter.getEntity(new ProfileShopDTO(), TEST_PHONE));
  }
}
