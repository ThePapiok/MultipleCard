package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.exceptions.BannedException;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
  private final LocaleChanger localeChanger;
  private final MessageSource messageSource;

  public CustomAuthenticationFailureHandler(
      LocaleChanger localeChanger, MessageSource messageSource) {
    this.localeChanger = localeChanger;
    this.messageSource = messageSource;
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    final String loginUrl = "/login?error";
    final String errorMessageParam = "errorMessage";
    if (exception instanceof NotActiveException || exception instanceof BannedException) {
      request.getSession().setAttribute(errorMessageParam, exception.getMessage());
      response.sendRedirect(loginUrl);
    } else if (exception instanceof BadCredentialsException) {
      request
          .getSession()
          .setAttribute(
              errorMessageParam,
              messageSource.getMessage(
                  "userService.loadUserByUsername.user_not_found",
                  null,
                  localeChanger.getLocale()));
      response.sendRedirect(loginUrl);
    }
  }
}
