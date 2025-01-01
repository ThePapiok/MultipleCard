package com.thepapiok.multiplecard.services;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AdminPanelServiceTest {
  private static final String TEST_EMAIL = "testEmail";
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
    final String testPhone = "+4812431244231";
    final String title = "Usunięcie przedmiotu - " + TEST_ID;
    final String text =
        "Twój przedmiot naruszył pewne normy, z przykrością musimy poinformować, że został on usunięty.";

    adminPanelService.sendInfoAboutDeletedProduct(TEST_EMAIL, testPhone, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }

  @Test
  public void shouldSendEmailAtSendInfoAboutDeletedProductWhenIsFromOtherCountry() {
    final String testPhone = "+1212431244231";
    final String title = "Removal of the item - " + TEST_ID;
    final String text =
        "Your item violated certain standards, we regret to inform you that it has been removed.";

    adminPanelService.sendInfoAboutDeletedProduct(TEST_EMAIL, testPhone, TEST_ID);
    verify(emailService).sendEmail(text, TEST_EMAIL, title);
  }
}
