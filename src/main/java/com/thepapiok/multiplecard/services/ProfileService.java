package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.misc.ProfileConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final ProfileConverter profileConverter;

  @Autowired
  public ProfileService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      ProfileConverter profileConverter) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.profileConverter = profileConverter;
  }

  public ProfileDTO getProfile(String phone) {
    Optional<User> optionalUser =
        userRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalUser.isPresent()) {
      return profileConverter.getDTO(optionalUser.get());
    } else {
      return null;
    }
  }
}
