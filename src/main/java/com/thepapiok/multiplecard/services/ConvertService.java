package com.thepapiok.multiplecard.services;

import jakarta.servlet.http.HttpSession;
import java.util.HashSet;
import org.springframework.stereotype.Service;

@Service
public class ConvertService {

  public HashSet<String> getIds(HttpSession httpSession) {
    Object ids = httpSession.getAttribute("ids");
    HashSet<String> set;
    if (ids == null) {
      set = new HashSet<>();
    } else if (ids instanceof HashSet<?> genericSet) {
      if (genericSet.stream().allMatch(e -> e instanceof String)) {
        set = (HashSet<String>) genericSet;
      } else {
        set = new HashSet<>();
      }
    } else {
      set = new HashSet<>();
    }
    return set;
  }
}
