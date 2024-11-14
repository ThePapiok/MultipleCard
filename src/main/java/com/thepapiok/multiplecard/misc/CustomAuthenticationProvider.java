package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.exceptions.BannedException;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.services.UserService;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
  private final LocaleChanger localeChanger;
  private final MessageSource messageSource;

  @Autowired
  public CustomAuthenticationProvider(
      UserService userService,
      PasswordEncoder passwordEncoder,
      LocaleChanger localeChanger,
      MessageSource messageSource) {
    this.localeChanger = localeChanger;
    this.messageSource = messageSource;
    setPasswordEncoder(passwordEncoder);
    setUserDetailsService(userService);
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Authentication getAuthentication = null;
    Locale locale = localeChanger.getLocale();
    try {
      getAuthentication = super.authenticate(authentication);
    } catch (InternalAuthenticationServiceException e) {
      if (e.getCause() instanceof NotActiveException) {
        throw new NotActiveException(
            messageSource.getMessage(
                "userService.loadUserByUsername.user_not_active", null, locale));
      }
      if (e.getCause() instanceof BannedException) {
        throw new BannedException(
            messageSource.getMessage("userService.loadUserByUsername.user_banned", null, locale));
      }
    }
    return getAuthentication;
  }
}
