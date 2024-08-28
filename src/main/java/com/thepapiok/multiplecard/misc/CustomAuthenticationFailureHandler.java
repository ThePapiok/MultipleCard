package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.exceptions.NotActiveException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    final String loginUrl = "/login?error";
    final String errorMessageParam = "errorMessage";
    if (exception instanceof NotActiveException) {
      request.getSession().setAttribute(errorMessageParam, exception.getMessage());
      response.sendRedirect(loginUrl);
    } else if (exception instanceof BadCredentialsException) {
      request.getSession().setAttribute(errorMessageParam, "Podane dane są nieprawidłowe");
      response.sendRedirect(loginUrl);
    }
  }
}
