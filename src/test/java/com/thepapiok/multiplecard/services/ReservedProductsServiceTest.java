package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.ReservedProduct;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.ReservedProductsRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ReservedProductsServiceTest {
  private static final String TEST_CARD_ID = "123456789012345678901234";
  private static final String TEST_PRODUCT1_ID = "123456789012345678901231";
  private static final String TEST_PRODUCT2_ID = "123456789012345678901232";
  private static final String TEST_IP = "127.0.0.1";
  private static final String TEST_ENCRYPTED_IP = "safd234234dsdfasdfasdf";
  private static final ObjectId TEST_CARD_OBJECT_ID = new ObjectId(TEST_CARD_ID);
  private ReservedProductService reservedProductService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private ReservedProductsRepository reservedProductsRepository;
  @Mock private AggregationRepository aggregationRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    reservedProductService =
        new ReservedProductService(
            passwordEncoder, reservedProductsRepository, aggregationRepository);
  }

  @Test
  public void shouldReturnTrueAtReservedProductsWhenEverythingOk() {
    Map<ProductInfo, Integer> productsInfo = new HashMap<>();
    productsInfo.put(new ProductInfo(TEST_PRODUCT1_ID, true), 1);
    productsInfo.put(new ProductInfo(TEST_PRODUCT2_ID, true), 1);
    productsInfo.put(new ProductInfo("123456789012345678901233", false), 1);
    Map<ObjectId, Integer> reducedProducts = new HashMap<>();
    reducedProducts.put(new ObjectId(TEST_PRODUCT1_ID), 1);
    reducedProducts.put(new ObjectId(TEST_PRODUCT2_ID), 1);

    when(passwordEncoder.encode(TEST_IP)).thenReturn(TEST_ENCRYPTED_IP);
    when(aggregationRepository.reservedProducts(
            reducedProducts, TEST_ENCRYPTED_IP, TEST_CARD_OBJECT_ID))
        .thenReturn(true);

    assertTrue(reservedProductService.reservedProducts(productsInfo, TEST_IP, TEST_CARD_ID));
  }

  @Test
  public void shouldReturnFalseAtReservedProductsWhenErrorAtReservedProducts() {
    Map<ProductInfo, Integer> productsInfo = new HashMap<>();
    productsInfo.put(new ProductInfo(TEST_PRODUCT1_ID, true), 1);
    productsInfo.put(new ProductInfo(TEST_PRODUCT2_ID, true), 1);
    productsInfo.put(new ProductInfo("123456789012345678901233", false), 1);
    Map<ObjectId, Integer> reducedProducts = new HashMap<>();
    reducedProducts.put(new ObjectId(TEST_PRODUCT1_ID), 1);
    reducedProducts.put(new ObjectId(TEST_PRODUCT2_ID), 1);

    when(passwordEncoder.encode(TEST_IP)).thenReturn(TEST_ENCRYPTED_IP);
    when(aggregationRepository.reservedProducts(
            reducedProducts, TEST_ENCRYPTED_IP, TEST_CARD_OBJECT_ID))
        .thenReturn(false);

    assertFalse(reservedProductService.reservedProducts(productsInfo, TEST_IP, TEST_CARD_ID));
  }

  @Test
  public void shouldReturnTrueAtCheckReservedProductsIsLessThan100ByCardIdWhenEverythingOk() {
    final int countReservedProductsByCardId = 100;
    when(reservedProductsRepository.countByCardId(TEST_CARD_OBJECT_ID))
        .thenReturn(countReservedProductsByCardId);

    assertTrue(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID));
  }

  @Test
  public void shouldReturnFalseAtCheckReservedProductsIsLessThan100ByCardIdWhenFoundMoreThan100() {
    final int countReservedProductsByCardId = 101;
    when(reservedProductsRepository.countByCardId(TEST_CARD_OBJECT_ID))
        .thenReturn(countReservedProductsByCardId);

    assertFalse(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID));
  }

  @Test
  public void shouldReturnTrueAtCheckReservedProductsIsLessThan100ByEncryptedIpWhenEverythingOk() {
    final int countReservedProductsByEncryptedIp = 100;
    List<ReservedProduct> reservedProducts = new ArrayList<>();
    ReservedProduct reservedProduct1 = new ReservedProduct();
    reservedProduct1.setEncryptedIp("fdsgdsfrdt3342342134");
    reservedProducts.add(reservedProduct1);
    for (int i = 1; i <= countReservedProductsByEncryptedIp; i++) {
      ReservedProduct reservedProduct = new ReservedProduct();
      reservedProduct.setEncryptedIp(TEST_ENCRYPTED_IP);
      reservedProducts.add(reservedProduct);
    }

    when(reservedProductsRepository.findAll()).thenReturn(reservedProducts);
    when(passwordEncoder.matches(TEST_IP, TEST_ENCRYPTED_IP)).thenReturn(true);

    assertTrue(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(TEST_IP));
  }

  @Test
  public void
      shouldReturnFalseAtCheckReservedProductsIsLessThan100ByEncryptedIpWhenFoundMoreThan100() {
    final int countReservedProductsByEncryptedIp = 101;
    List<ReservedProduct> reservedProducts = new ArrayList<>();
    ReservedProduct reservedProduct1 = new ReservedProduct();
    reservedProduct1.setEncryptedIp("fdsgdsfrdt3342342134");
    reservedProducts.add(reservedProduct1);
    for (int i = 1; i <= countReservedProductsByEncryptedIp; i++) {
      ReservedProduct reservedProduct = new ReservedProduct();
      reservedProduct.setEncryptedIp(TEST_ENCRYPTED_IP);
      reservedProducts.add(reservedProduct);
    }

    when(reservedProductsRepository.findAll()).thenReturn(reservedProducts);
    when(passwordEncoder.matches(TEST_IP, TEST_ENCRYPTED_IP)).thenReturn(true);

    assertFalse(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(TEST_IP));
  }
}
