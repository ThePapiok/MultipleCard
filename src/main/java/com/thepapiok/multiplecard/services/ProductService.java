package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.ProductGetDTO;
import com.thepapiok.multiplecard.misc.ProductConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
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
  private static final String COUNT_FIELD = "count";
  private static final String ID_FIELD = "_id";
  private static final String SHOP_ID_FIELD = "shopId";
  private static final String PRODUCTS_COLLECTION = "products";
  private static final String TEXT_OPERATOR = "$text";
  private static final String SEARCH_OPERATOR = "$search";
  private final CategoryService categoryService;
  private final ProductConverter productConverter;
  private final ProductRepository productRepository;
  private final CloudinaryService cloudinaryService;
  private final AccountRepository accountRepository;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;
  private final AggregationRepository aggregationRepository;

  @Autowired
  public ProductService(
      CategoryService categoryService,
      ProductConverter productConverter,
      ProductRepository productRepository,
      CloudinaryService cloudinaryService,
      AccountRepository accountRepository,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate,
      AggregationRepository aggregationRepository) {
    this.categoryService = categoryService;
    this.productConverter = productConverter;
    this.productRepository = productRepository;
    this.cloudinaryService = cloudinaryService;
    this.accountRepository = accountRepository;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
    this.aggregationRepository = aggregationRepository;
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
              product.setShopId(ownerId);
              product.setImageUrl("");
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

  public List<ProductGetDTO> getProductsOwner(
      String phone, int page, String field, boolean isDescending, String text) {
    return aggregationRepository.getProductsOwner(phone, page, field, isDescending, text);
  }

  public int getMaxPage(String text, String phone) {
    return aggregationRepository.getMaxPage(text, phone);
  }
}
