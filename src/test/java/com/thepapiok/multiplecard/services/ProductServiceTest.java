package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.ProductGetDTO;
import com.thepapiok.multiplecard.misc.ProductConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mock.web.MockMultipartFile;

public class ProductServiceTest {

  private static final String TEST_PRODUCT_NAME = "name";
  private static final String TEST_BARCODE = "12345678901234567890123";
  private static final ObjectId TEST_OWNER_ID = new ObjectId("123451789012345678901234");
  private static final String TEST_PHONE = "+482314123412341423";
  @Mock private CategoryService categoryService;
  @Mock private ProductConverter productConverter;
  @Mock private CloudinaryService cloudinaryService;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private ProductRepository productRepository;
  @Mock private AccountRepository accountRepository;
  @Mock private AggregationRepository aggregationRepository;
  private ProductService productService;

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
            aggregationRepository);
  }

  @Test
  public void shouldReturnTrueAtAddProductWhenEverythingOk() throws IOException {
    final int cents = 1123;
    final String firstCategoryName = "category1";
    final String secondCategoryName = "category2";
    final String imageUrl = "url";
    final ObjectId categoryId = new ObjectId("123456789012345678901234");
    final ObjectId categoryOtherId = new ObjectId("223456789012345678901235");
    final ObjectId ownerId = new ObjectId("123456789012345678901235");
    final ObjectId productId = new ObjectId("123456789012345678901211");
    List<String> categoryNames = List.of(firstCategoryName, secondCategoryName);
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
    category.setName(secondCategoryName);
    Category expectedCategory = new Category();
    expectedCategory.setOwnerId(ownerId);
    expectedCategory.setName(secondCategoryName);
    expectedCategory.setId(categoryOtherId);
    Product productWithCategories = new Product();
    productWithCategories.setName(addProductDTO.getName());
    productWithCategories.setAmount(cents);
    productWithCategories.setBarcode(addProductDTO.getBarcode());
    productWithCategories.setDescription(addProductDTO.getDescription());
    productWithCategories.setCategories(categories);
    productWithCategories.setActive(true);
    productWithCategories.setShopId(ownerId);
    productWithCategories.setImageUrl("");
    Product productWithId = new Product();
    productWithId.setName(addProductDTO.getName());
    productWithId.setAmount(cents);
    productWithId.setBarcode(addProductDTO.getBarcode());
    productWithId.setDescription(addProductDTO.getDescription());
    productWithId.setCategories(categories);
    productWithId.setId(productId);
    productWithId.setActive(true);
    productWithId.setShopId(ownerId);
    productWithId.setImageUrl("");
    Product expectedProduct = new Product();
    expectedProduct.setName(addProductDTO.getName());
    expectedProduct.setAmount(cents);
    expectedProduct.setBarcode(addProductDTO.getBarcode());
    expectedProduct.setDescription(addProductDTO.getDescription());
    expectedProduct.setCategories(categories);
    expectedProduct.setId(productId);
    expectedProduct.setImageUrl(imageUrl);
    expectedProduct.setId(productId);
    expectedProduct.setActive(true);
    expectedProduct.setShopId(ownerId);
    expectedProduct.setImageUrl("");

    when(productConverter.getEntity(addProductDTO)).thenReturn(productAfterConverter);
    when(categoryService.getCategoryIdByName(firstCategoryName)).thenReturn(categoryId);
    when(categoryService.getCategoryIdByName(secondCategoryName)).thenReturn(null);
    when(mongoTemplate.save(category)).thenReturn(expectedCategory);
    when(mongoTemplate.save(productWithCategories)).thenReturn(productWithId);
    when(cloudinaryService.addImage(addProductDTO.getFile().getBytes(), productId.toHexString()))
        .thenReturn(imageUrl);

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
  public void shouldReturnListOfProductGetDTOAtGetProductsOwner() {
    final ObjectId productId1 = new ObjectId("123456789012345678904312");
    final ObjectId productId2 = new ObjectId("023456589012345178904387");
    final int testAmountProduct1 = 10;
    final int testAmountProduct2 = 222;
    final int testAmountPromotion1 = 5;
    final int testYearStartAtPromotion1 = 2024;
    final int testMonthStartAtPromotion1 = 1;
    final int testDayStartAtPromotion1 = 1;
    final int testYearExpiredAtPromotion1 = 2015;
    final int testMonthExpiredAtPromotion1 = 2;
    final int testDayExpiredAtPromotion1 = 3;
    final String countField = "count";
    List<ObjectId> categories = List.of(new ObjectId("153456489019345178004311"));
    Product product1 = new Product();
    product1.setActive(true);
    product1.setImageUrl("url1");
    product1.setBarcode("barcode1");
    product1.setDescription("description1");
    product1.setName("name1");
    product1.setShopId(TEST_OWNER_ID);
    product1.setId(productId1);
    product1.setCategories(categories);
    product1.setAmount(testAmountProduct1);
    Product product2 = new Product();
    product2.setActive(true);
    product2.setAmount(testAmountProduct2);
    product2.setDescription("description2");
    product2.setBarcode("barcode2");
    product2.setShopId(TEST_OWNER_ID);
    product2.setImageUrl("url2");
    product2.setName("name2");
    product2.setCategories(categories);
    product2.setId(productId2);
    Promotion promotion1 = new Promotion();
    promotion1.setId(new ObjectId("923426389512345172904181"));
    promotion1.setAmount(testAmountPromotion1);
    promotion1.setProductId(productId1);
    promotion1.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion1, testMonthStartAtPromotion1, testDayStartAtPromotion1));
    promotion1.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion1, testMonthExpiredAtPromotion1, testDayExpiredAtPromotion1));
    ProductGetDTO productGetDTO1 = new ProductGetDTO();
    productGetDTO1.setProduct(product1);
    productGetDTO1.setPromotion(promotion1);
    ProductGetDTO productGetDTO2 = new ProductGetDTO();
    productGetDTO2.setProduct(product2);
    productGetDTO2.setPromotion(null);
    List<ProductGetDTO> expectedProducts = List.of(productGetDTO1, productGetDTO2);

    when(aggregationRepository.getProductsOwner(TEST_PHONE, 1, countField, true, ""))
        .thenReturn(expectedProducts);

    assertEquals(
        expectedProducts, productService.getProductsOwner(TEST_PHONE, 1, countField, true, ""));
  }

  @Test
  public void shouldReturn12AtGetMaxPage() {
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
    final ObjectId otherOwnerId = new ObjectId("098765432112345678904321");
    Account account = new Account();
    account.setId(TEST_OWNER_ID);
    Product product = new Product();
    product.setShopId(otherOwnerId);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.findShopIdById(otherOwnerId)).thenReturn(product);

    assertFalse(productService.isProductOwner(TEST_PHONE, otherOwnerId.toString()));
  }

  @Test
  public void shouldReturnFalseAtIsProductOwnerWhenProductNotFound() {
    final ObjectId otherOwnerId = new ObjectId("098765439912345678904329");
    Account account = new Account();
    account.setId(TEST_OWNER_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.findShopIdById(otherOwnerId)).thenReturn(null);

    assertFalse(productService.isProductOwner(TEST_PHONE, otherOwnerId.toString()));
  }
}
