package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Refund;
import com.thepapiok.multiplecard.repositories.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundService {
  private final RefundRepository refundRepository;

  @Autowired
  public RefundService(RefundRepository refundRepository) {
    this.refundRepository = refundRepository;
  }

  public boolean checkExistsAlreadyRefund(String orderId) {
    return refundRepository.existsByOrderId(orderId);
  }

  public void createRefund(String orderId) {
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
