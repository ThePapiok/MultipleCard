package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.exceptions.NotActiveException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CustomAuthenticationFailureHandlerTest {
  private static final String ERROR_USER_NOT_ACTIVE_MESSAGE = "Konto nie jest aktywowane";
  private static final String ERROR_BAD_CREDENTIALS_MESSAGE = "Podane dane są nieprawidłowe";
  private static final String LOGIN_ERROR_URL = "/login?error";

  private static final String ERROR_MESSAGE_PARAM = "errorMessage";

  @Autowired
  private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
  private MockHttpServletRequest httpServletRequest;
  private MockHttpServletResponse httpServletResponse;

  @BeforeEach
  public void setUp() {
    httpServletRequest = new MockHttpServletRequest();
    customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler();
    httpServletResponse = new MockHttpServletResponse();
  }

  @Test
  public void shouldRedirectToLoginErrorWhenUserIsNotActive() throws IOException {
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
  public void shouldRedirectToLoginErrorWhenBadCredentials() throws IOException {
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
