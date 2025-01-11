package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.dto.ReportDTO;
import com.thepapiok.multiplecard.dto.ReportsDTO;
import com.thepapiok.multiplecard.dto.ReviewAtReportDTO;
import com.thepapiok.multiplecard.services.ConvertService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ReportService;
import com.thepapiok.multiplecard.services.ReviewService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReportControllerTest {
  private static final String TEST_ID = "123456789012345678901234";
  private static final String TRUE_VALUE = "true";
  private static final String FALSE_VALUE = "false";
  private static final String IDS_PARAM = "ids";
  private static final String REPORTS_URL = "/reports";
  private static final String ID_PARAM = "id";
  private static final String REPORTS_PAGE = "reportsPage";
  private static final String IS_PRODUCT_PARAM = "isProduct";
  private static final String PL_LANGUAGE = "pl";

  @Autowired private MockMvc mockMvc;
  @MockBean private ConvertService convertService;
  @MockBean private ReportService reportService;
  @MockBean private ProductService productService;
  @MockBean private ReviewService reviewService;
  @MockBean private AdminPanelController adminPanelController;

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnReportsPageAtReportsPageWhenReportNotFoundButIdsIsNotEmptyAndIsProduct()
      throws Exception {
    final int testPrice = 5234;
    final int testQuantity = 2;
    final int testNewPrice = 2442;
    final int testStartAtYear = 2012;
    final int testStartAtMonth = 1;
    final int testStartAtDay = 3;
    final int testExpiredAtYear = 2013;
    final int testExpiredAtMonth = 7;
    final int testExpiredAtDay = 23;
    final LocalDate testStartAt = LocalDate.of(testStartAtYear, testStartAtMonth, testStartAtDay);
    final LocalDate testExpiredAt =
        LocalDate.of(testExpiredAtYear, testExpiredAtMonth, testExpiredAtDay);
    HashSet<String> ids = new HashSet<>();
    ids.add("sfdfdsdfs12312");
    ids.add("fsadfasdsfdsdf1231");
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(IDS_PARAM, ids);
    List<ReportDTO> reports = getReportsAtReportsPage();
    ReportsDTO reportsDTO = new ReportsDTO();
    reportsDTO.setReports(reports);
    reportsDTO.setId(TEST_ID);
    reportsDTO.setProduct(true);
    HashSet<String> expectedIds = new HashSet<>();
    expectedIds.add(TEST_ID);
    ProductWithShopDTO productWithShopDTO = new ProductWithShopDTO();
    productWithShopDTO.setShopName("testShopName");
    productWithShopDTO.setShopId(new ObjectId());
    productWithShopDTO.setShopImageUrl("testShopImageUrl");
    productWithShopDTO.setActive(true);
    productWithShopDTO.setDescription("testDescription");
    productWithShopDTO.setProductId(TEST_ID);
    productWithShopDTO.setProductName("testProductName");
    productWithShopDTO.setProductImageUrl("testProductImageUrl");
    productWithShopDTO.setPrice(testPrice);
    productWithShopDTO.setQuantityPromotion(testQuantity);
    productWithShopDTO.setNewPricePromotion(testNewPrice);
    productWithShopDTO.setStartAtPromotion(testStartAt);
    productWithShopDTO.setExpiredAtPromotion(testExpiredAt);

    when(convertService.getIds(httpSession)).thenReturn(ids);
    when(reportService.getReport(ids)).thenReturn(null);
    when(reportService.getReport(new HashSet<>())).thenReturn(reportsDTO);
    when(productService.getProductWithShopDTOById(TEST_ID)).thenReturn(productWithShopDTO);

    mockMvc
        .perform(get(REPORTS_URL).session(httpSession))
        .andExpect(model().attribute("product", productWithShopDTO))
        .andExpect(model().attribute(ID_PARAM, TEST_ID))
        .andExpect(model().attribute(IS_PRODUCT_PARAM, true))
        .andExpect(model().attribute("reports", reports))
        .andExpect(view().name(REPORTS_PAGE));
    assertEquals(expectedIds, httpSession.getAttribute(IDS_PARAM));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnReportsPageAtReportsPageWhenReportFoundAndIsReview() throws Exception {
    final int testCount = 2;
    final int testRating = 5;
    HashSet<String> ids = new HashSet<>();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(IDS_PARAM, ids);
    List<ReportDTO> reports = getReportsAtReportsPage();
    ReportsDTO reportsDTO = new ReportsDTO();
    reportsDTO.setReports(reports);
    reportsDTO.setId(TEST_ID);
    reportsDTO.setProduct(false);
    HashSet<String> expectedIds = new HashSet<>();
    expectedIds.add(TEST_ID);
    ReviewAtReportDTO reviewAtReportDTO = new ReviewAtReportDTO();
    reviewAtReportDTO.setId(TEST_ID);
    reviewAtReportDTO.setRating(testRating);
    reviewAtReportDTO.setCount(testCount);
    reviewAtReportDTO.setDescription("testDescription");
    reviewAtReportDTO.setFirstName("testFirstName");

    when(convertService.getIds(httpSession)).thenReturn(ids);
    when(reportService.getReport(ids)).thenReturn(reportsDTO);
    when(reportService.getReport(new HashSet<>())).thenReturn(reportsDTO);
    when(reviewService.getReviewById(TEST_ID)).thenReturn(reviewAtReportDTO);

    mockMvc
        .perform(get(REPORTS_URL).session(httpSession))
        .andExpect(model().attribute("review", reviewAtReportDTO))
        .andExpect(model().attribute(ID_PARAM, TEST_ID))
        .andExpect(model().attribute(IS_PRODUCT_PARAM, false))
        .andExpect(model().attribute("reports", reports))
        .andExpect(view().name(REPORTS_PAGE));
    assertEquals(expectedIds, httpSession.getAttribute(IDS_PARAM));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnReportsPageAtReportsPasgeWhenReportFoundAndIsReview() throws Exception {
    HashSet<String> ids = new HashSet<>();
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(IDS_PARAM, ids);

    when(convertService.getIds(httpSession)).thenReturn(ids);
    when(reportService.getReport(ids)).thenReturn(null);

    mockMvc.perform(get(REPORTS_URL).session(httpSession)).andExpect(view().name(REPORTS_PAGE));
    assertEquals(ids, httpSession.getAttribute(IDS_PARAM));
  }

  private List<ReportDTO> getReportsAtReportsPage() {
    final String testFirstName1 = "testFirstName1";
    final String testLastName1 = "testFirstName1";
    final String testUserId1 = "afdsfsa123dssddfaffdasf";
    final String testDescription1 = "asdfasffsdasafdsafd";
    final String testFirstName2 = "testFirstName2";
    final String testLastName2 = "testFirstName2";
    final String testUserId2 = "dfsf123123dffsasdfsdasdafas";
    final String testDescription2 = "aadsfafsd2131234123";
    ReportDTO reportDTO1 = new ReportDTO();
    reportDTO1.setFirstName(testFirstName1);
    reportDTO1.setLastName(testLastName1);
    reportDTO1.setDescription(testDescription1);
    reportDTO1.setUserId(testUserId1);
    ReportDTO reportDTO2 = new ReportDTO();
    reportDTO2.setFirstName(testFirstName2);
    reportDTO2.setLastName(testLastName2);
    reportDTO2.setDescription(testDescription2);
    reportDTO2.setUserId(testUserId2);
    List<ReportDTO> reports = new ArrayList<>();
    reports.add(reportDTO1);
    reports.add(reportDTO2);
    return reports;
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtNextReportWhenEverythingOk() throws Exception {
    when(reportService.removeReport(TEST_ID)).thenReturn(true);

    performPostAtNextReport(TRUE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtNextReportWhenErrorAtRemoveReport() throws Exception {
    when(reportService.removeReport(TEST_ID)).thenReturn(false);

    performPostAtNextReport(FALSE_VALUE);
  }

  private void performPostAtNextReport(String content) throws Exception {
    mockMvc
        .perform(post("/reject_report").param(ID_PARAM, TEST_ID))
        .andExpect(content().string(content));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtBlockUserWhenErrorAtBlockUser() throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, true, locale)).thenReturn(false);

    performPostAtBlockUser(FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtBlockUserWhenEverythingOk() throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, true, locale)).thenReturn(true);
    when(reportService.removeReport(TEST_ID)).thenReturn(true);

    performPostAtBlockUser(TRUE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtBlockUserWhenErrorAtRemoveReport() throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, true, locale)).thenReturn(true);
    when(reportService.removeReport(TEST_ID)).thenReturn(false);

    performPostAtBlockUser(FALSE_VALUE);
  }

  private void performPostAtBlockUser(String content) throws Exception {
    mockMvc
        .perform(
            post("/block_at_report").param(ID_PARAM, TEST_ID).param(IS_PRODUCT_PARAM, TRUE_VALUE))
        .andExpect(content().string(content));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtBlockAndDeleteWhenErrorAtBlockUser() throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, true, locale)).thenReturn(false);

    performPostAtBlockAndDelete(TRUE_VALUE, FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtBlockAndDeleteWhenIsProductAndEverythingOk() throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, true, locale)).thenReturn(true);
    when(adminPanelController.deleteProduct(TEST_ID)).thenReturn(true);

    performPostAtBlockAndDelete(TRUE_VALUE, TRUE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtBlockAndDeleteWhenIsProductAndErrorAtDeleteProduct()
      throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, true, locale)).thenReturn(true);
    when(adminPanelController.deleteProduct(TEST_ID)).thenReturn(false);

    performPostAtBlockAndDelete(TRUE_VALUE, FALSE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnTrueAtBlockAndDeleteWhenIsNotProductAndEverythingOk() throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, false, locale)).thenReturn(true);
    when(adminPanelController.deleteReview(TEST_ID)).thenReturn(true);

    performPostAtBlockAndDelete(FALSE_VALUE, TRUE_VALUE);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldReturnFalseAtBlockAndDeleteWhenIsNotProductAndErrorAtDeleteProduct()
      throws Exception {
    Locale locale = new Locale.Builder().setLanguage(PL_LANGUAGE).build();

    when(adminPanelController.blockUser(TEST_ID, false, locale)).thenReturn(true);
    when(adminPanelController.deleteReview(TEST_ID)).thenReturn(false);

    performPostAtBlockAndDelete(FALSE_VALUE, FALSE_VALUE);
  }

  private void performPostAtBlockAndDelete(String isProduct, String content) throws Exception {
    mockMvc
        .perform(
            post("/delete_and_block").param(ID_PARAM, TEST_ID).param(IS_PRODUCT_PARAM, isProduct))
        .andExpect(content().string(content));
  }
}
