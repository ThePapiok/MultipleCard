package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Refund;
import com.thepapiok.multiplecard.repositories.RefundRepository;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class RefundService {
  private final RefundRepository refundRepository;
  private final EmailService emailService;
  private final MessageSource messageSource;

  @Autowired
  public RefundService(
      RefundRepository refundRepository, EmailService emailService, MessageSource messageSource) {
    this.refundRepository = refundRepository;
    this.emailService = emailService;
    this.messageSource = messageSource;
  }

  public boolean checkExistsAlreadyRefund(String orderId) {
    return refundRepository.existsByOrderId(orderId);
  }

  public void createRefund(String orderId, String localeText, String email) {
    Locale locale;
    if ("pl".equals(localeText)) {
      locale = Locale.getDefault();
    } else {
      locale = new Locale.Builder().setLanguage("eng").build();
    }
    emailService.sendEmail(
        messageSource.getMessage("buyProducts.refund.message", null, locale),
        email,
        messageSource.getMessage("buyProducts.refund.title", null, locale) + " - " + orderId);
    Refund refund = new Refund();
    refund.setOrderId(orderId);
    refund.setRefunded(false);
    refundRepository.save(refund);
  }

  public void updateRefund(String orderId) {
    Refund refund = refundRepository.findByOrderId(orderId);
    refund.setRefunded(true);
    refundRepository.save(refund);
  }
}
