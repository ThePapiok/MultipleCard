package com.thepapiok.multiplecard.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.collections.BlockedProduct;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import com.thepapiok.multiplecard.dto.PageOwnerProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsWithShopDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductOrderDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.misc.ProductConverter;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.misc.ProductPayU;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.BlockedProductRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import com.thepapiok.multiplecard.repositories.ReportRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
  private final BlockedProductRepository blockedProductRepository;
  private final CategoryRepository categoryRepository;
  private final ShopRepository shopRepository;
  private final PromotionRepository promotionRepository;
  private final ReservedProductService reservedProductService;
  private final UserRepository userRepository;
  private final ReportRepository reportRepository;

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
      BlockedProductRepository blockedProductRepository,
      CategoryRepository categoryRepository,
      ShopRepository shopRepository,
      PromotionRepository promotionRepository,
      ReservedProductService reservedProductService,
      UserRepository userRepository,
      ReportRepository reportRepository) {
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
    this.blockedProductRepository = blockedProductRepository;
    this.categoryRepository = categoryRepository;
    this.shopRepository = shopRepository;
    this.promotionRepository = promotionRepository;
    this.reservedProductService = reservedProductService;
    this.userRepository = userRepository;
    this.reportRepository = reportRepository;
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

  public boolean isProductOwner(String phone, String id) {
    Product product = productRepository.findShopIdById(new ObjectId(id));
    if (product == null) {
      return false;
    }
    return accountRepository.findIdByPhone(phone).getId().equals(product.getShopId());
  }

  public boolean isLessThanOriginalPrice(String price, String productId) {
    final int centsPerZl = 100;
    final int amountCents = (int) (Double.parseDouble(price) * centsPerZl);
    Optional<Product> product = productRepository.findById(new ObjectId(productId));
    if (product.isEmpty()) {
      return false;
    }
    return product.get().getPrice() > amountCents;
  }

  public String getPrice(String productId) {
    final double centsPerZl = 100.0;
    Optional<Product> product = productRepository.findById(new ObjectId(productId));
    if (product.isEmpty()) {
      return null;
    }
    return String.format(Locale.US, "%.2f", (product.get().getPrice() / centsPerZl));
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
                blockedProductRepository.deleteByProductId(objectId);
                reportRepository.deleteAllByReportedId(objectId);
                List<Order> orders = orderRepository.findAllByProductIdAndIsUsed(objectId, false);
                for (Order order : orders) {
                  mongoTemplate.updateFirst(
                      query(where("cardId").is(order.getCardId())),
                      new Update().inc("points", (Math.round(order.getPrice() / centsPerZloty))),
                      User.class);
                  mongoTemplate.remove(order);
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
    return blockedProductRepository.existsByProductId(new ObjectId(id));
  }

  public boolean blockProduct(String id) {
    try {
      final int month = 30;
      BlockedProduct blockedProduct = new BlockedProduct();
      blockedProduct.setProductId(new ObjectId(id));
      blockedProduct.setExpiredAt(LocalDate.now().plusDays(month));
      blockedProductRepository.save(blockedProduct);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean unblockProduct(String id) {
    try {
      BlockedProduct blockedProduct = blockedProductRepository.findByProductId(new ObjectId(id));
      blockedProductRepository.delete(blockedProduct);
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
              if (!file.isEmpty()) {
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

  public PageProductsDTO getProducts(
      String phone,
      int page,
      String field,
      boolean isDescending,
      String text,
      String category,
      String shopName,
      boolean hiddenBlock) {
    return aggregationRepository.getProducts(
        phone, page, field, isDescending, text, category, shopName, hiddenBlock);
  }

  public List<ProductWithShopDTO> getProductsByIds(List<String> productsInfo)
      throws JsonProcessingException {
    if (productsInfo.size() == 0) {
      return List.of();
    }
    ObjectMapper objectMapper = new ObjectMapper();
    List<ProductInfo> products = new ArrayList<>();
    for (String json : productsInfo) {
      products.add(objectMapper.readValue(json, ProductInfo.class));
    }
    return aggregationRepository.findProductsByIdsAndType(products);
  }

  public PageProductsWithShopDTO getProductsWithShops(
      int page, String field, boolean isDescending, String text, String category, String shopName) {
    PageProductsDTO pageProductsDTO =
        getProducts(null, page, field, isDescending, text, category, shopName, true);
    List<ProductWithShopDTO> products = new ArrayList<>();
    for (ProductDTO productDTO : pageProductsDTO.getProducts()) {
      Shop shop = shopRepository.findImageUrlAndNameById(productDTO.getShopId());
      products.add(new ProductWithShopDTO(productDTO, shop.getName(), shop.getImageUrl()));
    }
    return new PageProductsWithShopDTO(pageProductsDTO.getMaxPage(), products);
  }

  public boolean checkProductsQuantity(Map<ProductInfo, Integer> products) {
    final int maxQuantityPerProduct = 10;
    final int maxQuantityPerAllProducts = 100;
    int totalQuantity = 0;
    int quantity;
    for (Map.Entry<ProductInfo, Integer> entry : products.entrySet()) {
      quantity = entry.getValue();
      if (quantity > maxQuantityPerProduct || quantity <= 0) {
        return false;
      } else if (!productRepository.existsById(entry.getKey().getProductId())) {
        return false;
      } else {
        totalQuantity += quantity;
      }
    }
    return totalQuantity <= maxQuantityPerAllProducts;
  }

  public Map<ProductInfo, Integer> getProductsInfo(Map<String, Integer> productsId) {
    try {
      if (productsId.size() == 0) {
        return Map.of();
      }
      ObjectMapper objectMapper = new ObjectMapper();
      Map<ProductInfo, Integer> productsInfo = new HashMap<>();
      for (Map.Entry<String, Integer> entry : productsId.entrySet()) {
        productsInfo.put(
            objectMapper.readValue(entry.getKey(), ProductInfo.class), entry.getValue());
      }
      return productsInfo;
    } catch (Exception e) {
      return Map.of();
    }
  }

  public boolean buyProducts(
      List<ProductPayU> products, String cardId, String orderId, Integer points, String phone) {
    ObjectMapper objectMapper = new ObjectMapper();
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      LocalDateTime localDateTime = LocalDateTime.now();
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              int newQuantity;
              Map<String, Object> productInfo;
              ObjectId productId;
              ObjectId shopId;
              for (ProductPayU product : products) {
                try {
                  productInfo =
                      objectMapper.readValue(
                          product.getName(), new TypeReference<Map<String, Object>>() {});
                  productId = new ObjectId((String) productInfo.get("productId"));
                  shopId = productRepository.findShopIdById(productId).getShopId();
                  for (int i = 1; i <= product.getQuantity(); i++) {
                    Order order = new Order();
                    order.setProductId(productId);
                    order.setOrderId(new ObjectId(orderId));
                    order.setUsed(false);
                    order.setCardId(new ObjectId(cardId));
                    order.setCreatedAt(localDateTime);
                    order.setShopId(shopId);
                    if ((Boolean) productInfo.get("hasPromotion")) {
                      Promotion promotion = promotionRepository.findByProductId(productId);
                      if (promotion.getQuantity() != null) {
                        newQuantity = promotion.getQuantity() - 1;
                        if (newQuantity == 0) {
                          mongoTemplate.remove(promotion);
                        } else {
                          promotion.setQuantity(newQuantity);
                          mongoTemplate.save(promotion);
                        }
                      }
                    }
                    order.setPrice(product.getUnitPrice());
                    mongoTemplate.save(order);
                  }
                } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              }
              reservedProductService.deleteAllByOrderId(orderId);
              if (points != null) {
                Optional<User> optionalUser =
                    userRepository.findById(accountRepository.findIdByPhone(phone).getId());
                if (optionalUser.isEmpty()) {
                  throw new RuntimeException();
                }
                User user = optionalUser.get();
                user.setPoints(user.getPoints() - points);
                mongoTemplate.save(user);
              }
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public PageOwnerProductsDTO getProductsByOwnerCard(
      int page,
      String field,
      boolean isDescending,
      String text,
      String category,
      String shopName,
      String cardId) {
    return aggregationRepository.getProductsByOwnerCard(
        page, field, isDescending, text, category, shopName, cardId);
  }

  public List<ProductOrderDTO> getProductsAtCard(String phone, String cardId) {
    return orderRepository.getProductsAtCard(
        accountRepository.findIdByPhone(phone).getId(), new ObjectId(cardId));
  }

  public List<ProductPayU> getProductsPayU(Map<ProductInfo, Integer> products)
      throws JsonProcessingException {
    ProductInfo productInfo;
    ObjectMapper objectMapper = new ObjectMapper();
    int price = 0;
    List<ProductPayU> productPayUS = new ArrayList<>();
    ObjectId productId;
    for (Map.Entry<ProductInfo, Integer> entry : products.entrySet()) {
      productInfo = entry.getKey();
      productId = productInfo.getProductId();
      if (productInfo.isHasPromotion()) {
        price = promotionRepository.findNewPriceByProductId(productId).getNewPrice();
      } else {
        price = productRepository.findPriceById(productId).getPrice();
      }
      Map<String, Object> name = new HashMap<>(2);
      name.put("productId", productId.toHexString());
      name.put("hasPromotion", productInfo.isHasPromotion());
      ProductPayU productPayU = new ProductPayU();
      productPayU.setUnitPrice(price);
      productPayU.setQuantity(entry.getValue());
      productPayU.setName(objectMapper.writeValueAsString(name));
      productPayUS.add(productPayU);
    }
    return productPayUS;
  }
}
