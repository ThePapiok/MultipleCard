package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Report;
import com.thepapiok.multiplecard.dto.ReportsDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ReportRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
  private final ReportRepository reportRepository;
  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;

  @Autowired
  public ReportService(
      ReportRepository reportRepository,
      AccountRepository accountRepository,
      ProductRepository productRepository) {
    this.reportRepository = reportRepository;
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
  }

  public boolean addReport(boolean isProduct, String reportedId, String phone, String description) {
    try {
      Report report = new Report();
      report.setReportedId(new ObjectId(reportedId));
      report.setCreatedAt(LocalDateTime.now());
      report.setDescription(description);
      report.setProduct(isProduct);
      report.setUserId(accountRepository.findIdByPhone(phone).getId());
      reportRepository.save(report);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean checkReportAlreadyExists(String reportedId, String phone) {
    try {
      return reportRepository.existsByUserIdAndReportedId(
          accountRepository.findIdByPhone(phone).getId(), new ObjectId(reportedId));
    } catch (Exception e) {
      return true;
    }
  }

  public boolean checkIsOwner(String reportedId, String phone, boolean isProduct) {
    try {
      if (!isProduct) {
        return accountRepository.findIdByPhone(phone).getId().equals(new ObjectId(reportedId));
      } else {
        Product product = productRepository.findShopIdById(new ObjectId(reportedId));
        return product == null;
      }
    } catch (Exception e) {
      return true;
    }
  }

  public ReportsDTO getReport(HashSet<String> ids) {
    return reportRepository.getFirstReport(ids.stream().map(ObjectId::new).toList());
  }

  public boolean removeReport(String id) {
    try {
      reportRepository.deleteAllByReportedId(new ObjectId(id));
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
