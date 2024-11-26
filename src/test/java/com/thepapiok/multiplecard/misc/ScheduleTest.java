package com.thepapiok.multiplecard.misc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.BlockedProduct;
import com.thepapiok.multiplecard.repositories.BlockedIpRepository;
import com.thepapiok.multiplecard.repositories.BlockedProductRepository;
import com.thepapiok.multiplecard.services.BlockedProductService;
import com.thepapiok.multiplecard.services.ProductService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

public class ScheduleTest {
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId("123456789012345678901234");
  private Schedule schedule;
  @Mock private BlockedProductService blockedProductService;
  @Mock private BlockedProductRepository blockedProductRepository;
  @Mock private BlockedIpRepository blockedIpRepository;
  @Mock private ProductService productService;
  @Mock private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    schedule =
        new Schedule(
            blockedProductRepository,
            blockedIpRepository,
            productService,
            blockedProductService,
            restTemplate);
  }

  @Test
  public void shouldGetEntityAtCheckHealth() {
    schedule.checkHealth();
    verify(restTemplate)
        .getForEntity("https://multiplecard-neq8.onrender.com/health", String.class);
  }

  @Test
  public void shouldOnlyDeleteBlockedIpsAtCheckBlockedWhenPeriodIsTooLess() {
    final int days = 10;
    List<BlockedProduct> blockedProducts = new ArrayList<>();
    BlockedProduct blockedProduct = new BlockedProduct();
    blockedProduct.setExpiredAt(LocalDate.now().minusDays(days));
    blockedProducts.add(blockedProduct);

    when(blockedProductRepository.findAll()).thenReturn(blockedProducts);

    schedule.checkBlocked();
    verify(blockedIpRepository).deleteAll();
    verifyNoInteractions(blockedProductService);
    verifyNoInteractions(productService);
  }

  @Test
  public void shouldDeleteBlockedIpsAndSendWaringWith7DaysAtCheckBlockedWhenPeriodIs7() {
    final int days = 7;
    List<BlockedProduct> blockedProducts = new ArrayList<>();
    BlockedProduct blockedProduct = new BlockedProduct();
    blockedProduct.setProductId(TEST_PRODUCT_ID);
    blockedProduct.setExpiredAt(LocalDate.now().minusDays(days));
    blockedProducts.add(blockedProduct);

    when(blockedProductRepository.findAll()).thenReturn(blockedProducts);

    schedule.checkBlocked();
    verify(blockedIpRepository).deleteAll();
    verify(blockedProductService)
        .sendWarning(TEST_PRODUCT_ID, "blockedProductService.warning.text_7days");
    verifyNoInteractions(productService);
  }

  @Test
  public void shouldDeleteBlockedIpsAndSendWaringWith1DayAtCheckBlockedWhenPeriodIs1() {
    final int days = 1;
    List<BlockedProduct> blockedProducts = new ArrayList<>();
    BlockedProduct blockedProduct = new BlockedProduct();
    blockedProduct.setProductId(TEST_PRODUCT_ID);
    blockedProduct.setExpiredAt(LocalDate.now().minusDays(days));
    blockedProducts.add(blockedProduct);

    when(blockedProductRepository.findAll()).thenReturn(blockedProducts);

    schedule.checkBlocked();
    verify(blockedIpRepository).deleteAll();
    verify(blockedProductService)
        .sendWarning(TEST_PRODUCT_ID, "blockedProductService.warning.text_1day");
    verifyNoInteractions(productService);
  }

  @Test
  public void shouldDeleteBlockedIpsAndDeleteProductAtCheckBlockedWhenPeriodIs0() {
    List<BlockedProduct> blockedProducts = new ArrayList<>();
    BlockedProduct blockedProduct = new BlockedProduct();
    blockedProduct.setProductId(TEST_PRODUCT_ID);
    blockedProduct.setExpiredAt(LocalDate.now());
    blockedProducts.add(blockedProduct);

    when(blockedProductRepository.findAll()).thenReturn(blockedProducts);

    schedule.checkBlocked();
    verify(blockedIpRepository).deleteAll();
    verify(productService).deleteProduct(TEST_PRODUCT_ID.toString());
    verifyNoInteractions(blockedProductService);
  }
}
