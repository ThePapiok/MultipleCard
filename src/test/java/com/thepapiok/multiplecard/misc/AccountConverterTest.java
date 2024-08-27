package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountConverterTest {
  private AccountConverter accountConverter;
  @Mock private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    accountConverter = new AccountConverter(passwordEncoder);
  }

  @Test
  public void shouldSuccessGetEntity() {
    final String password = "Test123!";
    final String encodePassword = "123dsfavdva3312";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword(password);
    registerDTO.setCallingCode("+4");
    registerDTO.setPhone("8");
    registerDTO.setEmail("test@test");
    Account expectedAccount = new Account();
    expectedAccount.setPassword(encodePassword);
    expectedAccount.setPhone("+48");
    expectedAccount.setEmail("test@test");

    when(passwordEncoder.encode(password)).thenReturn(encodePassword);

    assertEquals(expectedAccount, accountConverter.getEntity(registerDTO));
  }
}
