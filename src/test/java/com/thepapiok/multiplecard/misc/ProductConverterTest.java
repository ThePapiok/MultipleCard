package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductConverterTest {
  private static final String TEST_PRODUCT_NAME = "name";
  private static final String TEST_BARCODE = "barcode";
  private static final String TEST_DESCRIPTION = "description";
  private static final String TEST_ID = "123456789009876543215556";
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId(TEST_ID);
  private static final int TEST_CENTS = 1345;
  @Mock private ProductRepository productRepository;
  private ProductConverter productConverter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    productConverter = new ProductConverter(productRepository);
  }

  @Test
  public void shouldReturnProductEntityAddProductDTOAtGetEntityWhenEverythingOk() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName(TEST_PRODUCT_NAME);
    addProductDTO.setBarcode(TEST_BARCODE);
    addProductDTO.setPrice("13.45");
    addProductDTO.setDescription(TEST_DESCRIPTION);
    Product expectedProduct = new Product();
    expectedProduct.setName(addProductDTO.getName());
    expectedProduct.setBarcode(addProductDTO.getBarcode());
    expectedProduct.setPrice(TEST_CENTS);
    expectedProduct.setDescription(addProductDTO.getDescription());

    assertEquals(expectedProduct, productConverter.getEntity(addProductDTO));
  }

  @Test
  public void shouldReturnProductEntityEditProductDTOAtGetEntityWhenEverythingOk() {
    EditProductDTO editProductDTO = new EditProductDTO();
    editProductDTO.setName(TEST_PRODUCT_NAME);
    editProductDTO.setBarcode(TEST_BARCODE);
    editProductDTO.setPrice("13.45");
    editProductDTO.setDescription(TEST_DESCRIPTION);
    editProductDTO.setId(TEST_ID);
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    Product expectedProduct = new Product();
    expectedProduct.setId(TEST_PRODUCT_ID);
    expectedProduct.setName(editProductDTO.getName());
    expectedProduct.setBarcode(editProductDTO.getBarcode());
    expectedProduct.setPrice(TEST_CENTS);
    expectedProduct.setDescription(editProductDTO.getDescription());

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    assertEquals(expectedProduct, productConverter.getEntity(editProductDTO));
  }

  @Test
  public void shouldReturnNullAtGetEntityWhenProductNotFound() {
    EditProductDTO editProductDTO = new EditProductDTO();
    editProductDTO.setId(TEST_ID);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    assertNull(productConverter.getEntity(editProductDTO));
  }

  @Test
  public void shouldReturnEditProductDTOAtGetDTOWhenEverythingOk() {
    final String testUrl = "url";
    Product product = new Product();
    product.setImageUrl(testUrl);
    product.setName(TEST_PRODUCT_NAME);
    product.setPrice(TEST_CENTS);
    product.setDescription(TEST_DESCRIPTION);
    product.setBarcode(TEST_BARCODE);
    product.setId(TEST_PRODUCT_ID);
    EditProductDTO expectedEditProductDTO = new EditProductDTO();
    expectedEditProductDTO.setId(TEST_ID);
    expectedEditProductDTO.setDescription(TEST_DESCRIPTION);
    expectedEditProductDTO.setPrice(String.valueOf(TEST_CENTS));
    expectedEditProductDTO.setBarcode(TEST_BARCODE);
    expectedEditProductDTO.setImageUrl(testUrl);
    expectedEditProductDTO.setName(TEST_PRODUCT_NAME);

    assertEquals(expectedEditProductDTO, productConverter.getDTO(product));
  }
}
