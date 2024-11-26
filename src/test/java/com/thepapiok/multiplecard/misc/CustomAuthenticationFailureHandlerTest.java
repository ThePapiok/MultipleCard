package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.exceptions.NotActiveException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CustomAuthenticationFailureHandlerTest {
  private MockHttpServletRequest httpServletRequest;
  private MockHttpServletResponse httpServletResponse;
  @Autowired private MessageSource messageSource;
  @Autowired private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  @BeforeEach
  public void setUp() {
    LocaleChanger localeChanger = new LocaleChanger();
    localeChanger.setLocale(LocaleContextHolder.getLocale());
    httpServletRequest = new MockHttpServletRequest();
    customAuthenticationFailureHandler =
        new CustomAuthenticationFailureHandler(localeChanger, messageSource);
    httpServletResponse = new MockHttpServletResponse();
  }

  @Test
  public void shouldRedirectToLoginErrorAtOnAuthenticationFailureWhenUserIsNotActive()
      throws IOException {
    customAuthenticationFailureHandler.onAuthenticationFailure(
        httpServletRequest,
        httpServletResponse,
        new NotActiveException("Konto nie jest aktywowane"));

    assertEquals("/login?error", httpServletResponse.getRedirectedUrl());
    assertEquals(
        "Konto nie jest aktywowane", httpServletRequest.getSession().getAttribute("errorMessage"));
  }

  @Test
  public void shouldRedirectToLoginErrorAtOnAuthenticationFailureWhenBadCredentials()
      throws IOException {
    customAuthenticationFailureHandler.onAuthenticationFailure(
        httpServletRequest,
        httpServletResponse,
        new BadCredentialsException("Błędny login lub hasło"));

    assertEquals("/login?error", httpServletResponse.getRedirectedUrl());
    assertEquals(
        "Błędny login lub hasło", httpServletRequest.getSession().getAttribute("errorMessage"));
  }
}
