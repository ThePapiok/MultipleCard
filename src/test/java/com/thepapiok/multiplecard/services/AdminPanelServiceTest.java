package com.thepapiok.multiplecard.services;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AdminPanelServiceTest {
  private static final String TEST_EMAIL = "testEmail";
  private static final String TEST_PHONE_PL = "+4812431244231";
  private static final String TEST_PHONE_OTHER = "+1212431244231";
  private static final String TEST_ID = "123fdsasdfas13213212";
  private AdminPanelService adminPanelService;
  @Mock private EmailService emailService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    adminPanelService = new AdminPanelService(emailService);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutDeletedProductWhenIsFromPoland() {
    final String title = "Usunięcie przedmiotu - " + TEST_ID;
    final String text =
        "Twój przedmiot naruszył pewne normy, z przykrością musimy poinformować, że został on usunięty.";

    adminPanelService.sendInfoAboutDeletedProduct(TEST_EMAIL, TEST_PHONE_PL, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutDeletedProductWhenIsFromOtherCountry() {
    final String title = "Removal of the item - " + TEST_ID;
    final String text =
        "Your item violated certain standards, we regret to inform you that it has been removed.";

    adminPanelService.sendInfoAboutDeletedProduct(TEST_EMAIL, TEST_PHONE_OTHER, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutBlockedUserWhenIsFromPoland() {
    final String title = "Zablokowanie - " + TEST_ID;
    final String text = "Z pewnych względów twoje konto zostało zablokowane.";

    adminPanelService.sendInfoAboutBlockedUser(TEST_EMAIL, TEST_PHONE_PL, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutBlockedUserWhenIsFromOtherCountry() {
    final String title = "Blocking your account - " + TEST_ID;
    final String text = "For some reasons your account has been blocked.";

    adminPanelService.sendInfoAboutBlockedUser(TEST_EMAIL, TEST_PHONE_OTHER, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutDeletedReviewWhenIsFromPoland() {
    final String title = "Usunięcie opinii - " + TEST_ID;
    final String text =
        "Twoja wypowiedz naruszyła pewne normy, z przykrością musimy poinformować, że została ona usunięta.";

    adminPanelService.sendInfoAboutDeletedReview(TEST_EMAIL, TEST_PHONE_PL, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutDeletedReviewWhenIsFromOtherCountry() {
    final String title = "Removal of a review - " + TEST_ID;
    final String text =
        "Your review violated certain standards, we regret to inform you that it has been removed.";

    adminPanelService.sendInfoAboutDeletedReview(TEST_EMAIL, TEST_PHONE_OTHER, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutMutedUserWhenIsFromPoland() {
    final String title = "Nadanie ograniczeń - " + TEST_ID;
    final String text = "Z pewnych względów twoje konto zostało ograniczone.";

    adminPanelService.sendInfoAboutMutedUser(TEST_EMAIL, TEST_PHONE_PL, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutMutedUserWhenIsFromOtherCountry() {
    final String title = "Give restriction - " + TEST_ID;
    final String text = "For some reasons your account has been restricted.";

    adminPanelService.sendInfoAboutMutedUser(TEST_EMAIL, TEST_PHONE_OTHER, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutDeletedCategoryWhenIsFromPoland() {
    final String title = "Usunięcie kategorii - " + TEST_ID;
    final String text =
        "Twoja kategoria naruszyła pewne normy, z przykrością musimy poinformować, że została ona usunięta.";

    adminPanelService.sendInfoAboutDeletedCategory(TEST_EMAIL, TEST_PHONE_PL, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutDeletedCategoryWhenIsFromOtherCountry() {
    final String title = "Removal of the category - " + TEST_ID;
    final String text =
        "Your category violated certain standards, we regret to inform you that it has been removed.";

    adminPanelService.sendInfoAboutDeletedCategory(TEST_EMAIL, TEST_PHONE_OTHER, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }
}
