package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.BlockedIp;
import com.thepapiok.multiplecard.repositories.BlockedIpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BlockedIpService {
  private final BlockedIpRepository blockedIpRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public BlockedIpService(
      BlockedIpRepository blockedIpRepository, PasswordEncoder passwordEncoder) {
    this.blockedIpRepository = blockedIpRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public boolean checkIpIsNotBlocked(String ip) {
    final int maxAttempts = 3;
    final int maxAmount = 100;
    for (BlockedIp blockedIp :
        blockedIpRepository.getAllByAmountGreaterThanOrAttemptsEquals(maxAmount, maxAttempts)) {
      if (passwordEncoder.matches(ip, blockedIp.getEncryptedIp())) {
        return false;
      }
    }
    return true;
  }
}
