package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.ProfileService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {
  private final ProfileService profileService;
  private final CountryService countryService;

  @Autowired
  public ProfileController(ProfileService profileService, CountryService countryService) {
    this.profileService = profileService;
    this.countryService = countryService;
  }

  @GetMapping("/user")
  public String getProfile(Principal principal, Model model) {
    model.addAttribute("profile", profileService.getProfile(principal.getName()));
    model.addAttribute(
        "countries",
        countryService.getAll().stream()
            .map(e -> new CountryNamesDTO(e.getName(), e.getCode()))
            .distinct()
            .toList());
    return "profilePage";
  }
}
