package com.thepapiok.multiplecard.exceptions;

import org.springframework.security.core.AuthenticationException;

public class BannedException extends AuthenticationException {
  public BannedException(String msg) {
    super(msg);
  }

  public BannedException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
