package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

  @Autowired
  public CustomAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
    setPasswordEncoder(passwordEncoder);
    setUserDetailsService(userService);
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Authentication getAuthentication = null;
    try {
      getAuthentication = super.authenticate(authentication);
    } catch (InternalAuthenticationServiceException e) {
      if (e.getCause() instanceof NotActiveException) {
        throw new NotActiveException("Konto nie jest aktywowane");
      }
    }
    return getAuthentication;
  }
}
