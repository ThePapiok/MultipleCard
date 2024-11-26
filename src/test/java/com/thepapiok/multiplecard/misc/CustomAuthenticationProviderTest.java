package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.exceptions.BannedException;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.services.UserService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CustomAuthenticationProviderTest {
  private static final String TEST_USER_TEXT = "user";
  @MockBean private UserService userService;
  @MockBean private PasswordEncoder passwordEncoder;
  @Autowired private MessageSource messageSource;
  @Autowired private CustomAuthenticationProvider customAuthenticationProvider;

  @BeforeEach
  public void setUp() {
    LocaleChanger localeChanger = new LocaleChanger();
    localeChanger.setLocale(LocaleContextHolder.getLocale());
    customAuthenticationProvider =
        new CustomAuthenticationProvider(
            userService, passwordEncoder, localeChanger, messageSource);
  }

  @Test
  public void shouldReturnAuthenticationAtAuthenticateWhenEverythingOk() {
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(TEST_USER_TEXT, TEST_USER_TEXT);
    User user =
        new User(
            TEST_USER_TEXT,
            TEST_USER_TEXT,
            Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

    when(passwordEncoder.matches(TEST_USER_TEXT, TEST_USER_TEXT)).thenReturn(true);
    when(userService.loadUserByUsername(TEST_USER_TEXT)).thenReturn(user);

    assertNotNull(customAuthenticationProvider.authenticate(authentication));
  }

  @Test
  public void shouldRedirectToLoginAtAuthenticateWhenUserNotActive() {
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(TEST_USER_TEXT, TEST_USER_TEXT);

    when(userService.loadUserByUsername(TEST_USER_TEXT)).thenThrow(NotActiveException.class);

    assertThrows(
        NotActiveException.class, () -> customAuthenticationProvider.authenticate(authentication));
  }

  @Test
  public void shouldRedirectToLoginAtAuthenticateWhenUserBanned() {
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(TEST_USER_TEXT, TEST_USER_TEXT);

    when(userService.loadUserByUsername(TEST_USER_TEXT)).thenThrow(BannedException.class);

    assertThrows(
        BannedException.class, () -> customAuthenticationProvider.authenticate(authentication));
  }
}
