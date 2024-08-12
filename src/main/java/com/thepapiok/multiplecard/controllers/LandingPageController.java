package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LandingPageController {

    @GetMapping
    public String getLandingPage(@RequestParam(defaultValue = "false") String added, Model model){
        model.addAttribute("review", new ReviewDTO());
        model.addAttribute("added", added);
        return "landingPage";
    }
}
