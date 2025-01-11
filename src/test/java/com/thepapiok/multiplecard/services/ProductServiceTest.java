package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.BlockedProduct;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import com.thepapiok.multiplecard.dto.PageOwnerProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsWithShopDTO;
import com.thepapiok.multiplecard.dto.ProductAtCardDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductOrderDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.misc.ProductConverter;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.misc.ProductPayU;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.BlockedProductRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import com.thepapiok.multiplecard.repositories.ReportRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ProductServiceTest {
  private static final String TEST_ID = "103451789009876547211492";
  private static final String TEST_ID1 = "123456789012345678904312";
  private static final String TEST_ID2 = "023456589012345178904387";
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId(TEST_ID);
  private static final String TEST_PRODUCT_NAME = "name";
  private static final String TEST_BARCODE = "12345678901234567890123";
  private static final ObjectId TEST_OWNER_ID = new ObjectId("123451789012345678901234");
  private static final ObjectId TEST_CATEGORY_ID1 = new ObjectId("123456789009876544215678");
  private static final ObjectId TEST_CATEGORY_ID2 = new ObjectId("921456789019876524215678");
  private static final ObjectId TEST_CATEGORY_ID3 = new ObjectId("752145671905987654421528");
  private static final String TEST_PHONE = "+482314123412341423";
  private static final String TEST_CATEGORY_NAME1 = "category1";
  private static final String TEST_CATEGORY_NAME2 = "category2";
  private static final String TEST_CATEGORY_NAME3 = "category3";
  private static final String TEST_SHOP_NAME = "shopName";
  private static final String TEST_SHOP_IMAGE_URL = "shopImageUrl";
  private static final String TEST_URL = "url";
  private static final String COUNT_FILED = "count";
  private static final LocalDateTime TEST_DATE = LocalDateTime.now();
  private static MockedStatic<LocalDateTime> localDateTimeMockedStatic;
  private static List<String> testNameOfCategories;
  private static Product testProduct;
  private static Product testExpectedProduct;
  private static EditProductDTO testEditProductDTO;
  @Mock private CategoryService categoryService;
  @Mock private ProductConverter productConverter;
  @Mock private CloudinaryService cloudinaryService;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private ProductRepository productRepository;
  @Mock private AccountRepository accountRepository;
  @Mock private AggregationRepository aggregationRepository;
  @Mock private PromotionService promotionService;
  @Mock private OrderRepository orderRepository;
  @Mock private BlockedProductRepository blockedProductRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private ShopRepository shopRepository;
  @Mock private PromotionRepository promotionRepository;
  @Mock private ReservedProductService reservedProductService;
  @Mock private UserRepository userRepository;
  @Mock private ReportRepository reportRepository;
  private ProductService productService;

  @BeforeAll
  public static void setStaticMethods() {
    localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_DATE);
  }

  @AfterAll
  public static void cleanStaticMethods() {
    localDateTimeMockedStatic.close();
  }

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    productService =
        new ProductService(
            categoryService,
            productConverter,
            productRepository,
            cloudinaryService,
            accountRepository,
            mongoTransactionManager,
            mongoTemplate,
            aggregationRepository,
            promotionService,
            orderRepository,
            blockedProductRepository,
            categoryRepository,
            shopRepository,
            promotionRepository,
            reservedProductService,
            userRepository,
            reportRepository);
  }

  @Test
  public void shouldReturnTrueAtAddProductWhenEverythingOk() throws IOException {
    final int cents = 1123;
    final ObjectId categoryId = new ObjectId("123456789012345678901234");
    final ObjectId categoryOtherId = new ObjectId("223456789012345678901235");
    final ObjectId ownerId = new ObjectId("123456789012345678901235");
    final ObjectId productId = new ObjectId("123456789012345678901211");

    List<String> categoryNames = List.of(TEST_CATEGORY_NAME1, TEST_CATEGORY_NAME2);
    List<ObjectId> categories = List.of(categoryId, categoryOtherId);
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName(TEST_PRODUCT_NAME);
    addProductDTO.setCategory(categoryNames);
    addProductDTO.setBarcode("barcode");
    addProductDTO.setPrice("11.23");
    addProductDTO.setDescription("description");
    addProductDTO.setFile(new MockMultipartFile("file", new byte[0]));
    Product productAfterConverter = new Product();
    productAfterConverter.setName(addProductDTO.getName());
    productAfterConverter.setPrice(cents);
    productAfterConverter.setBarcode(addProductDTO.getBarcode());
    productAfterConverter.setDescription(addProductDTO.getDescription());
    Category category = new Category();
    category.setOwnerId(ownerId);
    category.setName(TEST_CATEGORY_NAME2);
    Category expectedCategory = new Category();
    expectedCategory.setOwnerId(ownerId);
    expectedCategory.setName(TEST_CATEGORY_NAME2);
    expectedCategory.setId(categoryOtherId);
    Product productWithCategories = new Product();
    productWithCategories.setName(addProductDTO.getName());
    productWithCategories.setPrice(cents);
    productWithCategories.setBarcode(addProductDTO.getBarcode());
    productWithCategories.setDescription(addProductDTO.getDescription());
    productWithCategories.setCategories(categories);
    productWithCategories.setShopId(ownerId);
    productWithCategories.setImageUrl("");
    productWithCategories.setUpdatedAt(TEST_DATE);
    Product productWithId = new Product();
    productWithId.setName(addProductDTO.getName());
    productWithId.setPrice(cents);
    productWithId.setBarcode(addProductDTO.getBarcode());
    productWithId.setDescription(addProductDTO.getDescription());
    productWithId.setCategories(categories);
    productWithId.setId(productId);
    productWithId.setShopId(ownerId);
    productWithId.setImageUrl("");
    productWithId.setUpdatedAt(TEST_DATE);
    Product expectedProduct = new Product();
    expectedProduct.setName(addProductDTO.getName());
    expectedProduct.setPrice(cents);
    expectedProduct.setBarcode(addProductDTO.getBarcode());
    expectedProduct.setDescription(addProductDTO.getDescription());
    expectedProduct.setCategories(categories);
    expectedProduct.setId(productId);
    expectedProduct.setImageUrl(TEST_URL);
    expectedProduct.setId(productId);
    expectedProduct.setShopId(ownerId);
    expectedProduct.setImageUrl("");
    expectedProduct.setUpdatedAt(TEST_DATE);

    when(productConverter.getEntity(addProductDTO)).thenReturn(productAfterConverter);
    when(categoryService.getCategoryIdByName(TEST_CATEGORY_NAME1)).thenReturn(categoryId);
    when(categoryService.getCategoryIdByName(TEST_CATEGORY_NAME2)).thenReturn(null);
    when(mongoTemplate.save(category)).thenReturn(expectedCategory);
    when(mongoTemplate.save(productWithCategories)).thenReturn(productWithId);
    when(cloudinaryService.addImage(addProductDTO.getFile().getBytes(), productId.toHexString()))
        .thenReturn(TEST_URL);

    assertTrue(productService.addProduct(addProductDTO, ownerId, categoryNames));
  }

  @Test
  public void shouldReturnFalseAtAddProductWhenGetException() {
    AddProductDTO addProductDTO = new AddProductDTO();

    when(productConverter.getEntity(addProductDTO)).thenThrow(RuntimeException.class);

    assertFalse(
        productService.addProduct(
            addProductDTO, new ObjectId("123456789012345678904321"), List.of()));
  }

  @Test
  public void shouldReturnTrueAtCheckOwnerHasTheSameNameProductWhenFound() {
    when(productRepository.existsByNameAndShopId(TEST_PRODUCT_NAME, TEST_OWNER_ID))
        .thenReturn(true);

    assertTrue(productService.checkOwnerHasTheSameNameProduct(TEST_OWNER_ID, TEST_PRODUCT_NAME));
  }

  @Test
  public void shouldReturnFalseAtCheckOwnerHasTheSameNameProductWhenNotFound() {
    when(productRepository.existsByNameAndShopId(TEST_PRODUCT_NAME, TEST_OWNER_ID))
        .thenReturn(false);

    assertFalse(productService.checkOwnerHasTheSameNameProduct(TEST_OWNER_ID, TEST_PRODUCT_NAME));
  }

  @Test
  public void shouldReturnTrueAtCheckOwnerHasTheSameBarcodeWhenFound() {
    when(productRepository.existsByBarcodeAndShopId(TEST_BARCODE, TEST_OWNER_ID)).thenReturn(true);

    assertTrue(productService.checkOwnerHasTheSameBarcode(TEST_OWNER_ID, TEST_BARCODE));
  }

  @Test
  public void shouldReturnFalseAtCheckOwnerHasTheSameBarcodeWhenNotFound() {
    when(productRepository.existsByBarcodeAndShopId(TEST_BARCODE, TEST_OWNER_ID)).thenReturn(false);

    assertFalse(productService.checkOwnerHasTheSameBarcode(TEST_OWNER_ID, TEST_BARCODE));
  }

  @Test
  public void shouldReturnTrueAtIsProductOwnerWhenIsOwner() {
    Account account = new Account();
    account.setId(TEST_OWNER_ID);
    Product product = new Product();
    product.setShopId(TEST_OWNER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.findShopIdById(TEST_OWNER_ID)).thenReturn(product);

    assertTrue(productService.isProductOwner(TEST_PHONE, TEST_OWNER_ID.toString()));
  }

  @Test
  public void shouldReturnFalseAtIsProductOwnerWhenIsNotOwner() {
    final ObjectId shopId = new ObjectId(TEST_ID);
    Account account = new Account();
    account.setId(TEST_OWNER_ID);
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setShopId(shopId);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.findShopIdById(TEST_PRODUCT_ID)).thenReturn(product);

    assertFalse(productService.isProductOwner(TEST_PHONE, TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtIsProductOwnerWhenProductNotFound() {
    Account account = new Account();
    account.setId(TEST_OWNER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.findShopIdById(TEST_PRODUCT_ID)).thenReturn(null);

    assertFalse(productService.isProductOwner(TEST_PHONE, TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtIsLessThanOriginalPriceWhenIsLess() {
    final int amount = 3000;
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setPrice(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertTrue(productService.isLessThanOriginalPrice("20.00", TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtIsLessThanOriginalPriceWhenIsMore() {
    final int amount = 3000;
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setPrice(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertFalse(productService.isLessThanOriginalPrice("40.00", TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtIsLessThanOriginalPriceWhenProductIsNotFound() {
    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    assertFalse(productService.isLessThanOriginalPrice("35.00", TEST_ID));
  }

  @Test
  public void shouldReturn35dot91AtGetPriceWhenAmountDecimalPart() {
    final int amount = 3501;
    final String amountWithoutCents = "35.01";
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setPrice(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertEquals(amountWithoutCents, productService.getPrice(TEST_ID));
  }

  @Test
  public void shouldReturn35dot91AtGetPriceWhenAmountHundredthPart() {
    final int amount = 3590;
    final String amountWithoutCents = "35.90";
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setPrice(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertEquals(amountWithoutCents, productService.getPrice(TEST_ID));
  }

  @Test
  public void shouldReturn35dot91AtGetPriceWhenAmountHasDecimalPartAndHundredthPart() {
    final int amount = 3591;
    final String amountWithoutCents = "35.91";
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setPrice(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertEquals(amountWithoutCents, productService.getPrice(TEST_ID));
  }

  @Test
  public void shouldReturnNull1AtGetPriceWhenProductNotFound() {
    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    assertNull(productService.getPrice(TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtDeleteProductsWhenEverythingOk() throws IOException {
    final ObjectId cardId1 = new ObjectId("103586189012345678101240");
    final ObjectId cardId2 = new ObjectId("203586189012345678101240");
    final ObjectId cardId3 = new ObjectId("303586189012345678101240");
    final int amount1 = 500;
    final int amount2 = 120;
    final int amount3 = 4350;
    final float centsPerZloty = 100;
    final String cardIdField = "cardId";
    final String pointsField = "points";
    Card card1 = new Card();
    card1.setId(cardId1);
    Card card2 = new Card();
    card2.setId(cardId2);
    Card card3 = new Card();
    card3.setId(cardId3);
    Order order1 = new Order();
    order1.setUsed(false);
    order1.setProductId(TEST_PRODUCT_ID);
    order1.setCardId(cardId1);
    order1.setPrice(amount1);
    Order order2 = new Order();
    order2.setUsed(false);
    order2.setProductId(TEST_PRODUCT_ID);
    order2.setCardId(cardId2);
    order2.setPrice(amount2);
    Order order3 = new Order();
    order3.setUsed(false);
    order3.setProductId(TEST_PRODUCT_ID);
    order3.setCardId(cardId3);
    order3.setPrice(amount3);
    List<Order> orders = List.of(order1, order2, order3);

    when(orderRepository.findAllByProductIdAndIsUsed(TEST_PRODUCT_ID, false)).thenReturn(orders);

    assertTrue(productService.deleteProducts(List.of(TEST_PRODUCT_ID)));
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdField).is(cardId1)),
            new Update().inc(pointsField, (Math.round(amount1 / centsPerZloty))),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdField).is(cardId2)),
            new Update().inc(pointsField, (Math.round(amount2 / centsPerZloty))),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdField).is(cardId3)),
            new Update().inc(pointsField, (Math.round(amount3 / centsPerZloty))),
            User.class);
    verify(mongoTemplate).remove(order1);
    verify(mongoTemplate).remove(order2);
    verify(mongoTemplate).remove(order3);
    verify(cloudinaryService).deleteImage(TEST_ID);
    verify(promotionService).deletePromotion(TEST_ID);
    verify(productRepository).deleteById(TEST_PRODUCT_ID);
    verify(blockedProductRepository).deleteByProductId(TEST_PRODUCT_ID);
    verify(reportRepository).deleteAllByReportedId(TEST_PRODUCT_ID);
  }

  @Test
  public void shouldReturnFalseAtDeleteProductsWhenGetException() throws IOException {
    doThrow(RuntimeException.class).when(cloudinaryService).deleteImage(TEST_ID);

    assertFalse(productService.deleteProducts(List.of(TEST_PRODUCT_ID)));
  }

  @Test
  public void shouldReturnTrueAtHasBlockWhenProductIsBlocked() {
    when(blockedProductRepository.existsByProductId(TEST_PRODUCT_ID)).thenReturn(true);

    assertTrue(productService.hasBlock(TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtHasBlockWhenProductIsNotBlocked() {
    when(blockedProductRepository.existsByProductId(TEST_PRODUCT_ID)).thenReturn(false);

    assertFalse(productService.hasBlock(TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtBlockProductWhenEverythingOk() {
    final int month = 30;
    BlockedProduct expectedBlockedProduct = new BlockedProduct();
    expectedBlockedProduct.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlockedProduct.setProductId(TEST_PRODUCT_ID);

    assertTrue(productService.blockProduct(TEST_ID));
    verify(blockedProductRepository).save(expectedBlockedProduct);
  }

  @Test
  public void shouldReturnFalseAtBlockProductWhenGetException() {
    final int month = 30;
    BlockedProduct expectedBlockedProduct = new BlockedProduct();
    expectedBlockedProduct.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlockedProduct.setProductId(TEST_PRODUCT_ID);

    when(blockedProductRepository.save(expectedBlockedProduct))
        .thenThrow(MongoWriteException.class);

    assertFalse(productService.blockProduct(TEST_ID));
    verify(blockedProductRepository).save(expectedBlockedProduct);
  }

  @Test
  public void shouldReturnTrueAtUnblockProductWhenEverythingOk() {
    final int month = 30;
    BlockedProduct expectedBlockedProduct = new BlockedProduct();
    expectedBlockedProduct.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlockedProduct.setProductId(TEST_PRODUCT_ID);

    when(blockedProductRepository.findByProductId(TEST_PRODUCT_ID))
        .thenReturn(expectedBlockedProduct);

    assertTrue(productService.unblockProduct(TEST_ID));
    verify(blockedProductRepository).delete(expectedBlockedProduct);
  }

  @Test
  public void shouldReturnFalseAtUnblockProductWhenGetException() {
    final int month = 30;
    BlockedProduct expectedBlockedProduct = new BlockedProduct();
    expectedBlockedProduct.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlockedProduct.setProductId(TEST_PRODUCT_ID);

    when(blockedProductRepository.findByProductId(TEST_PRODUCT_ID))
        .thenReturn(expectedBlockedProduct);
    doThrow(MongoWriteException.class)
        .when(blockedProductRepository)
        .delete(expectedBlockedProduct);

    assertFalse(productService.unblockProduct(TEST_ID));
    verify(blockedProductRepository).delete(expectedBlockedProduct);
  }

  @Test
  public void shouldReturnProductAtGetProductByIdWhenEverythingOk() {
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertEquals(product, productService.getProductById(TEST_ID));
  }

  @Test
  public void shouldReturnNullAtGetProductByIdWhenNotFoundProduct() {
    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    assertNull(productService.getProductById(TEST_ID));
  }

  @Test
  public void shouldReturnListOfStringAtGetCategoriesNamesWhenEverythingOk() {
    final ObjectId categoryId1 = new ObjectId("925158789012345678904321");
    final ObjectId categoryId2 = new ObjectId("225158789012345678904321");
    final ObjectId categoryId3 = new ObjectId("725158789012345678904321");
    final ObjectId categoryId4 = new ObjectId("825158789012345678904321");
    final String categoryName1 = "categoryName1";
    final String categoryName2 = "categoryName2";
    final String categoryName3 = "categoryName3";
    Category category1 = new Category();
    category1.setName(categoryName1);
    category1.setId(categoryId1);
    Category category2 = new Category();
    category2.setName(categoryName2);
    category2.setId(categoryId2);
    Category category3 = new Category();
    category3.setName(categoryName3);
    category3.setId(categoryId3);
    List<ObjectId> objectIds = List.of(categoryId1, categoryId2, categoryId3, categoryId4);
    Product product = new Product();
    product.setCategories(objectIds);
    List<String> expectedNames = List.of(categoryName1, categoryName2, categoryName3);

    when(categoryRepository.findById(categoryId1)).thenReturn(Optional.of(category1));
    when(categoryRepository.findById(categoryId2)).thenReturn(Optional.of(category2));
    when(categoryRepository.findById(categoryId3)).thenReturn(Optional.of(category3));
    when(categoryRepository.findById(categoryId4)).thenReturn(Optional.empty());

    assertEquals(expectedNames, productService.getCategoriesNames(product));
  }

  @Test
  public void shouldReturnEditProductDTOAtGetEditProductDTOWhenEverythingOk() {
    Product product = new Product();
    product.setName(TEST_PRODUCT_NAME);
    product.setBarcode(TEST_BARCODE);
    product.setId(TEST_PRODUCT_ID);
    EditProductDTO editProductDTO = new EditProductDTO();
    editProductDTO.setName(TEST_PRODUCT_NAME);
    editProductDTO.setBarcode(TEST_BARCODE);
    editProductDTO.setId(TEST_ID);

    when(productConverter.getDTO(product)).thenReturn(editProductDTO);

    assertEquals(editProductDTO, productService.getEditProductDTO(product));
  }

  @Test
  public void shouldReturnTrueAtEditProductWhenFileIsNull() {
    setDataForEditProduct();
    testEditProductDTO.setFile(new MockMultipartFile("file1", new byte[0]));

    assertTrue(productService.editProduct(testEditProductDTO, TEST_OWNER_ID, testNameOfCategories));
    verify(mongoTemplate).save(testExpectedProduct);
  }

  @Test
  public void shouldReturnTrueAtEditProductWhenHasFile() {
    setDataForEditProduct();
    final String testNewUrl = "newUrl";
    final byte[] bytes = "testFile".getBytes();
    final MultipartFile multipartFile = new MockMultipartFile("file1", bytes);
    EditProductDTO editProductDTO = new EditProductDTO();
    editProductDTO.setName(TEST_PRODUCT_NAME);
    editProductDTO.setBarcode(TEST_BARCODE);
    editProductDTO.setId(TEST_ID);
    editProductDTO.setCategory(testNameOfCategories);
    editProductDTO.setFile(multipartFile);
    Product expectedProduct = new Product();
    expectedProduct.setName(TEST_PRODUCT_NAME);
    expectedProduct.setBarcode(TEST_BARCODE);
    expectedProduct.setId(TEST_PRODUCT_ID);
    expectedProduct.setImageUrl(testNewUrl);
    expectedProduct.setCategories(List.of(TEST_CATEGORY_ID1, TEST_CATEGORY_ID2, TEST_CATEGORY_ID3));
    expectedProduct.setUpdatedAt(TEST_DATE);

    when(productConverter.getEntity(editProductDTO)).thenReturn(testProduct);
    when(cloudinaryService.addImage(bytes, TEST_ID)).thenReturn(testNewUrl);

    assertTrue(productService.editProduct(editProductDTO, TEST_OWNER_ID, testNameOfCategories));
    verify(mongoTemplate).save(expectedProduct);
  }

  @Test
  public void shouldReturnFalseAtEditProductWhenGetException() {
    setDataForEditProduct();

    when(mongoTemplate.save(testProduct)).thenThrow(MongoWriteException.class);

    assertFalse(
        productService.editProduct(testEditProductDTO, TEST_OWNER_ID, testNameOfCategories));
  }

  private void setDataForEditProduct() {
    Category testCategory = new Category();
    testCategory.setName(TEST_CATEGORY_NAME3);
    testCategory.setOwnerId(TEST_OWNER_ID);
    Category testExpectedCategory = new Category();
    testExpectedCategory.setId(TEST_CATEGORY_ID3);
    testExpectedCategory.setName(TEST_CATEGORY_NAME3);
    testExpectedCategory.setOwnerId(TEST_OWNER_ID);
    testNameOfCategories = List.of(TEST_CATEGORY_NAME1, TEST_CATEGORY_NAME2, TEST_CATEGORY_NAME3);
    testProduct = new Product();
    testProduct.setName(TEST_PRODUCT_NAME);
    testProduct.setBarcode(TEST_BARCODE);
    testProduct.setId(TEST_PRODUCT_ID);
    testProduct.setImageUrl(TEST_URL);
    testProduct.setUpdatedAt(TEST_DATE);
    testEditProductDTO = new EditProductDTO();
    testEditProductDTO.setName(TEST_PRODUCT_NAME);
    testEditProductDTO.setBarcode(TEST_BARCODE);
    testEditProductDTO.setId(TEST_ID);
    testEditProductDTO.setCategory(testNameOfCategories);
    testExpectedProduct = new Product();
    testExpectedProduct.setName(TEST_PRODUCT_NAME);
    testExpectedProduct.setBarcode(TEST_BARCODE);
    testExpectedProduct.setId(TEST_PRODUCT_ID);
    testExpectedProduct.setImageUrl(TEST_URL);
    testExpectedProduct.setCategories(
        List.of(TEST_CATEGORY_ID1, TEST_CATEGORY_ID2, TEST_CATEGORY_ID3));
    testExpectedProduct.setUpdatedAt(TEST_DATE);

    when(productConverter.getEntity(testEditProductDTO)).thenReturn(testProduct);
    when(categoryService.getCategoryIdByName(TEST_CATEGORY_NAME1)).thenReturn(TEST_CATEGORY_ID1);
    when(categoryService.getCategoryIdByName(TEST_CATEGORY_NAME2)).thenReturn(TEST_CATEGORY_ID2);
    when(categoryService.getCategoryIdByName(TEST_CATEGORY_NAME3)).thenReturn(null);
    when(mongoTemplate.save(testCategory)).thenReturn(testExpectedCategory);
  }

  @Test
  public void shouldReturnPageProductsDTOAtGetProductsWhenEverythingOk() {
    final int maxPage = 5;
    List<ProductDTO> expectedProducts = setDataProductsDTO();
    PageProductsDTO pageProductsDTO = new PageProductsDTO();
    pageProductsDTO.setProducts(expectedProducts);
    pageProductsDTO.setMaxPage(maxPage);

    when(aggregationRepository.getProducts(TEST_PHONE, 1, COUNT_FILED, true, "", "", "", false))
        .thenReturn(pageProductsDTO);

    assertEquals(
        pageProductsDTO,
        productService.getProducts(TEST_PHONE, 1, COUNT_FILED, true, "", "", "", false));
  }

  @Test
  public void shouldReturnListOfProductWithShopDTOAtGetProductsByIdsWhenEverythingOk()
      throws JsonProcessingException {
    List<ProductDTO> productDTOS = setDataProductsDTO();
    ProductWithShopDTO product1 =
        new ProductWithShopDTO(productDTOS.get(0), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);
    ProductWithShopDTO product2 =
        new ProductWithShopDTO(productDTOS.get(1), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);
    String productsInfo1JSON = "{\"productId\": \"" + TEST_ID1 + "\", \"hasPromotion\": true}";
    String productsInfo2JSON = "{\"productId\": \"" + TEST_ID2 + "\", \"hasPromotion\": false}";
    ProductInfo productInfo1 = new ProductInfo(TEST_ID1, true);
    ProductInfo productInfo2 = new ProductInfo(TEST_ID2, false);

    when(aggregationRepository.findProductsByIdsAndType(List.of(productInfo1, productInfo2)))
        .thenReturn(List.of(product1, product2));

    assertEquals(
        List.of(product1, product2),
        productService.getProductsByIds(List.of(productsInfo1JSON, productsInfo2JSON)));
  }

  @Test
  public void shouldReturnEmptyListAtGetProductsByIdsWhenNoProductsInfo()
      throws JsonProcessingException {
    assertEquals(List.of(), productService.getProductsByIds(List.of()));
  }

  @Test
  public void shouldReturnPageProductsWithShopDTOAtGetProductsWithShopsWhenEverythingOk() {
    final int maxPage = 5;
    List<ProductDTO> expectedProducts = setDataProductsDTO();
    PageProductsDTO pageProductsDTO = new PageProductsDTO();
    pageProductsDTO.setProducts(expectedProducts);
    pageProductsDTO.setMaxPage(maxPage);

    ProductWithShopDTO product1 =
        new ProductWithShopDTO(expectedProducts.get(0), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);
    ProductWithShopDTO product2 =
        new ProductWithShopDTO(expectedProducts.get(1), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);
    Shop shop = new Shop();
    shop.setName(TEST_SHOP_NAME);
    shop.setImageUrl(TEST_SHOP_IMAGE_URL);
    PageProductsWithShopDTO pageProductsWithShopDTO = new PageProductsWithShopDTO();
    pageProductsWithShopDTO.setProducts(List.of(product1, product2));
    pageProductsWithShopDTO.setMaxPage(maxPage);

    when(aggregationRepository.getProducts(null, 1, COUNT_FILED, true, "", "", "", true))
        .thenReturn(pageProductsDTO);
    when(shopRepository.findImageUrlAndNameById(TEST_OWNER_ID)).thenReturn(shop);

    assertEquals(
        pageProductsWithShopDTO,
        productService.getProductsWithShops(1, COUNT_FILED, true, "", "", ""));
  }

  private List<ProductDTO> setDataProductsDTO() {
    final int testAmountProduct1 = 10;
    final int testAmountProduct2 = 222;
    final int testAmountPromotion1 = 5;
    final int testYearStartAtPromotion1 = 2024;
    final int testMonthStartAtPromotion1 = 1;
    final int testDayStartAtPromotion1 = 1;
    final int testYearExpiredAtPromotion1 = 2015;
    final int testMonthExpiredAtPromotion1 = 2;
    final int testDayExpiredAtPromotion1 = 3;
    ProductDTO productDTO1 = new ProductDTO();
    productDTO1.setActive(true);
    productDTO1.setProductId(TEST_ID1);
    productDTO1.setProductName("name1");
    productDTO1.setPrice(testAmountProduct1);
    productDTO1.setDescription("description1");
    productDTO1.setProductImageUrl("url1");
    productDTO1.setShopId(TEST_OWNER_ID);
    productDTO1.setStartAtPromotion(
        LocalDate.of(
            testYearStartAtPromotion1, testMonthStartAtPromotion1, testDayStartAtPromotion1));
    productDTO1.setExpiredAtPromotion(
        LocalDate.of(
            testYearExpiredAtPromotion1, testMonthExpiredAtPromotion1, testDayExpiredAtPromotion1));
    productDTO1.setNewPricePromotion(testAmountPromotion1);
    productDTO1.setQuantityPromotion(0);
    ProductDTO productDTO2 = new ProductDTO();
    productDTO2.setActive(false);
    productDTO2.setProductId(TEST_ID2);
    productDTO2.setProductName("name2");
    productDTO2.setPrice(testAmountProduct2);
    productDTO2.setDescription("description2");
    productDTO2.setProductImageUrl("url2");
    productDTO2.setShopId(TEST_OWNER_ID);
    productDTO2.setStartAtPromotion(null);
    productDTO2.setExpiredAtPromotion(null);
    productDTO2.setNewPricePromotion(0);
    productDTO2.setQuantityPromotion(0);
    return List.of(productDTO1, productDTO2);
  }

  @Test
  public void shouldReturnFalseAtCheckProductsQuantityWhenGetMoreThan10PerProducts() {
    final int badSizeOfProducts = 11;
    Map<ProductInfo, Integer> products = Map.of(new ProductInfo(TEST_ID, true), badSizeOfProducts);

    assertFalse(productService.checkProductsQuantity(products));
  }

  @Test
  public void shouldReturnFalseAtCheckProductsQuantityWhenGetLessThan1Product() {
    final int badSizeOfProducts = -5;
    Map<ProductInfo, Integer> products = Map.of(new ProductInfo(TEST_ID, true), badSizeOfProducts);

    assertFalse(productService.checkProductsQuantity(products));
  }

  @Test
  public void shouldReturnFalseAtCheckProductsQuantityWhenGetMoreThan100PerAllProducts() {
    final int goodSizeOfProducts = 10;
    final String testId3 = "123456789012345678904567";
    final String testId4 = "723456789015345678904567";
    final String testId5 = "923456780012145678904567";
    Map<ProductInfo, Integer> products = new HashMap<>();
    products.put(new ProductInfo(TEST_ID, true), goodSizeOfProducts);
    products.put(new ProductInfo(TEST_ID, false), goodSizeOfProducts);
    products.put(new ProductInfo(TEST_ID1, true), goodSizeOfProducts);
    products.put(new ProductInfo(TEST_ID1, false), goodSizeOfProducts);
    products.put(new ProductInfo(TEST_ID2, true), goodSizeOfProducts);
    products.put(new ProductInfo(TEST_ID2, false), goodSizeOfProducts);
    products.put(new ProductInfo(testId3, true), goodSizeOfProducts);
    products.put(new ProductInfo(testId3, false), goodSizeOfProducts);
    products.put(new ProductInfo(testId4, true), goodSizeOfProducts);
    products.put(new ProductInfo(testId4, false), goodSizeOfProducts);
    products.put(new ProductInfo(testId5, true), goodSizeOfProducts);
    products.put(new ProductInfo(testId5, false), goodSizeOfProducts);

    when(productRepository.existsById(new ObjectId(TEST_ID))).thenReturn(true);
    when(productRepository.existsById(new ObjectId(TEST_ID1))).thenReturn(true);
    when(productRepository.existsById(new ObjectId(TEST_ID2))).thenReturn(true);
    when(productRepository.existsById(new ObjectId(testId3))).thenReturn(true);
    when(productRepository.existsById(new ObjectId(testId4))).thenReturn(true);
    when(productRepository.existsById(new ObjectId(testId5))).thenReturn(true);

    assertFalse(productService.checkProductsQuantity(products));
  }

  @Test
  public void shouldReturnFalseAtCheckProductsQuantityWhenNotFoundProduct() {
    final int goodSizeOfProducts = 10;
    Map<ProductInfo, Integer> products = Map.of(new ProductInfo(TEST_ID, true), goodSizeOfProducts);

    when(productRepository.existsById(new ObjectId(TEST_ID))).thenReturn(false);

    assertFalse(productService.checkProductsQuantity(products));
  }

  @Test
  public void shouldReturnTrueAtCheckProductsQuantityWhenEverythingOk() {
    final int goodSizeOfProducts1 = 10;
    final int goodSizeOfProducts2 = 1;
    final int goodSizeOfProducts3 = 7;
    Map<ProductInfo, Integer> products = new HashMap<>();
    products.put(new ProductInfo(TEST_ID, true), goodSizeOfProducts1);
    products.put(new ProductInfo(TEST_ID1, false), goodSizeOfProducts2);
    products.put(new ProductInfo(TEST_ID2, true), goodSizeOfProducts3);

    when(productRepository.existsById(new ObjectId(TEST_ID))).thenReturn(true);
    when(productRepository.existsById(new ObjectId(TEST_ID1))).thenReturn(true);
    when(productRepository.existsById(new ObjectId(TEST_ID2))).thenReturn(true);

    assertTrue(productService.checkProductsQuantity(products));
  }

  @Test
  public void shouldReturnEmptyMapAtGetProductsInfoWhenGetEmptyMap() {
    assertEquals(Map.of(), productService.getProductsInfo(Map.of()));
  }

  @Test
  public void shouldReturnMapOfProductInfoAndIntegerAtGetProductsInfoWhenEverythingOk() {
    String productInfo1JSON = setNameAtBuyProducts(TEST_ID, true);
    String productInfo2JSON = setNameAtBuyProducts(TEST_ID1, false);
    Map<String, Integer> products = Map.of(productInfo1JSON, 1, productInfo2JSON, 1);
    Map<ProductInfo, Integer> expectedProducts =
        Map.of(new ProductInfo(TEST_ID, true), 1, new ProductInfo(TEST_ID1, false), 1);

    assertEquals(expectedProducts, productService.getProductsInfo(products));
  }

  @Test
  public void shouldReturnEmptyMapAtGetProductsInfoWhenErrorAtObjectMapper() {
    String badProductInfoJSON =
        """
            {
              "productId": """
            + TEST_ID
            + ","
            + """
              "hasPromotion": true
            }
            """;

    assertEquals(Map.of(), productService.getProductsInfo(Map.of(badProductInfoJSON, 1)));
  }

  @Test
  public void shouldReturnTrueAtBuyProductsWhenEverythingOk() {
    final int testPromotion1NewPrice = 444;
    final int testPromotion2NewPrice = 51;
    final int testPromotion3NewPrice = 100;
    final int testPromotion3Quantity = 100;
    final int testProduct1Quantity = 1;
    final int testProduct2Quantity = 2;
    final int testProduct3Quantity = 3;
    final int testProduct4Quantity = 1;
    final int testProduct5Quantity = 2;
    final int testProduct2Price = 500;
    final int testProduct5Price = 502;
    final String testProduct4Id = "123456709832145678091423";
    final String testCardId = "783456709832145678091423";
    final String testOrderId = "915456709832145678091423";
    final ObjectId testShopObjectId = new ObjectId("915451709832145628091425");
    final ObjectId testCardObjectId = new ObjectId(testCardId);
    final ObjectId testOrderObjectId = new ObjectId(testOrderId);
    final ObjectId testProduct1ObjectId = new ObjectId(TEST_ID);
    final ObjectId testProduct2ObjectId = new ObjectId(TEST_ID1);
    final ObjectId testProduct3ObjectId = new ObjectId(TEST_ID2);
    final ObjectId testProduct4ObjectId = new ObjectId(testProduct4Id);
    Promotion promotion1 = new Promotion();
    promotion1.setNewPrice(testPromotion1NewPrice);
    promotion1.setQuantity(null);
    promotion1.setProductId(testProduct3ObjectId);
    Promotion promotion2 = new Promotion();
    promotion2.setNewPrice(testPromotion2NewPrice);
    promotion2.setQuantity(1);
    promotion2.setProductId(testProduct1ObjectId);
    Promotion promotion3 = new Promotion();
    promotion3.setNewPrice(testPromotion3NewPrice);
    promotion3.setQuantity(testPromotion3Quantity);
    promotion3.setProductId(testProduct4ObjectId);
    Promotion expectedPromotion3 = new Promotion();
    expectedPromotion3.setNewPrice(testPromotion3NewPrice);
    expectedPromotion3.setQuantity(testPromotion3Quantity - 1);
    expectedPromotion3.setProductId(testProduct4ObjectId);
    List<ProductPayU> productPayUS = new ArrayList<>();
    ProductPayU productPayU1 = new ProductPayU();
    productPayU1.setQuantity(testProduct1Quantity);
    productPayU1.setUnitPrice(testPromotion2NewPrice);
    productPayU1.setName(setNameAtBuyProducts(TEST_ID, true));
    ProductPayU productPayU2 = new ProductPayU();
    productPayU2.setQuantity(testProduct2Quantity);
    productPayU2.setUnitPrice(testProduct2Price);
    productPayU2.setName(setNameAtBuyProducts(TEST_ID1, false));
    ProductPayU productPayU3 = new ProductPayU();
    productPayU3.setQuantity(testProduct3Quantity);
    productPayU3.setUnitPrice(testPromotion1NewPrice);
    productPayU3.setName(setNameAtBuyProducts(TEST_ID2, true));
    ProductPayU productPayU4 = new ProductPayU();
    productPayU4.setQuantity(testProduct4Quantity);
    productPayU4.setUnitPrice(testPromotion3NewPrice);
    productPayU4.setName(setNameAtBuyProducts(testProduct4Id, true));
    ProductPayU productPayU5 = new ProductPayU();
    productPayU5.setQuantity(testProduct5Quantity);
    productPayU5.setUnitPrice(testProduct5Price);
    productPayU5.setName(setNameAtBuyProducts(TEST_ID, false));
    productPayUS.add(productPayU1);
    productPayUS.add(productPayU2);
    productPayUS.add(productPayU3);
    productPayUS.add(productPayU4);
    productPayUS.add(productPayU5);
    Product product = new Product();
    product.setShopId(testShopObjectId);
    Order order1 = new Order();
    order1.setProductId(testProduct1ObjectId);
    order1.setUsed(false);
    order1.setCreatedAt(TEST_DATE);
    order1.setShopId(testShopObjectId);
    order1.setPrice(testPromotion2NewPrice);
    order1.setCardId(testCardObjectId);
    order1.setOrderId(testOrderObjectId);
    Order order2 = new Order();
    order2.setProductId(testProduct2ObjectId);
    order2.setUsed(false);
    order2.setCreatedAt(TEST_DATE);
    order2.setShopId(testShopObjectId);
    order2.setPrice(testProduct2Price);
    order2.setCardId(testCardObjectId);
    order2.setOrderId(testOrderObjectId);
    Order order3 = new Order();
    order3.setProductId(testProduct3ObjectId);
    order3.setUsed(false);
    order3.setCreatedAt(TEST_DATE);
    order3.setShopId(testShopObjectId);
    order3.setPrice(testPromotion1NewPrice);
    order3.setCardId(testCardObjectId);
    order3.setOrderId(testOrderObjectId);
    Order order4 = new Order();
    order4.setProductId(testProduct4ObjectId);
    order4.setUsed(false);
    order4.setCreatedAt(TEST_DATE);
    order4.setShopId(testShopObjectId);
    order4.setPrice(testPromotion3NewPrice);
    order4.setCardId(testCardObjectId);
    order4.setOrderId(testOrderObjectId);
    Order order5 = new Order();
    order5.setProductId(testProduct1ObjectId);
    order5.setUsed(false);
    order5.setCreatedAt(TEST_DATE);
    order5.setShopId(testShopObjectId);
    order5.setPrice(testProduct5Price);
    order5.setCardId(testCardObjectId);
    order5.setOrderId(testOrderObjectId);

    when(productRepository.findShopIdById(any(ObjectId.class))).thenReturn(product);
    when(promotionRepository.findByProductId(testProduct3ObjectId)).thenReturn(promotion1);
    when(promotionRepository.findByProductId(testProduct1ObjectId)).thenReturn(promotion2);
    when(promotionRepository.findByProductId(testProduct4ObjectId)).thenReturn(promotion3);

    assertTrue(productService.buyProducts(productPayUS, testCardId, testOrderId, null, null));
    verify(mongoTemplate).remove(promotion2);
    verify(mongoTemplate).save(expectedPromotion3);
    verify(mongoTemplate, times(testProduct1Quantity)).save(order1);
    verify(mongoTemplate, times(testProduct2Quantity)).save(order2);
    verify(mongoTemplate, times(testProduct3Quantity)).save(order3);
    verify(mongoTemplate, times(testProduct4Quantity)).save(order4);
    verify(mongoTemplate, times(testProduct5Quantity)).save(order5);
    verify(reservedProductService).deleteAllByOrderId(testOrderId);
  }

  @Test
  public void shouldReturnTrueAtBuyProductsWhenGetPoints() {
    final int testPromotion1NewPrice = 444;
    final int testPromotion2NewPrice = 51;
    final int testPromotion3NewPrice = 100;
    final int testPromotion3Quantity = 100;
    final int testProduct1Quantity = 1;
    final int testProduct2Quantity = 2;
    final int testProduct3Quantity = 3;
    final int testProduct4Quantity = 1;
    final int testProduct5Quantity = 2;
    final int testProduct2Price = 500;
    final int testProduct5Price = 502;
    final int testUserPoints = 1234;
    final int testPoints = 1004;
    final String testProduct4Id = "123456709832145678091423";
    final String testCardId = "783456709832145678091423";
    final String testOrderId = "915456709832145678091423";
    final ObjectId testShopObjectId = new ObjectId("915451709832145628091425");
    final ObjectId testCardObjectId = new ObjectId(testCardId);
    final ObjectId testOrderObjectId = new ObjectId(testOrderId);
    final ObjectId testProduct1ObjectId = new ObjectId(TEST_ID);
    final ObjectId testProduct2ObjectId = new ObjectId(TEST_ID1);
    final ObjectId testProduct3ObjectId = new ObjectId(TEST_ID2);
    final ObjectId testProduct4ObjectId = new ObjectId(testProduct4Id);
    Promotion promotion1 = new Promotion();
    promotion1.setNewPrice(testPromotion1NewPrice);
    promotion1.setQuantity(null);
    promotion1.setProductId(testProduct3ObjectId);
    Promotion promotion2 = new Promotion();
    promotion2.setNewPrice(testPromotion2NewPrice);
    promotion2.setQuantity(1);
    promotion2.setProductId(testProduct1ObjectId);
    Promotion promotion3 = new Promotion();
    promotion3.setNewPrice(testPromotion3NewPrice);
    promotion3.setQuantity(testPromotion3Quantity);
    promotion3.setProductId(testProduct4ObjectId);
    Promotion expectedPromotion3 = new Promotion();
    expectedPromotion3.setNewPrice(testPromotion3NewPrice);
    expectedPromotion3.setQuantity(testPromotion3Quantity - 1);
    expectedPromotion3.setProductId(testProduct4ObjectId);
    List<ProductPayU> productPayUS = new ArrayList<>();
    ProductPayU productPayU1 = new ProductPayU();
    productPayU1.setQuantity(testProduct1Quantity);
    productPayU1.setUnitPrice(testPromotion2NewPrice);
    productPayU1.setName(setNameAtBuyProducts(TEST_ID, true));
    ProductPayU productPayU2 = new ProductPayU();
    productPayU2.setQuantity(testProduct2Quantity);
    productPayU2.setUnitPrice(testProduct2Price);
    productPayU2.setName(setNameAtBuyProducts(TEST_ID1, false));
    ProductPayU productPayU3 = new ProductPayU();
    productPayU3.setQuantity(testProduct3Quantity);
    productPayU3.setUnitPrice(testPromotion1NewPrice);
    productPayU3.setName(setNameAtBuyProducts(TEST_ID2, true));
    ProductPayU productPayU4 = new ProductPayU();
    productPayU4.setQuantity(testProduct4Quantity);
    productPayU4.setUnitPrice(testPromotion3NewPrice);
    productPayU4.setName(setNameAtBuyProducts(testProduct4Id, true));
    ProductPayU productPayU5 = new ProductPayU();
    productPayU5.setQuantity(testProduct5Quantity);
    productPayU5.setUnitPrice(testProduct5Price);
    productPayU5.setName(setNameAtBuyProducts(TEST_ID, false));
    productPayUS.add(productPayU1);
    productPayUS.add(productPayU2);
    productPayUS.add(productPayU3);
    productPayUS.add(productPayU4);
    productPayUS.add(productPayU5);
    Product product = new Product();
    product.setShopId(testShopObjectId);
    Order order1 = new Order();
    order1.setProductId(testProduct1ObjectId);
    order1.setUsed(false);
    order1.setCreatedAt(TEST_DATE);
    order1.setShopId(testShopObjectId);
    order1.setPrice(testPromotion2NewPrice);
    order1.setCardId(testCardObjectId);
    order1.setOrderId(testOrderObjectId);
    Order order2 = new Order();
    order2.setProductId(testProduct2ObjectId);
    order2.setUsed(false);
    order2.setCreatedAt(TEST_DATE);
    order2.setShopId(testShopObjectId);
    order2.setPrice(testProduct2Price);
    order2.setCardId(testCardObjectId);
    order2.setOrderId(testOrderObjectId);
    Order order3 = new Order();
    order3.setProductId(testProduct3ObjectId);
    order3.setUsed(false);
    order3.setCreatedAt(TEST_DATE);
    order3.setShopId(testShopObjectId);
    order3.setPrice(testPromotion1NewPrice);
    order3.setCardId(testCardObjectId);
    order3.setOrderId(testOrderObjectId);
    Order order4 = new Order();
    order4.setProductId(testProduct4ObjectId);
    order4.setUsed(false);
    order4.setCreatedAt(TEST_DATE);
    order4.setShopId(testShopObjectId);
    order4.setPrice(testPromotion3NewPrice);
    order4.setCardId(testCardObjectId);
    order4.setOrderId(testOrderObjectId);
    Order order5 = new Order();
    order5.setProductId(testProduct1ObjectId);
    order5.setUsed(false);
    order5.setCreatedAt(TEST_DATE);
    order5.setShopId(testShopObjectId);
    order5.setPrice(testProduct5Price);
    order5.setCardId(testCardObjectId);
    order5.setOrderId(testOrderObjectId);
    Account account = new Account();
    account.setId(TEST_OWNER_ID);
    User user = new User();
    user.setPoints(testUserPoints);
    user.setId(TEST_OWNER_ID);
    User expectedUser = new User();
    expectedUser.setPoints(testUserPoints - testPoints);
    expectedUser.setId(TEST_OWNER_ID);

    when(productRepository.findShopIdById(any(ObjectId.class))).thenReturn(product);
    when(promotionRepository.findByProductId(testProduct3ObjectId)).thenReturn(promotion1);
    when(promotionRepository.findByProductId(testProduct1ObjectId)).thenReturn(promotion2);
    when(promotionRepository.findByProductId(testProduct4ObjectId)).thenReturn(promotion3);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_OWNER_ID)).thenReturn(Optional.of(user));

    assertTrue(
        productService.buyProducts(productPayUS, testCardId, testOrderId, testPoints, TEST_PHONE));
    verify(mongoTemplate).remove(promotion2);
    verify(mongoTemplate).save(expectedPromotion3);
    verify(mongoTemplate, times(testProduct1Quantity)).save(order1);
    verify(mongoTemplate, times(testProduct2Quantity)).save(order2);
    verify(mongoTemplate, times(testProduct3Quantity)).save(order3);
    verify(mongoTemplate, times(testProduct4Quantity)).save(order4);
    verify(mongoTemplate, times(testProduct5Quantity)).save(order5);
    verify(reservedProductService).deleteAllByOrderId(testOrderId);
    verify(mongoTemplate).save(expectedUser);
  }

  private String setNameAtBuyProducts(String id, boolean hasPromotion) {
    return """
            {
              "productId": \""""
        + id
        + "\","
        + """
              "hasPromotion": """
        + hasPromotion
        + """
            }
            """;
  }

  @Test
  public void shouldReturnFalseAtBuyProductsWhenGetException() {
    ProductPayU productPayU = new ProductPayU();
    productPayU.setName("{}");

    assertFalse(productService.buyProducts(List.of(productPayU), null, null, null, null));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenEverythingOk() {
    final String testCardId = "123451234509876543217777";
    final int testMaxPage = 4;
    List<ProductAtCardDTO> products = new ArrayList<>();
    ProductAtCardDTO product1 = new ProductAtCardDTO();
    product1.setProductName(TEST_PRODUCT_NAME);
    ProductAtCardDTO product2 = new ProductAtCardDTO();
    product2.setProductName(TEST_PRODUCT_NAME);
    products.add(product1);
    products.add(product2);
    PageOwnerProductsDTO pageOwnerProductsDTO = new PageOwnerProductsDTO();
    pageOwnerProductsDTO.setProducts(products);
    pageOwnerProductsDTO.setMaxPage(testMaxPage);

    when(aggregationRepository.getProductsByOwnerCard(0, "", true, "", "", "", testCardId))
        .thenReturn(pageOwnerProductsDTO);

    assertEquals(
        pageOwnerProductsDTO,
        productService.getProductsByOwnerCard(0, "", true, "", "", "", testCardId));
  }

  @Test
  public void shouldListOfProductOrderDTOAtGetProductsAtCardWhenEverythingOk() {
    final ObjectId testAccountId = new ObjectId("923456781009875643211237");
    Account account = new Account();
    account.setId(testAccountId);
    List<ProductOrderDTO> productOrderDTOS = new ArrayList<>();
    ProductOrderDTO product1 = new ProductOrderDTO();
    product1.setBarcode(TEST_BARCODE);
    product1.setName(TEST_PRODUCT_NAME);
    product1.setImageUrl(TEST_URL);
    product1.setId(TEST_ID1);
    ProductOrderDTO product2 = new ProductOrderDTO();
    product2.setBarcode(TEST_BARCODE + "5");
    product2.setName(TEST_PRODUCT_NAME + "A");
    product2.setImageUrl(TEST_URL + "C");
    product2.setId(TEST_ID2);
    productOrderDTOS.add(product1);
    productOrderDTOS.add(product2);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(orderRepository.getProductsAtCard(testAccountId, new ObjectId(TEST_ID)))
        .thenReturn(productOrderDTOS);

    assertEquals(productOrderDTOS, productService.getProductsAtCard(TEST_PHONE, TEST_ID));
  }

  @Test
  public void shouldReturnListOfProductPayUAtGetProductsPayUWhenEverythingOk()
      throws JsonProcessingException {
    final ObjectId testProductId1 = new ObjectId("523467890123456879054231");
    final ObjectId testProductId2 = new ObjectId("723867890124456879054231");
    final int testQuantity1 = 2;
    final int testQuantity2 = 5;
    final int testPrice1 = 54324;
    final int testPrice2 = 134;
    ObjectMapper objectMapper = new ObjectMapper();
    Map<ProductInfo, Integer> products = new HashMap<>();
    ProductInfo productInfo1 = new ProductInfo();
    productInfo1.setProductId(testProductId1);
    productInfo1.setHasPromotion(false);
    ProductInfo productInfo2 = new ProductInfo();
    productInfo2.setProductId(testProductId2);
    productInfo2.setHasPromotion(true);
    products.put(productInfo1, testQuantity1);
    products.put(productInfo2, testQuantity2);
    List<ProductPayU> expectedProducts = new ArrayList<>();
    Map<String, Object> name1 = new HashMap<>(2);
    name1.put("productId", testProductId1.toHexString());
    name1.put("hasPromotion", false);
    ProductPayU productPayU1 = new ProductPayU();
    productPayU1.setUnitPrice(testPrice1);
    productPayU1.setQuantity(testQuantity1);
    productPayU1.setName(objectMapper.writeValueAsString(name1));
    Map<String, Object> name2 = new HashMap<>(2);
    name2.put("productId", testProductId2.toHexString());
    name2.put("hasPromotion", true);
    ProductPayU productPayU2 = new ProductPayU();
    productPayU2.setUnitPrice(testPrice2);
    productPayU2.setQuantity(testQuantity2);
    productPayU2.setName(objectMapper.writeValueAsString(name2));
    expectedProducts.add(productPayU2);
    expectedProducts.add(productPayU1);
    Product product = new Product();
    product.setPrice(testPrice1);
    Promotion promotion = new Promotion();
    promotion.setNewPrice(testPrice2);

    when(productRepository.findPriceById(testProductId1)).thenReturn(product);
    when(promotionRepository.findNewPriceByProductId(testProductId2)).thenReturn(promotion);

    assertEquals(expectedProducts, productService.getProductsPayU(products));
  }

  @Test
  public void shouldReturnTrueAtDeleteCategoryAndProductsWhenEverythingOk() throws IOException {
    final ObjectId cardId1 = new ObjectId("103586189012345678101240");
    final ObjectId cardId2 = new ObjectId("203586189012345678101240");
    final ObjectId cardId3 = new ObjectId("303586189012345678101240");
    final int amount1 = 500;
    final int amount2 = 120;
    final int amount3 = 4350;
    final float centsPerZloty = 100;
    final String cardIdField = "cardId";
    final String pointsField = "points";
    Card card1 = new Card();
    card1.setId(cardId1);
    Card card2 = new Card();
    card2.setId(cardId2);
    Card card3 = new Card();
    card3.setId(cardId3);
    Order order1 = new Order();
    order1.setUsed(false);
    order1.setProductId(TEST_PRODUCT_ID);
    order1.setCardId(cardId1);
    order1.setPrice(amount1);
    Order order2 = new Order();
    order2.setUsed(false);
    order2.setProductId(TEST_PRODUCT_ID);
    order2.setCardId(cardId2);
    order2.setPrice(amount2);
    Order order3 = new Order();
    order3.setUsed(false);
    order3.setProductId(TEST_PRODUCT_ID);
    order3.setCardId(cardId3);
    order3.setPrice(amount3);
    List<Order> orders = List.of(order1, order2, order3);

    when(productRepository.getProductsIdByCategoryId(new ObjectId(TEST_ID1)))
        .thenReturn(List.of(TEST_PRODUCT_ID));
    when(orderRepository.findAllByProductIdAndIsUsed(TEST_PRODUCT_ID, false)).thenReturn(orders);

    assertTrue(productService.deleteCategoryAndProducts(TEST_ID1));
    verify(categoryRepository).deleteById(new ObjectId(TEST_ID1));
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdField).is(cardId1)),
            new Update().inc(pointsField, (Math.round(amount1 / centsPerZloty))),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdField).is(cardId2)),
            new Update().inc(pointsField, (Math.round(amount2 / centsPerZloty))),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdField).is(cardId3)),
            new Update().inc(pointsField, (Math.round(amount3 / centsPerZloty))),
            User.class);
    verify(mongoTemplate).remove(order1);
    verify(mongoTemplate).remove(order2);
    verify(mongoTemplate).remove(order3);
    verify(cloudinaryService).deleteImage(TEST_ID);
    verify(promotionService).deletePromotion(TEST_ID);
    verify(productRepository).deleteById(TEST_PRODUCT_ID);
    verify(blockedProductRepository).deleteByProductId(TEST_PRODUCT_ID);
    verify(reportRepository).deleteAllByReportedId(TEST_PRODUCT_ID);
  }

  @Test
  public void shouldReturnFalseAtDeleteCategoryAndProductsWhenGetException() throws IOException {
    doThrow(RuntimeException.class).when(categoryRepository).deleteById(new ObjectId(TEST_ID1));

    assertFalse(productService.deleteCategoryAndProducts(TEST_ID1));
    verify(categoryRepository).deleteById(new ObjectId(TEST_ID1));
    verifyNoInteractions(productRepository);
  }

  @Test
  public void shouldReturnFalseAtDeleteCategoryAndProductsWhenErrorAtDeleteProducts()
      throws IOException {
    doThrow(RuntimeException.class).when(cloudinaryService).deleteImage(TEST_ID);

    when(productRepository.getProductsIdByCategoryId(new ObjectId(TEST_ID1)))
        .thenReturn(List.of(TEST_PRODUCT_ID));

    assertFalse(productService.deleteCategoryAndProducts(TEST_ID1));
    verify(categoryRepository).deleteById(new ObjectId(TEST_ID1));
  }

  @Test
  public void shouldReturnProductWithShopDTOAtGetProductWithShopDTOByIdWhenEverythingOk() {
    final String testDescription = "testDescription";
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
    ProductWithShopDTO productWithShopDTO = new ProductWithShopDTO();
    productWithShopDTO.setShopName(TEST_SHOP_NAME);
    productWithShopDTO.setShopId(TEST_OWNER_ID);
    productWithShopDTO.setShopImageUrl(TEST_SHOP_IMAGE_URL);
    productWithShopDTO.setActive(true);
    productWithShopDTO.setDescription(testDescription);
    productWithShopDTO.setProductId(TEST_PRODUCT_ID.toString());
    productWithShopDTO.setProductName(TEST_PRODUCT_NAME);
    productWithShopDTO.setProductImageUrl(TEST_URL);
    productWithShopDTO.setPrice(testPrice);
    productWithShopDTO.setQuantityPromotion(testQuantity);
    productWithShopDTO.setNewPricePromotion(testNewPrice);
    productWithShopDTO.setStartAtPromotion(testStartAt);
    productWithShopDTO.setExpiredAtPromotion(testExpiredAt);

    when(productRepository.getProductWithShopDTOById(TEST_PRODUCT_ID))
        .thenReturn(productWithShopDTO);

    assertEquals(productWithShopDTO, productService.getProductWithShopDTOById(TEST_ID));
  }
}
