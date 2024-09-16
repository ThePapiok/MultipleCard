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
  private static final String ERROR_USER_NOT_ACTIVE_MESSAGE = "Konto nie jest aktywowane";
  private static final String ERROR_BAD_CREDENTIALS_MESSAGE = "Błędny login lub hasło";
  private static final String LOGIN_ERROR_URL = "/login?error";

  private static final String ERROR_MESSAGE_PARAM = "errorMessage";

  @Autowired private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
  private MockHttpServletRequest httpServletRequest;
  private MockHttpServletResponse httpServletResponse;
  @Autowired private MessageSource messageSource;

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
        new NotActiveException(ERROR_USER_NOT_ACTIVE_MESSAGE));

    assertEquals(LOGIN_ERROR_URL, httpServletResponse.getRedirectedUrl());
    assertEquals(
        ERROR_USER_NOT_ACTIVE_MESSAGE,
        httpServletRequest.getSession().getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldRedirectToLoginErrorAtOnAuthenticationFailureWhenBadCredentials()
      throws IOException {
    customAuthenticationFailureHandler.onAuthenticationFailure(
        httpServletRequest,
        httpServletResponse,
        new BadCredentialsException(ERROR_BAD_CREDENTIALS_MESSAGE));

    assertEquals(LOGIN_ERROR_URL, httpServletResponse.getRedirectedUrl());
    assertEquals(
        ERROR_BAD_CREDENTIALS_MESSAGE,
        httpServletRequest.getSession().getAttribute(ERROR_MESSAGE_PARAM));
  }
}
