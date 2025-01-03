package com.thepapiok.multiplecard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminPanelService {
  private static final String PL_CALLING_CODE = "+48";
  private final EmailService emailService;

  @Autowired
  public AdminPanelService(EmailService emailService) {
    this.emailService = emailService;
  }

  public void sendInfoAboutDeletedProduct(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
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

  public void sendInfoAboutBlockedUser(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Zablokowanie - " + id;
      text = "Z pewnych względów twoje konto zostało zablokowane.";
    } else {
      title = "Blocking your account - " + id;
      text = "For some reasons your account has been blocked.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutDeletedReview(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Usunięcie opinii - " + id;
      text =
          "Twoja wypowiedz naruszyła pewne normy, z przykrością musimy poinformować, że została ona usunięta.";
    } else {
      title = "Removal of a review - " + id;
      text =
          "Your review violated certain standards, we regret to inform you that it has been removed.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutMutedUser(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Nadanie ograniczeń - " + id;
      text = "Z pewnych względów twoje konto zostało ograniczone.";
    } else {
      title = "Give restriction - " + id;
      text = "For some reasons your account has been restricted.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutDeletedCategory(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Usunięcie kategorii - " + id;
      text =
          "Twoja kategoria naruszyła pewne normy, z przykrością musimy poinformować, że została ona usunięta.";
    } else {
      title = "Removal of the category - " + id;
      text =
          "Your category violated certain standards, we regret to inform you that it has been removed.";
    }
    emailService.sendEmail(text, email, title);
  }
}
