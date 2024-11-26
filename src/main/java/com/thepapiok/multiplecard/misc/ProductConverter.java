package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {
  private final ProductRepository productRepository;

  @Autowired
  public ProductConverter(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product getEntity(AddProductDTO addProductDTO) {
    final int centsPerZl = 100;
    Product product = new Product();
    product.setBarcode(addProductDTO.getBarcode());
    product.setName(addProductDTO.getName());
    product.setDescription(addProductDTO.getDescription());
    product.setPrice((int) (Double.parseDouble(addProductDTO.getPrice()) * centsPerZl));
    return product;
  }

  public Product getEntity(EditProductDTO editProductDTO) {
    final int centsPerZl = 100;
    Optional<Product> optionalProduct =
        productRepository.findById(new ObjectId(editProductDTO.getId()));
    if (optionalProduct.isEmpty()) {
      return null;
    }
    Product product = optionalProduct.get();
    product.setBarcode(editProductDTO.getBarcode());
    product.setName(editProductDTO.getName());
    product.setDescription(editProductDTO.getDescription());
    product.setPrice((int) (Double.parseDouble(editProductDTO.getPrice()) * centsPerZl));
    return product;
  }

  public EditProductDTO getDTO(Product product) {
    EditProductDTO editProductDTO = new EditProductDTO();
    editProductDTO.setPrice(String.valueOf(product.getPrice()));
    editProductDTO.setName(product.getName());
    editProductDTO.setDescription(product.getDescription());
    editProductDTO.setImageUrl(product.getImageUrl());
    editProductDTO.setBarcode(product.getBarcode());
    editProductDTO.setId(product.getId().toString());
    return editProductDTO;
  }
}
