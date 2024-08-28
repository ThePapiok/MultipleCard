package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.exceptions.NotActiveException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    if (exception instanceof NotActiveException) {
      System.out.println(request.getUserPrincipal());
      request.getSession().setAttribute("errorMessage", exception.getMessage());
      response.sendRedirect("/login?error");
    }
  }
}
