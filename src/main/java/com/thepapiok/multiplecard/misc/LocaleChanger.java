package com.thepapiok.multiplecard.misc;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class LocaleChanger {
  private Locale locale;

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}
