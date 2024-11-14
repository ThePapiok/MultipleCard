package com.thepapiok.multiplecard.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.thepapiok.multiplecard.collections.Blocked;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {
  private final CategoryService categoryService;
  private final ProductConverter productConverter;
  private final ProductRepository productRepository;
  private final CloudinaryService cloudinaryService;
  private final AccountRepository accountRepository;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;
  private final AggregationRepository aggregationRepository;
  private final PromotionService promotionService;
  private final OrderRepository orderRepository;
  private final BlockedRepository blockedRepository;
  private final CategoryRepository categoryRepository;
  private final ShopRepository shopRepository;

  @Autowired
  public ProductService(
      CategoryService categoryService,
      ProductConverter productConverter,
      ProductRepository productRepository,
      CloudinaryService cloudinaryService,
      AccountRepository accountRepository,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate,
      AggregationRepository aggregationRepository,
      PromotionService promotionService,
      OrderRepository orderRepository,
      BlockedRepository blockedRepository,
      CategoryRepository categoryRepository,
      ShopRepository shopRepository) {
    this.categoryService = categoryService;
    this.productConverter = productConverter;
    this.productRepository = productRepository;
    this.cloudinaryService = cloudinaryService;
    this.accountRepository = accountRepository;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
    this.aggregationRepository = aggregationRepository;
    this.promotionService = promotionService;
    this.orderRepository = orderRepository;
    this.blockedRepository = blockedRepository;
    this.categoryRepository = categoryRepository;
    this.shopRepository = shopRepository;
  }

  public boolean addProduct(
      AddProductDTO addProductDTO, ObjectId ownerId, List<String> nameOfCategories) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              Product product = productConverter.getEntity(addProductDTO);
              product.setShopId(ownerId);
              product.setImageUrl("");
              product.setUpdatedAt(LocalDateTime.now());
              product.setCategories(setCategories(nameOfCategories, ownerId));
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

  public int getMaxPage(String text, String phone, String category, String shopName) {
    return aggregationRepository.getMaxPage(text, phone, category, shopName);
  }

  public boolean isProductOwner(String phone, String id) {
    Product product = productRepository.findShopIdById(new ObjectId(id));
    if (product == null) {
      return false;
    }
    return accountRepository.findIdByPhone(phone).getId().equals(product.getShopId());
  }

  public boolean isLessThanOriginalPrice(String amount, String productId) {
    final int centsPerZl = 100;
    final int amountCents = (int) (Double.parseDouble(amount) * centsPerZl);
    Optional<Product> product = productRepository.findById(new ObjectId(productId));
    if (product.isEmpty()) {
      return false;
    }
    return product.get().getAmount() > amountCents;
  }

  public Double getAmount(String productId) {
    final double centsPerZl = 100.0;
    Optional<Product> product = productRepository.findById(new ObjectId(productId));
    if (product.isEmpty()) {
      return null;
    }

    return (product.get().getAmount() / centsPerZl);
  }

  public boolean deleteProduct(String productId) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              try {
                final ObjectId objectId = new ObjectId(productId);
                final float centsPerZloty = 100;
                promotionService.deletePromotion(productId);
                productRepository.deleteById(objectId);
                blockedRepository.deleteByProductId(objectId);
                List<Order> orders = orderRepository.findAllByProductIdAndIsUsed(objectId, false);
                for (Order order : orders) {
                  mongoTemplate.updateFirst(
                      query(where("cardId").is(order.getCardId())),
                      new Update().inc("points", (Math.round(order.getAmount() / centsPerZloty))),
                      User.class);
                  order.setUsed(true);
                  mongoTemplate.save(order);
                }
                cloudinaryService.deleteImage(productId);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean hasBlock(String id) {
    return blockedRepository.existsByProductId(new ObjectId(id));
  }

  public boolean blockProduct(String id) {
    try {
      final int month = 30;
      Blocked blocked = new Blocked();
      blocked.setProductId(new ObjectId(id));
      blocked.setExpiredAt(LocalDate.now().plusDays(month));
      blockedRepository.save(blocked);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean unblockProduct(String id) {
    try {
      Blocked blocked = blockedRepository.findByProductId(new ObjectId(id));
      blockedRepository.delete(blocked);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Product getProductById(String id) {
    Optional<Product> optionalProduct = productRepository.findById(new ObjectId(id));
    if (optionalProduct.isEmpty()) {
      return null;
    }
    return optionalProduct.get();
  }

  public List<String> getCategoriesNames(Product product) {
    Optional<Category> optionalCategory;
    List<String> names = new ArrayList<>();
    for (ObjectId categoryId : product.getCategories()) {
      optionalCategory = categoryRepository.findById(categoryId);
      if (optionalCategory.isPresent()) {
        names.add(optionalCategory.get().getName());
      }
    }
    return names;
  }

  public EditProductDTO getEditProductDTO(Product product) {
    return productConverter.getDTO(product);
  }

  public boolean editProduct(
      EditProductDTO editProduct, ObjectId ownerId, List<String> nameOfCategories) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              final MultipartFile file = editProduct.getFile();
              Product product = productConverter.getEntity(editProduct);
              product.setCategories(setCategories(nameOfCategories, ownerId));
              product.setUpdatedAt(LocalDateTime.now());
              if (file != null) {
                try {
                  product.setImageUrl(
                      cloudinaryService.addImage(file.getBytes(), product.getId().toHexString()));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
              mongoTemplate.save(product);
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private List<ObjectId> setCategories(List<String> nameOfCategories, ObjectId ownerId) {
    List<ObjectId> categories = new ArrayList<>();
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
    return categories;
  }

  public List<ProductDTO> getProducts(
      String phone,
      int page,
      String field,
      boolean isDescending,
      String text,
      String category,
      String shopName) {
    return aggregationRepository.getProducts(
        phone, page, field, isDescending, text, category, shopName);
  }

  public List<ProductWithShopDTO> getProductsByIds(List<String> productsId, int page) {
    final int countProductsAtPage = 12;
    return productRepository.findProductsByIds(
        productsId.stream().map(ObjectId::new).toList(), page * countProductsAtPage);
  }

  public List<ProductWithShopDTO> getProductsWithShops(
      int page, String field, boolean isDescending, String text, String category, String shopName) {
    List<ProductDTO> productDTOS =
        getProducts(null, page, field, isDescending, text, category, shopName);
    List<ProductWithShopDTO> products = new ArrayList<>();
    for (ProductDTO productDTO : productDTOS) {
      Shop shop = shopRepository.findImageUrlAndNameById(productDTO.getShopId());
      products.add(new ProductWithShopDTO(productDTO, shop.getName(), shop.getImageUrl()));
    }
    return products;
  }
}
