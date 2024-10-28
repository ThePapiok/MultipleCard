package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

  public Product getEntity(AddProductDTO addProductDTO) {
    final int centsPerZl = 100;
    Product product = new Product();
    product.setBarcode(addProductDTO.getBarcode());
    product.setName(addProductDTO.getName());
    product.setDescription(addProductDTO.getDescription());
    product.setAmount((int) (Double.parseDouble(addProductDTO.getAmount()) * centsPerZl));
    return product;
  }
}
