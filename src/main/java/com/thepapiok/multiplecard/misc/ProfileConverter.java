package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter {
  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final AddressConverter addressConverter;
  private final ShopRepository shopRepository;
  private final OrderRepository orderRepository;

  @Autowired
  public ProfileConverter(
      UserRepository userRepository,
      AccountRepository accountRepository,
      AddressConverter addressConverter,
      ShopRepository shopRepository,
      OrderRepository orderRepository) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.addressConverter = addressConverter;
    this.shopRepository = shopRepository;
    this.orderRepository = orderRepository;
  }

  public ProfileDTO getDTO(User user) {
    Address address = user.getAddress();
    ProfileDTO profileDTO = new ProfileDTO();
    profileDTO.setFirstName(user.getFirstName());
    profileDTO.setLastName(user.getLastName());
    profileDTO.setPoints(user.getPoints());
    profileDTO.setAddress(addressConverter.getDTO(address));
    return profileDTO;
  }

  public ProfileShopDTO getDTO(Shop shop) {
    final double centsPerZl = 100;
    ProfileShopDTO profileShopDTO = new ProfileShopDTO();
    profileShopDTO.setFirstName(shop.getFirstName());
    profileShopDTO.setLastName(shop.getLastName());
    profileShopDTO.setName(shop.getName());
    profileShopDTO.setAccountNumber(shop.getAccountNumber());
    profileShopDTO.setAddress(addressConverter.getDTOs(shop.getPoints()));
    profileShopDTO.setImageUrl(shop.getImageUrl());
    Long totalAmount = orderRepository.sumTotalAmountForShop(shop.getId());
    if (totalAmount == null) {
      profileShopDTO.setTotalAmount("0");
    } else {
      profileShopDTO.setTotalAmount(String.valueOf(totalAmount / centsPerZl));
    }
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

  public Shop getEntity(ProfileShopDTO profileShopDTO, String phone) {
    Optional<Shop> optionalShop =
        shopRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalShop.isEmpty()) {
      return null;
    }
    Shop shop = optionalShop.get();
    shop.setFirstName(profileShopDTO.getFirstName());
    shop.setLastName(profileShopDTO.getLastName());
    shop.setName(profileShopDTO.getName());
    shop.setAccountNumber(profileShopDTO.getAccountNumber());
    shop.setPoints(addressConverter.getEntities(profileShopDTO.getAddress()));
    return shop;
  }
}
