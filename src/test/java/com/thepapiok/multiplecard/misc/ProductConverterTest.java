package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import org.junit.jupiter.api.Test;

public class ProductConverterTest {

  private final ProductConverter productConverter = new ProductConverter();

  @Test
  public void shouldReturnProductEntityAtGetEntityWhenEverythingOk() {
    final int cents = 1345;
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName("name");
    addProductDTO.setBarcode("barcode");
    addProductDTO.setAmount("13.45");
    addProductDTO.setDescription("description");
    Product expectedProduct = new Product();
    expectedProduct.setName(addProductDTO.getName());
    expectedProduct.setBarcode(addProductDTO.getBarcode());
    expectedProduct.setAmount(cents);
    expectedProduct.setDescription(addProductDTO.getDescription());

    assertEquals(expectedProduct, productConverter.getEntity(addProductDTO));
  }
}
