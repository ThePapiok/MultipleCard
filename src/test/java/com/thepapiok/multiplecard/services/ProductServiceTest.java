package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.misc.ProductConverter;
import java.io.IOException;
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

  @Mock private CategoryService categoryService;
  @Mock private ProductConverter productConverter;
  @Mock private CloudinaryService cloudinaryService;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private MongoTemplate mongoTemplate;
  private ProductService productService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    productService =
        new ProductService(
            categoryService,
            productConverter,
            cloudinaryService,
            mongoTransactionManager,
            mongoTemplate);
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
    addProductDTO.setName("name");
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
    productWithCategories.setPromotion(0);
    productWithCategories.setShopId(ownerId);
    Product productWithId = new Product();
    productWithId.setName(addProductDTO.getName());
    productWithId.setAmount(cents);
    productWithId.setBarcode(addProductDTO.getBarcode());
    productWithId.setDescription(addProductDTO.getDescription());
    productWithId.setCategories(categories);
    productWithId.setId(productId);
    productWithId.setActive(true);
    productWithId.setPromotion(0);
    productWithId.setShopId(ownerId);
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
    expectedProduct.setPromotion(0);
    expectedProduct.setShopId(ownerId);

    when(productConverter.getEntity(addProductDTO)).thenReturn(productAfterConverter);
    when(categoryService.getCategoryIdByName(firstCategoryName)).thenReturn(categoryId);
    when(categoryService.getCategoryIdByName(secondCategoryName)).thenReturn(null);
    when(mongoTemplate.save(category)).thenReturn(expectedCategory);
    when(mongoTemplate.save(productWithCategories)).thenReturn(productWithId);
    when(cloudinaryService.addImage(addProductDTO.getFile().getBytes(), productId.toHexString()))
        .thenReturn(imageUrl);

    assertTrue(productService.addProduct(addProductDTO, ownerId, categoryNames));
    verify(mongoTemplate).save(expectedProduct);
  }

  @Test
  public void shouldReturnFalseAtAddProductWhenGetException() {
    AddProductDTO addProductDTO = new AddProductDTO();

    when(productConverter.getEntity(addProductDTO)).thenThrow(RuntimeException.class);

    assertFalse(
        productService.addProduct(
            addProductDTO, new ObjectId("123456789012345678904321"), List.of()));
  }
}
