package com.thepapiok.multiplecard.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.services.AccountService;
import com.thepapiok.multiplecard.services.AdminPanelService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ReportService;
import com.thepapiok.multiplecard.services.ReviewService;
import com.thepapiok.multiplecard.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AdminPanelControllerTest {
  private static final String TEST_PHONE = "+423423141234";
  private static final String TEST_EMAIL = "testEmail";
  private static final String ID_PARAM = "id";
  private static final String TEST_ID = "123456789012345678901234";
  private static final String TEST_DESCRIPTION = "test sadfasdfasdfasdfafsdf";
  private static final String TRUE_VALUE = "true";
  private static final String FALSE_VALUE = "false";
  private static final int BAD_REQUEST_STATUS = 400;

  @Autowired private MockMvc mockMvc;
  @MockBean private AccountService accountService;
  @MockBean private UserService userService;
  @MockBean private ReportService reportService;
  @MockBean private ProductService productService;
  @MockBean private AdminPanelService adminPanelService;
  @MockBean private ReviewService reviewService;

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnAdminPanelPageAtAdminPanelWhenFoundUsers() throws Exception {
    List<UserDTO> users = new ArrayList<>();
    UserDTO user = new UserDTO();
    user.setId(new ObjectId().toString());
    users.add(user);

    when(accountService.getUsers(null, null)).thenReturn(users);

    mockMvc
        .perform(get("/admin_panel"))
        .andExpect(model().attribute("emptyUsers", false))
        .andExpect(model().attribute("users", users))
        .andExpect(view().name("adminPanelPage"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnAdminPanelPageAtAdminPanelWhenNotFoundUsers() throws Exception {
    when(accountService.getUsers(null, null)).thenReturn(List.of());

    mockMvc
        .perform(get("/admin_panel"))
        .andExpect(model().attribute("emptyUsers", true))
        .andExpect(view().name("adminPanelPage"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtChangeUserWhenTypeActiveAndErrorAtChangeActive() throws Exception {
    when(accountService.changeActive(TEST_ID, true)).thenReturn(false);

    performPostAtChangeUser(FALSE_VALUE, TRUE_VALUE, TEST_ID, TRUE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtChangeUserWhenTypeActive() throws Exception {
    when(accountService.changeActive(TEST_ID, true)).thenReturn(true);

    performPostAtChangeUser(TRUE_VALUE, TRUE_VALUE, TEST_ID, TRUE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtChangeUserWhenTypeBannedAndErrorAtChangeBanned() throws Exception {
    when(accountService.changeBanned(TEST_ID, true)).thenReturn(false);

    performPostAtChangeUser(FALSE_VALUE, FALSE_VALUE, TEST_ID, TRUE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtChangeUserWhenTypeBanned() throws Exception {
    when(accountService.changeBanned(TEST_ID, true)).thenReturn(true);

    performPostAtChangeUser(TRUE_VALUE, FALSE_VALUE, TEST_ID, TRUE_VALUE);
  }

  private void performPostAtChangeUser(String content, String type, String id, String value)
      throws Exception {
    mockMvc
        .perform(post("/change_user").param("type", type).param(ID_PARAM, id).param("value", value))
        .andExpect(content().string(content));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnIsRestrictedErrorAtReportProductWhenUserIsRestricted() throws Exception {
    when(userService.checkIsRestricted(TEST_PHONE)).thenReturn(true);

    performPostAtReportProduct(
        TEST_DESCRIPTION, BAD_REQUEST_STATUS, "Twój dostęp jest ograniczony");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnValidationErrorAtReportProductWhenLengthIsBad1() throws Exception {
    when(userService.checkIsRestricted(TEST_PHONE)).thenReturn(false);

    performPostAtReportProduct("cos", BAD_REQUEST_STATUS, "Podane dane są niepoprawne");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnValidationErrorAtReportProductWhenLengthIsBad2() throws Exception {
    final int badLength = 1001;
    StringBuilder description = new StringBuilder();
    description.append("c".repeat(badLength));

    when(userService.checkIsRestricted(TEST_PHONE)).thenReturn(false);

    performPostAtReportProduct(
        description.toString(), BAD_REQUEST_STATUS, "Podane dane są niepoprawne");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnOwnerErrorAtReportProductWhenIsOwner() throws Exception {
    when(userService.checkIsRestricted(TEST_PHONE)).thenReturn(false);
    when(reportService.checkIsOwner(TEST_ID, TEST_PHONE, true)).thenReturn(true);

    performPostAtReportProduct(TEST_DESCRIPTION, BAD_REQUEST_STATUS, "Jesteś tego właścicielem");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnAlreadyReportedErrorAtReportProductWhenReportExists() throws Exception {
    when(userService.checkIsRestricted(TEST_PHONE)).thenReturn(false);
    when(reportService.checkIsOwner(TEST_ID, TEST_PHONE, true)).thenReturn(false);
    when(reportService.checkReportAlreadyExists(TEST_ID, TEST_PHONE)).thenReturn(true);

    performPostAtReportProduct(TEST_DESCRIPTION, BAD_REQUEST_STATUS, "Już to zgłosiłeś");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnUnexpectedErrorAtReportProductWhenErrorAtAddReport() throws Exception {
    when(userService.checkIsRestricted(TEST_PHONE)).thenReturn(false);
    when(reportService.checkIsOwner(TEST_ID, TEST_PHONE, true)).thenReturn(false);
    when(reportService.checkReportAlreadyExists(TEST_ID, TEST_PHONE)).thenReturn(false);
    when(reportService.addReport(true, TEST_ID, TEST_PHONE, TEST_DESCRIPTION)).thenReturn(false);

    performPostAtReportProduct(TEST_DESCRIPTION, BAD_REQUEST_STATUS, "Nieoczekiwany błąd");
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnSuccessMessageAtReportProductWhenEverythingOk() throws Exception {
    final int okStatus = 200;

    when(userService.checkIsRestricted(TEST_PHONE)).thenReturn(false);
    when(reportService.checkIsOwner(TEST_ID, TEST_PHONE, true)).thenReturn(false);
    when(reportService.checkReportAlreadyExists(TEST_ID, TEST_PHONE)).thenReturn(false);
    when(reportService.addReport(true, TEST_ID, TEST_PHONE, TEST_DESCRIPTION)).thenReturn(true);

    performPostAtReportProduct(TEST_DESCRIPTION, okStatus, "Pomyślnie wysłano zgłoszenie");
  }

  private void performPostAtReportProduct(String description, int status, String content)
      throws Exception {
    mockMvc
        .perform(
            post("/report")
                .param(ID_PARAM, TEST_ID)
                .param("description", description)
                .param("isProduct", TRUE_VALUE))
        .andExpect(status().is(status))
        .andExpect(content().string(content));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtDeleteProductWhenNotFoundEmail() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);

    when(accountService.getAccountByProductId(TEST_ID)).thenReturn(account);

    performPostAtDeleteProduct(FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtDeleteProductWhenErrorAtDeleteProduct() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountByProductId(TEST_ID)).thenReturn(account);
    when(productService.deleteProduct(TEST_ID)).thenReturn(false);

    performPostAtDeleteProduct(FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtDeleteProductWhenEverythingOk() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountByProductId(TEST_ID)).thenReturn(account);
    when(productService.deleteProduct(TEST_ID)).thenReturn(true);

    performPostAtDeleteProduct(TRUE_VALUE);
    verify(adminPanelService).sendInfoAboutDeletedProduct(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  private void performPostAtDeleteProduct(String content) throws Exception {
    mockMvc
        .perform(post("/delete_product").param(ID_PARAM, TEST_ID))
        .andExpect(content().string(content));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtBlockUserWhenEmailIsNullWithUserParam() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);

    performPostAtBlockUser(false, FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtBlockUserWhenErrorAtChangeBannedWithUserParam() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);
    when(accountService.changeBanned(TEST_ID, true)).thenReturn(false);

    performPostAtBlockUser(false, FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtBlockUserWhenEverythingOkWithUserParam() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);
    when(accountService.changeBanned(TEST_ID, true)).thenReturn(true);

    performPostAtBlockUser(false, TRUE_VALUE);
    verify(adminPanelService).sendInfoAboutBlockedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtBlockUserWhenEverythingOkWithShopParam() throws Exception {
    final ObjectId testId = new ObjectId("123456789009876543210987");
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    account.setId(testId);

    when(accountService.getAccountByProductId(TEST_ID)).thenReturn(account);
    when(accountService.changeBanned(testId.toString(), true)).thenReturn(true);

    performPostAtBlockUser(true, TRUE_VALUE);
    verify(adminPanelService).sendInfoAboutBlockedUser(TEST_EMAIL, TEST_PHONE, testId.toString());
  }

  private void performPostAtBlockUser(boolean isShop, String content) throws Exception {

    mockMvc
        .perform(
            post("/block_user").param(ID_PARAM, TEST_ID).param("isShop", String.valueOf(isShop)))
        .andExpect(content().string(content));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtDeleteReviewWhenNotFoundEmail() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);

    performPostAtDeleteReview(FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtDeleteReviewWhenErrorAtDeleteReview() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);
    when(reviewService.removeReview(new ObjectId(TEST_ID), TEST_PHONE)).thenReturn(false);

    performPostAtDeleteReview(FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtDeleteReviewWhenEverythingOk() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);
    when(reviewService.removeReview(new ObjectId(TEST_ID), TEST_PHONE)).thenReturn(true);

    performPostAtDeleteReview(TRUE_VALUE);
    verify(adminPanelService).sendInfoAboutDeletedReview(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  private void performPostAtDeleteReview(String content) throws Exception {
    mockMvc
        .perform(post("/delete_review").param(ID_PARAM, TEST_ID))
        .andExpect(content().string(content));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtMuteUserWhenNotFoundEmail() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);

    performPostAtMuteUser(FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtMuteUserWhenErrorAtDeleteReview() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);
    when(userService.changeRestricted(TEST_ID, true)).thenReturn(false);

    performPostAtMuteUser(FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtMuteUserWhenEverythingOk() throws Exception {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountService.getAccountById(TEST_ID)).thenReturn(account);
    when(userService.changeRestricted(TEST_ID, true)).thenReturn(true);

    performPostAtMuteUser(TRUE_VALUE);
    verify(adminPanelService).sendInfoAboutMutedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  private void performPostAtMuteUser(String content) throws Exception {
    mockMvc
        .perform(post("/mute_user").param(ID_PARAM, TEST_ID))
        .andExpect(content().string(content));
  }
}