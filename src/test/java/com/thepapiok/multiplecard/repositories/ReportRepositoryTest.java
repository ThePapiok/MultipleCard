package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Report;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ReportDTO;
import com.thepapiok.multiplecard.dto.ReportsDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class ReportRepositoryTest {
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private Report report3;
  private Report report4;
  private Report report1;
  private User user1;
  private User user2;
  @Autowired private ReportRepository reportRepository;
  @Autowired private UserRepository userRepository;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final int testPoints = 500;
    Address address = new Address();
    address.setCity("testCity");
    address.setStreet("testStreet");
    address.setCountry("testCountry");
    address.setProvince("testProvince");
    address.setHouseNumber("testHouseNumber");
    address.setPostalCode("testPostalCode");
    address.setApartmentNumber(null);
    user1 = new User();
    user1.setFirstName("testFirstName1");
    user1.setLastName("testLastName1");
    user1.setPoints(0);
    user1.setRestricted(false);
    user1.setAddress(address);
    user1 = userRepository.save(user1);
    user2 = new User();
    user2.setFirstName("testFirstName2");
    user2.setLastName("testLastName2");
    user2.setPoints(testPoints);
    user2.setRestricted(true);
    user2.setAddress(address);
    user2 = userRepository.save(user2);
    report1 = new Report();
    report1.setReportedId(new ObjectId());
    report1.setProduct(true);
    report1.setUserId(user1.getId());
    report1.setDescription("Zle zachowanie");
    report1.setCreatedAt(LocalDateTime.now());
    report1 = reportRepository.save(report1);
    Report report2 = new Report();
    report2.setReportedId(new ObjectId());
    report2.setProduct(true);
    report2.setUserId(user1.getId());
    report2.setDescription("Zly produkt");
    report2.setCreatedAt(LocalDateTime.now().plusHours(2));
    reportRepository.save(report2);
    report3 = new Report();
    report3.setReportedId(TEST_ID);
    report3.setProduct(false);
    report3.setUserId(user1.getId());
    report3.setDescription("zła opinia");
    report3.setCreatedAt(LocalDateTime.now().plusHours(1));
    report3 = reportRepository.save(report3);
    report4 = new Report();
    report4.setReportedId(TEST_ID);
    report4.setProduct(false);
    report4.setUserId(user2.getId());
    report4.setDescription("Koszmara opinia");
    report4.setCreatedAt(LocalDateTime.now().plusHours(2));
    report4 = reportRepository.save(report4);
  }

  @AfterEach
  public void cleanUp() {
    reportRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void shouldReturnReportsDTOAtGetFirstReportWhenEverythingOk() {
    ReportDTO reportDTO1 = new ReportDTO();
    reportDTO1.setUserId(user1.getId().toString());
    reportDTO1.setDescription(report3.getDescription());
    reportDTO1.setFirstName(user1.getFirstName());
    reportDTO1.setLastName(user1.getLastName());
    ReportDTO reportDTO2 = new ReportDTO();
    reportDTO2.setUserId(user2.getId().toString());
    reportDTO2.setDescription(report4.getDescription());
    reportDTO2.setFirstName(user2.getFirstName());
    reportDTO2.setLastName(user2.getLastName());
    ReportsDTO reportsDTO = new ReportsDTO();
    reportsDTO.setProduct(false);
    reportsDTO.setId(TEST_ID.toString());
    reportsDTO.setReports(List.of(reportDTO1, reportDTO2));

    assertEquals(reportsDTO, reportRepository.getFirstReport(List.of(report1.getReportedId())));
  }
}
