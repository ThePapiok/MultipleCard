package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.misc.ProductConverter;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ProductService {
  private final CategoryService categoryService;
  private final ProductConverter productConverter;
  private final ProductRepository productRepository;
  private final CloudinaryService cloudinaryService;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;

  @Autowired
  public ProductService(
      CategoryService categoryService,
      ProductConverter productConverter,
      ProductRepository productRepository,
      CloudinaryService cloudinaryService,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate) {
    this.categoryService = categoryService;
    this.productConverter = productConverter;
    this.productRepository = productRepository;
    this.cloudinaryService = cloudinaryService;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
  }

  public boolean addProduct(
      AddProductDTO addProductDTO, ObjectId ownerId, List<String> nameOfCategories) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              List<ObjectId> categories = new ArrayList<>();
              Product product = productConverter.getEntity(addProductDTO);
              product.setActive(true);
              product.setPromotion(0);
              product.setShopId(ownerId);
              product.setImageUrl("");
              System.out.println(product);
              for (String name : nameOfCategories) {
                ObjectId id = categoryService.getCategoryIdByName(name);
                if (id == null) {
                  Category category = new Category();
                  category.setName(name);
                  category.setOwnerId(ownerId);
                  categories.add(mongoTemplate.save(category).getId());
                } else {
                  categories.add(id);
                }
              }
              product.setCategories(categories);
              product = mongoTemplate.save(product);
              try {
                System.out.println(product);
                product.setImageUrl(
                    cloudinaryService.addImage(
                        addProductDTO.getFile().getBytes(), product.getId().toHexString()));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              mongoTemplate.save(product);
            }
          });
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
    return true;
  }

  public boolean checkOwnerHasTheSameNameProduct(ObjectId ownerId, String name) {
    return productRepository.existsByNameAndShopId(name, ownerId);
  }

  public boolean checkOwnerHasTheSameBarcode(ObjectId ownerId, String barcode) {
    return productRepository.existsByBarcodeAndShopId(barcode, ownerId);
  }
}
