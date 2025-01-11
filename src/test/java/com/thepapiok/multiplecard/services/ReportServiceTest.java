package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Report;
import com.thepapiok.multiplecard.dto.ReportDTO;
import com.thepapiok.multiplecard.dto.ReportsDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ReportRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class ReportServiceTest {
  private static final LocalDateTime TEST_DATE = LocalDateTime.now();
  private static final String TEST_PHONE = "+324123412342134";
  private static final String TEST_DESCRIPTION = "Testsdfsdagasdfasdf";
  private static final String TEST_ID = "123456789012345678901234";
  private static final ObjectId TEST_OBJECT_ID = new ObjectId(TEST_ID);
  private static final ObjectId TEST_OBJECT_OTHER_ID = new ObjectId("523456789012345678901234");
  private static MockedStatic<LocalDateTime> localDateTimeMockedStatic;
  private ReportService reportService;
  @Mock private ReportRepository reportRepository;
  @Mock private AccountRepository accountRepository;
  @Mock private ProductRepository productRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    reportService = new ReportService(reportRepository, accountRepository, productRepository);
  }

  @BeforeAll
  public static void setStatics() {
    localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_DATE);
  }

  @AfterAll
  public static void cleanStatics() {
    localDateTimeMockedStatic.close();
  }

  @Test
  public void shouldReturnTrueAtAddReportWhenEverythingOk() {
    Account account = new Account();
    account.setId(TEST_OBJECT_OTHER_ID);
    Report expectedReport = new Report();
    expectedReport.setReportedId(TEST_OBJECT_ID);
    expectedReport.setCreatedAt(TEST_DATE);
    expectedReport.setDescription(TEST_DESCRIPTION);
    expectedReport.setProduct(true);
    expectedReport.setUserId(TEST_OBJECT_OTHER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);

    assertTrue(reportService.addReport(true, TEST_ID, TEST_PHONE, TEST_DESCRIPTION));
    verify(reportRepository).save(expectedReport);
  }

  @Test
  public void shouldReturnFalseAtAddReportWhenGetException() {
    Account account = new Account();
    account.setId(TEST_OBJECT_OTHER_ID);
    Report expectedReport = new Report();
    expectedReport.setReportedId(TEST_OBJECT_ID);
    expectedReport.setCreatedAt(TEST_DATE);
    expectedReport.setDescription(TEST_DESCRIPTION);
    expectedReport.setProduct(true);
    expectedReport.setUserId(TEST_OBJECT_OTHER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(reportRepository.save(expectedReport)).thenThrow(MongoWriteException.class);

    assertFalse(reportService.addReport(true, TEST_ID, TEST_PHONE, TEST_DESCRIPTION));
    verify(reportRepository).save(expectedReport);
  }

  @Test
  public void shouldReturnTrueAtCheckReportAlreadyExistsWhenReportFound() {
    Account account = new Account();
    account.setId(TEST_OBJECT_OTHER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(reportRepository.existsByUserIdAndReportedId(TEST_OBJECT_OTHER_ID, TEST_OBJECT_ID))
        .thenReturn(true);

    assertTrue(reportService.checkReportAlreadyExists(TEST_ID, TEST_PHONE));
  }

  @Test
  public void shouldReturnFalseAtCheckReportAlreadyExistsWhenReportNotFound() {
    Account account = new Account();
    account.setId(TEST_OBJECT_OTHER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(reportRepository.existsByUserIdAndReportedId(TEST_OBJECT_OTHER_ID, TEST_OBJECT_ID))
        .thenReturn(false);

    assertFalse(reportService.checkReportAlreadyExists(TEST_ID, TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtCheckReportAlreadyExistsWhenGetException() {
    Account account = new Account();
    account.setId(TEST_OBJECT_OTHER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(reportRepository.existsByUserIdAndReportedId(TEST_OBJECT_OTHER_ID, TEST_OBJECT_ID))
        .thenThrow(MongoWriteException.class);

    assertTrue(reportService.checkReportAlreadyExists(TEST_ID, TEST_PHONE));
  }

  @Test
  public void shouldReturnFalseAtCheckIsOwnerWhenIsProductAndFoundProduct() {
    when(productRepository.findShopIdById(TEST_OBJECT_ID)).thenReturn(new Product());

    assertFalse(reportService.checkIsOwner(TEST_ID, TEST_PHONE, true));
  }

  @Test
  public void shouldReturnTrueAtCheckIsOwnerWhenIsProductAndNotFoundProduct() {
    when(productRepository.findShopIdById(TEST_OBJECT_ID)).thenReturn(null);

    assertTrue(reportService.checkIsOwner(TEST_ID, TEST_PHONE, true));
  }

  @Test
  public void shouldReturnFalseAtCheckIsOwnerWhenIsNotOwnerAndIsNotProduct() {
    Account account = new Account();
    account.setId(TEST_OBJECT_OTHER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);

    assertFalse(reportService.checkIsOwner(TEST_ID, TEST_PHONE, false));
  }

  @Test
  public void shouldReturnTrueAtCheckIsOwnerWhenIsOwnerAndIsNotProduct() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);

    assertTrue(reportService.checkIsOwner(TEST_ID, TEST_PHONE, false));
  }

  @Test
  public void shouldReturnTrueAtCheckIsOwnerWhenGetException() {
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenThrow(RuntimeException.class);

    assertTrue(reportService.checkIsOwner(TEST_ID, TEST_PHONE, false));
  }

  @Test
  public void shouldReturnReportsDTOAtGetReportWhenEverythingOk() {
    final String testFirstName1 = "testFirstName1";
    final String testLastName1 = "testFirstName1";
    final String testUserId1 = "afdsfsa123dssddfaffdasf";
    final String testFirstName2 = "testFirstName2";
    final String testLastName2 = "testFirstName2";
    final String testUserId2 = "dfsf123123dffsasdfsdasdafas";
    ReportDTO reportDTO1 = new ReportDTO();
    reportDTO1.setFirstName(testFirstName1);
    reportDTO1.setLastName(testLastName1);
    reportDTO1.setDescription(TEST_DESCRIPTION);
    reportDTO1.setUserId(testUserId1);
    ReportDTO reportDTO2 = new ReportDTO();
    reportDTO2.setFirstName(testFirstName2);
    reportDTO2.setLastName(testLastName2);
    reportDTO2.setDescription(TEST_DESCRIPTION);
    reportDTO2.setUserId(testUserId2);
    List<ReportDTO> reports = new ArrayList<>();
    reports.add(reportDTO1);
    reports.add(reportDTO2);
    ReportsDTO reportsDTO = new ReportsDTO();
    reportsDTO.setReports(reports);
    reportsDTO.setId(TEST_ID);
    reportsDTO.setProduct(true);
    HashSet<String> set = new HashSet<>();

    when(reportRepository.getFirstReport(List.of())).thenReturn(reportsDTO);

    assertEquals(reportsDTO, reportService.getReport(set));
  }

  @Test
  public void shouldReturnTrueAtRemoveReportWhenEverythingOk() {
    assertTrue(reportService.removeReport(TEST_ID));
    verify(reportRepository).deleteAllByReportedId(TEST_OBJECT_ID);
  }

  @Test
  public void shouldReturnFalseAtRemoveReportWhenGetException() {
    doThrow(MongoWriteException.class).when(reportRepository).deleteAllByReportedId(TEST_OBJECT_ID);

    assertFalse(reportService.removeReport(TEST_ID));
    verify(reportRepository).deleteAllByReportedId(TEST_OBJECT_ID);
  }
}
