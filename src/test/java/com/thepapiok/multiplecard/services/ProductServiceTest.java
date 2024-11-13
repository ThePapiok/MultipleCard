package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Blocked;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.misc.ProductConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.BlockedRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
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
  private static final ObjectId TEST_PRODUCT1_ID = new ObjectId(TEST_ID1);
  private static final ObjectId TEST_PRODUCT2_ID = new ObjectId(TEST_ID2);
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
  private static Category testCategory;
  private static Category testExpectedCategory;
  private static List<String> testNameOfCategories;
  private static Product testProduct;
  private static Product testExpectedProduct;
  private static EditProductDTO testEditProductDTO;
  private ProductService productService;

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
  @Mock private BlockedRepository blockedRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private ShopRepository shopRepository;

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
            blockedRepository,
            categoryRepository,
            shopRepository);
  }

  @Test
  public void shouldReturnTrueAtAddProductWhenEverythingOk() throws IOException {
    final int cents = 1123;
    final ObjectId categoryId = new ObjectId("123456789012345678901234");
    final ObjectId categoryOtherId = new ObjectId("223456789012345678901235");
    final ObjectId ownerId = new ObjectId("123456789012345678901235");
    final ObjectId productId = new ObjectId("123456789012345678901211");

    MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_DATE);
    List<String> categoryNames = List.of(TEST_CATEGORY_NAME1, TEST_CATEGORY_NAME2);
    List<ObjectId> categories = List.of(categoryId, categoryOtherId);
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName(TEST_PRODUCT_NAME);
    addProductDTO.setCategory(categoryNames);
    addProductDTO.setBarcode("barcode");
    addProductDTO.setAmount("11.23");
    addProductDTO.setDescription("description");
    addProductDTO.setFile(new MockMultipartFile("file", new byte[0]));
    Product productAfterConverter = new Product();
    productAfterConverter.setName(addProductDTO.getName());
    productAfterConverter.setAmount(cents);
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
    productWithCategories.setAmount(cents);
    productWithCategories.setBarcode(addProductDTO.getBarcode());
    productWithCategories.setDescription(addProductDTO.getDescription());
    productWithCategories.setCategories(categories);
    productWithCategories.setShopId(ownerId);
    productWithCategories.setImageUrl("");
    productWithCategories.setUpdatedAt(TEST_DATE);
    Product productWithId = new Product();
    productWithId.setName(addProductDTO.getName());
    productWithId.setAmount(cents);
    productWithId.setBarcode(addProductDTO.getBarcode());
    productWithId.setDescription(addProductDTO.getDescription());
    productWithId.setCategories(categories);
    productWithId.setId(productId);
    productWithId.setShopId(ownerId);
    productWithId.setImageUrl("");
    productWithId.setUpdatedAt(TEST_DATE);
    Product expectedProduct = new Product();
    expectedProduct.setName(addProductDTO.getName());
    expectedProduct.setAmount(cents);
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
    localDateTimeMockedStatic.close();
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
  public void shouldReturn12AtGetMaxPageWhenEverythingOk() {
    final int maxPage = 12;

    when(aggregationRepository.getMaxPage("", TEST_PHONE)).thenReturn(maxPage);

    assertEquals(maxPage, productService.getMaxPage("", TEST_PHONE));
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
    product.setAmount(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertTrue(productService.isLessThanOriginalPrice("20.00", TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtIsLessThanOriginalPriceWhenIsMore() {
    final int amount = 3000;
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setAmount(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertFalse(productService.isLessThanOriginalPrice("40.00", TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtIsLessThanOriginalPriceWhenProductIsNotFound() {
    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    assertFalse(productService.isLessThanOriginalPrice("35.00", TEST_ID));
  }

  @Test
  public void shouldReturn35dot91AtGetAmountWhenEverythingOk() {
    final int amount = 3591;
    final double amountWithoutCents = 35.91;
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setAmount(amount);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertEquals(amountWithoutCents, productService.getAmount(TEST_ID));
  }

  @Test
  public void shouldReturnNull1AtGetAmountWhenProductNotFound() {
    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    assertNull(productService.getAmount(TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtDeleteProductWhenEverythingOk() throws IOException {
    final ObjectId productId = new ObjectId(TEST_ID);
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
    order1.setProductId(productId);
    order1.setCardId(cardId1);
    order1.setAmount(amount1);
    Order order2 = new Order();
    order2.setUsed(false);
    order2.setProductId(productId);
    order2.setCardId(cardId2);
    order2.setAmount(amount2);
    Order order3 = new Order();
    order3.setUsed(false);
    order3.setProductId(productId);
    order3.setCardId(cardId3);
    order3.setAmount(amount3);
    Order order1AfterDelete = new Order();
    order1AfterDelete.setUsed(true);
    order1AfterDelete.setProductId(productId);
    order1AfterDelete.setCardId(cardId1);
    order1AfterDelete.setAmount(amount1);
    Order order2AfterDelete = new Order();
    order2AfterDelete.setUsed(true);
    order2AfterDelete.setProductId(productId);
    order2AfterDelete.setCardId(cardId2);
    order2AfterDelete.setAmount(amount2);
    Order order3AfterDelete = new Order();
    order3AfterDelete.setUsed(true);
    order3AfterDelete.setProductId(productId);
    order3AfterDelete.setCardId(cardId3);
    order3AfterDelete.setAmount(amount3);
    List<Order> orders = List.of(order1, order2, order3);

    when(orderRepository.findAllByProductIdAndIsUsed(productId, false)).thenReturn(orders);

    assertTrue(productService.deleteProduct(TEST_ID));
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
    verify(mongoTemplate).save(order1AfterDelete);
    verify(mongoTemplate).save(order2AfterDelete);
    verify(mongoTemplate).save(order3AfterDelete);
    verify(cloudinaryService).deleteImage(TEST_ID);
    verify(promotionService).deletePromotion(TEST_ID);
    verify(productRepository).deleteById(productId);
    verify(blockedRepository).deleteByProductId(productId);
  }

  @Test
  public void shouldReturnFalseAtDeleteProductWhenGetException() throws IOException {
    doThrow(RuntimeException.class).when(cloudinaryService).deleteImage(TEST_ID);

    assertFalse(productService.deleteProduct(TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtHasBlockWhenProductIsBlocked() {
    when(blockedRepository.existsByProductId(TEST_PRODUCT_ID)).thenReturn(true);

    assertTrue(productService.hasBlock(TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtHasBlockWhenProductIsNotBlocked() {
    when(blockedRepository.existsByProductId(TEST_PRODUCT_ID)).thenReturn(false);

    assertFalse(productService.hasBlock(TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtBlockProductWhenEverythingOk() {
    final int month = 30;
    Blocked expectedBlocked = new Blocked();
    expectedBlocked.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlocked.setProductId(TEST_PRODUCT_ID);

    assertTrue(productService.blockProduct(TEST_ID));
    verify(blockedRepository).save(expectedBlocked);
  }

  @Test
  public void shouldReturnFalseAtBlockProductWhenGetException() {
    final int month = 30;
    Blocked expectedBlocked = new Blocked();
    expectedBlocked.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlocked.setProductId(TEST_PRODUCT_ID);

    when(blockedRepository.save(expectedBlocked)).thenThrow(MongoWriteException.class);

    assertFalse(productService.blockProduct(TEST_ID));
    verify(blockedRepository).save(expectedBlocked);
  }

  @Test
  public void shouldReturnTrueAtUnblockProductWhenEverythingOk() {
    final int month = 30;
    Blocked expectedBlocked = new Blocked();
    expectedBlocked.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlocked.setProductId(TEST_PRODUCT_ID);

    when(blockedRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedBlocked);

    assertTrue(productService.unblockProduct(TEST_ID));
    verify(blockedRepository).delete(expectedBlocked);
  }

  @Test
  public void shouldReturnFalseAtUnblockProductWhenGetException() {
    final int month = 30;
    Blocked expectedBlocked = new Blocked();
    expectedBlocked.setExpiredAt(LocalDate.now().plusDays(month));
    expectedBlocked.setProductId(TEST_PRODUCT_ID);

    when(blockedRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedBlocked);
    doThrow(MongoWriteException.class).when(blockedRepository).delete(expectedBlocked);

    assertFalse(productService.unblockProduct(TEST_ID));
    verify(blockedRepository).delete(expectedBlocked);
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
    MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_DATE);
    setDataForEditProduct();

    assertTrue(productService.editProduct(testEditProductDTO, TEST_OWNER_ID, testNameOfCategories));
    verify(mongoTemplate).save(testExpectedProduct);
    localDateTimeMockedStatic.close();
  }

  @Test
  public void shouldReturnTrueAtEditProductWhenHasFile() {
    setDataForEditProduct();
    final String testNewUrl = "newUrl";
    final byte[] bytes = new byte[0];
    final MultipartFile multipartFile = new MockMultipartFile("file1", bytes);
    MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_DATE);
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
    localDateTimeMockedStatic.close();
  }

  @Test
  public void shouldReturnFalseAtEditProductWhenGetException() {
    setDataForEditProduct();
    MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_DATE);

    when(mongoTemplate.save(testProduct)).thenThrow(MongoWriteException.class);

    assertFalse(
        productService.editProduct(testEditProductDTO, TEST_OWNER_ID, testNameOfCategories));
    localDateTimeMockedStatic.close();
  }

  private void setDataForEditProduct() {
    testCategory = new Category();
    testCategory.setName(TEST_CATEGORY_NAME3);
    testCategory.setOwnerId(TEST_OWNER_ID);
    testExpectedCategory = new Category();
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
  public void shouldReturnListOfProductDTOAtGetProductsWhenEverythingOk() {
    List<ProductDTO> expectedProducts = setDataProductsDTO();

    when(aggregationRepository.getProducts(TEST_PHONE, 1, COUNT_FILED, true, ""))
        .thenReturn(expectedProducts);

    assertEquals(
        expectedProducts, productService.getProducts(TEST_PHONE, 1, COUNT_FILED, true, ""));
  }

  @Test
  public void shouldReturnListOfProductWithShopDTOAtGetProductsByIdsWhenEverythingOk() {
    List<ProductDTO> productDTOS = setDataProductsDTO();
    ProductWithShopDTO product1 =
        new ProductWithShopDTO(productDTOS.get(0), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);
    ProductWithShopDTO product2 =
        new ProductWithShopDTO(productDTOS.get(1), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);

    when(productRepository.findProductsByIds(List.of(TEST_PRODUCT1_ID, TEST_PRODUCT2_ID), 0))
        .thenReturn(List.of(product1, product2));

    assertEquals(
        List.of(product1, product2),
        productService.getProductsByIds(List.of(TEST_ID1, TEST_ID2), 0));
  }

  @Test
  public void shouldReturnListOfProductWithShopDTOAtGetProductsWithShopsWhenEverythingOk() {
    List<ProductDTO> productDTOS = setDataProductsDTO();
    ProductWithShopDTO product1 =
        new ProductWithShopDTO(productDTOS.get(0), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);
    ProductWithShopDTO product2 =
        new ProductWithShopDTO(productDTOS.get(1), TEST_SHOP_NAME, TEST_SHOP_IMAGE_URL);
    Shop shop = new Shop();
    shop.setName(TEST_SHOP_NAME);
    shop.setImageUrl(TEST_SHOP_IMAGE_URL);

    when(aggregationRepository.getProducts(null, 1, COUNT_FILED, true, "")).thenReturn(productDTOS);
    when(shopRepository.findImageUrlAndNameById(TEST_OWNER_ID)).thenReturn(shop);

    assertEquals(
        List.of(product1, product2), productService.getProductsWithShops(1, COUNT_FILED, true, ""));
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
    productDTO1.setBarcode("barcode1");
    productDTO1.setAmount(testAmountProduct1);
    productDTO1.setDescription("description1");
    productDTO1.setProductImageUrl("url1");
    productDTO1.setShopId(TEST_OWNER_ID);
    productDTO1.setStartAtPromotion(
        LocalDate.of(
            testYearStartAtPromotion1, testMonthStartAtPromotion1, testDayStartAtPromotion1));
    productDTO1.setExpiredAtPromotion(
        LocalDate.of(
            testYearExpiredAtPromotion1, testMonthExpiredAtPromotion1, testDayExpiredAtPromotion1));
    productDTO1.setAmountPromotion(testAmountPromotion1);
    productDTO1.setCountPromotion(0);
    ProductDTO productDTO2 = new ProductDTO();
    productDTO2.setActive(false);
    productDTO2.setProductId(TEST_ID2);
    productDTO2.setProductName("name2");
    productDTO2.setBarcode("barcode2");
    productDTO2.setAmount(testAmountProduct2);
    productDTO2.setDescription("description2");
    productDTO2.setProductImageUrl("url2");
    productDTO2.setShopId(TEST_OWNER_ID);
    productDTO2.setStartAtPromotion(null);
    productDTO2.setExpiredAtPromotion(null);
    productDTO2.setAmountPromotion(0);
    productDTO2.setCountPromotion(0);
    return List.of(productDTO1, productDTO2);
  }
}
