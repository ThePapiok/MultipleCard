package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.exceptions.NotActiveException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CustomAuthenticationFailureHandlerTest {
  private static final String ERROR_MESSAGE = "Konto nie jest aktywowane";
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
  public void shouldRedirectToLoginError() throws IOException {
    customAuthenticationFailureHandler.onAuthenticationFailure(
        httpServletRequest, httpServletResponse, new NotActiveException(ERROR_MESSAGE));

    assertEquals("/login?error", httpServletResponse.getRedirectedUrl());
    assertEquals(ERROR_MESSAGE, httpServletRequest.getSession().getAttribute("errorMessage"));
  }
}
