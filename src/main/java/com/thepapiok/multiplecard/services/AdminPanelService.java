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
      title = "Zablokowanie konta - " + id;
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

  public void sendInfoAboutUnblockedUser(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Odblokowanie konta - " + id;
      text = "Twoje konto zostało odblokowane.";
    } else {
      title = "Unblocking your account - " + id;
      text = "Your account has been unblocked.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutActivatedUser(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Aktywowanie konta - " + id;
      text = "Twoje konto zostało aktywowane.";
    } else {
      title = "Activating your account - " + id;
      text = "Your account has been activated.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutDeactivatedUser(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Dezaktywowanie konta - " + id;
      text = "Twoje konto zostało dezaktywowane.";
    } else {
      title = "Deactivating your account - " + id;
      text = "Your account has been deactivated.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutChangeUserToAdmin(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Awansowanie na Admina - " + id;
      text = "Uzyskałeś rolę Admin.";
    } else {
      title = "Promotion to Admin - " + id;
      text = "Your account is now Admin.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutChangeAdminToUser(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Degradacja - " + id;
      text = "Twoje konto zostało zdegradowane do roli User.";
    } else {
      title = "Degradation - " + id;
      text = "Your account is now User.";
    }
    emailService.sendEmail(text, email, title);
  }

  public void sendInfoAboutUnmutedUser(String email, String phone, String id) {
    String text;
    String title;
    if (phone.startsWith(PL_CALLING_CODE)) {
      title = "Cofnięcie ograniczeń - " + id;
      text = "Zabrano ograniczenia z twojego konta.";
    } else {
      title = "Withdrawal of restriction - " + id;
      text = "Your account is now without restriction.";
    }
    emailService.sendEmail(text, email, title);
  }
}
