package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.services.UserService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
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
  @Autowired private CustomAuthenticationProvider customAuthenticationProvider;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private UserService userService;
  @MockBean private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    customAuthenticationProvider = new CustomAuthenticationProvider(userService, passwordEncoder);
  }

  @Test
  public void shouldReturnAuthentication() {
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
  public void shouldRedirectToLoginWhenUserNotActive() {
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(TEST_USER_TEXT, TEST_USER_TEXT);

    when(userService.loadUserByUsername(TEST_USER_TEXT)).thenThrow(NotActiveException.class);

    assertThrows(
        NotActiveException.class, () -> customAuthenticationProvider.authenticate(authentication));
  }
}
