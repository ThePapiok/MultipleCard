package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.BlockedIp;
import com.thepapiok.multiplecard.repositories.BlockedIpRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BlockedIpServiceTest {
  private static final String TEST_ENCRYPTED1_IP = "safd234234fdsvdfsasad";
  private static final String TEST_ENCRYPTED2_IP = "123123dsfasdfasdf123";
  private static final String TEST_IP = "123.123.123.123";
  private BlockedIpService blockedIpService;
  @Mock private BlockedIpRepository blockedIpRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    final int maxAttempts = 3;
    final int maxAmount = 100;
    final int testBlockedIp1Amount = 50;
    final int testBlockedIp1Attempts = 3;
    final int testBlockedIp2Amount = 150;
    final int testBlockedIp2Attempts = 1;
    MockitoAnnotations.openMocks(this);
    blockedIpService = new BlockedIpService(blockedIpRepository, passwordEncoder);
    List<BlockedIp> blockedIps = new ArrayList<>();
    BlockedIp blockedIp1 = new BlockedIp();
    blockedIp1.setAmount(testBlockedIp1Amount);
    blockedIp1.setAttempts(testBlockedIp1Attempts);
    blockedIp1.setEncryptedIp(TEST_ENCRYPTED1_IP);
    BlockedIp blockedIp2 = new BlockedIp();
    blockedIp2.setAmount(testBlockedIp2Amount);
    blockedIp2.setAttempts(testBlockedIp2Attempts);
    blockedIp2.setEncryptedIp(TEST_ENCRYPTED2_IP);
    blockedIps.add(blockedIp1);
    blockedIps.add(blockedIp2);

    when(blockedIpRepository.getAllByAmountGreaterThanOrAttemptsEquals(maxAmount, maxAttempts))
        .thenReturn(blockedIps);
  }

  @Test
  public void shouldReturnTrueAtCheckIpIsNotBlockedWhenEverythingOk() {
    when(passwordEncoder.matches(TEST_IP, TEST_ENCRYPTED1_IP)).thenReturn(false);
    when(passwordEncoder.matches(TEST_IP, TEST_ENCRYPTED2_IP)).thenReturn(false);

    assertTrue(blockedIpService.checkIpIsNotBlocked(TEST_IP));
  }

  @Test
  public void shouldReturnFalseAtCheckIpIsNotBlockedWhenFoundBlocked() {
    when(passwordEncoder.matches(TEST_IP, TEST_ENCRYPTED1_IP)).thenReturn(false);
    when(passwordEncoder.matches(TEST_IP, TEST_ENCRYPTED2_IP)).thenReturn(true);

    assertFalse(blockedIpService.checkIpIsNotBlocked(TEST_IP));
  }
}
