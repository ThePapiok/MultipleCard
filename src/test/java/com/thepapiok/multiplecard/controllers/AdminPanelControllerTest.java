package com.thepapiok.multiplecard.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.services.AccountService;
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
  private static final String TEST_ID = "12312312sdffsafas";
  private static final String TRUE_VALUE = "true";
  private static final String FALSE_VALUE = "false";

  @Autowired private MockMvc mockMvc;
  @MockBean private AccountService accountService;

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
        .perform(post("/change_user").param("type", type).param("id", id).param("value", value))
        .andExpect(content().string(content));
  }
}
