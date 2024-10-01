package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AccountConverter(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public Account getEntity(RegisterDTO registerDTO) {
    Account account = new Account();
    account.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
    account.setPhone(registerDTO.getCallingCode() + registerDTO.getPhone());
    account.setEmail(registerDTO.getEmail());
    return account;
  }

  public Account getEntity(RegisterShopDTO registerShopDTO) {
    Account account = new Account();
    account.setPassword(passwordEncoder.encode(registerShopDTO.getPassword()));
    account.setPhone(registerShopDTO.getCallingCode() + registerShopDTO.getPhone());
    account.setEmail(registerShopDTO.getEmail());
    return account;
  }
}
