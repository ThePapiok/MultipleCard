package com.thepapiok.multiplecard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminPanelService {
  private final EmailService emailService;

  @Autowired
  public AdminPanelService(EmailService emailService) {
    this.emailService = emailService;
  }

  public void sendInfoAboutDeletedProduct(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith("+48")) {
      title = "Usunięcie przedmiotu - " + id;
      text =
          "Twój przedmiot naruszył pewne normy, z przykrością musimy poinformować, że został on usunięty.";
    } else {
      title = "Removal of the item - " + id;
      text =
          "Your item violated certain standards, we regret to inform you that it has been removed.";
    }
    emailService.sendEmail(text, email, title);
  }
}
