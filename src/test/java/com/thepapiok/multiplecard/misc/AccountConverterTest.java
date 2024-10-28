package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountConverterTest {
  private static final String TEST_PASSWORD = "Test123!";
  private static final String TEST_ENCODE_PASSWORD = "123dsfavdva3312";
  private static final String TEST_EMAIL = "test@test";
  private static final String TEST_CALLING_CODE = "+48";
  private static final String TEST_PHONE = "21441322432314432";
  private static final String TEST_FULL_PHONE = "+4821441322432314432";

  private AccountConverter accountConverter;
  @Mock private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    accountConverter = new AccountConverter(passwordEncoder);
  }

  @Test
  public void shouldReturnAccountEntityAtGetEntityRegisterDTOWhenEverythingOk() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword(TEST_PASSWORD);
    registerDTO.setCallingCode(TEST_CALLING_CODE);
    registerDTO.setPhone(TEST_PHONE);
    registerDTO.setEmail(TEST_EMAIL);
    Account expectedAccount = new Account();
    expectedAccount.setPassword(TEST_ENCODE_PASSWORD);
    expectedAccount.setPhone(TEST_FULL_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);

    when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODE_PASSWORD);

    assertEquals(expectedAccount, accountConverter.getEntity(registerDTO));
  }

  @Test
  public void shouldReturnAccountEntityAtGetEntityRegisterShopDTOWhenEverythingOk() {
    RegisterShopDTO registerShopDTO = new RegisterShopDTO();
    registerShopDTO.setPassword(TEST_PASSWORD);
    registerShopDTO.setCallingCode(TEST_CALLING_CODE);
    registerShopDTO.setPhone(TEST_PHONE);
    registerShopDTO.setEmail(TEST_EMAIL);
    Account expectedAccount = new Account();
    expectedAccount.setPassword(TEST_ENCODE_PASSWORD);
    expectedAccount.setPhone(TEST_FULL_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);

    when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODE_PASSWORD);

    assertEquals(expectedAccount, accountConverter.getEntity(registerShopDTO));
  }
}
