package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final AddressConverter addressConverter;

  @Autowired
  public ProfileConverter(
      UserRepository userRepository,
      AccountRepository accountRepository,
      AddressConverter addressConverter) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.addressConverter = addressConverter;
  }

  public ProfileDTO getDTO(User user) {
    Address address = user.getAddress();
    ProfileDTO profileDTO = new ProfileDTO();
    profileDTO.setFirstName(user.getFirstName());
    profileDTO.setLastName(user.getLastName());
    profileDTO.setAddress(addressConverter.getDTO(address));
    return profileDTO;
  }

  public ProfileShopDTO getDTO(Shop shop) {
    final float centsPerZloty = 100.0F;
    ProfileShopDTO profileShopDTO = new ProfileShopDTO();
    profileShopDTO.setFirstName(shop.getFirstName());
    profileShopDTO.setLastName(shop.getLastName());
    profileShopDTO.setName(shop.getName());
    profileShopDTO.setAccountNumber(shop.getAccountNumber());
    profileShopDTO.setAddresses(addressConverter.getDTOs(shop.getPoints()));
    profileShopDTO.setImageUrl(shop.getImageUrl());
    profileShopDTO.setTotalAmount(String.valueOf(shop.getTotalAmount() / centsPerZloty));
    return profileShopDTO;
  }

  public User getEntity(ProfileDTO profileDTO, String phone) {
    Optional<User> optionalUser =
        userRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalUser.isEmpty()) {
      return null;
    }
    User user = optionalUser.get();
    user.setAddress(addressConverter.getEntity(profileDTO.getAddress()));
    user.setFirstName(profileDTO.getFirstName());
    user.setLastName(profileDTO.getLastName());
    return user;
  }
}
