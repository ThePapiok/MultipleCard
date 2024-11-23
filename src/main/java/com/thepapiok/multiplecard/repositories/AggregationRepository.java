package com.thepapiok.multiplecard.repositories;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import com.mongodb.BasicDBObject;
import com.thepapiok.multiplecard.collections.ReservedProduct;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.misc.CustomProjectAggregationOperation;
import com.thepapiok.multiplecard.misc.ProductInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Repository
public class AggregationRepository {
  private static final String COUNT_FIELD = "count";
  private static final String ID_FIELD = "_id";
  private static final String PRODUCTS_COLLECTION = "products";
  private static final String BLOCKED_FIELD = "blockedProducts";
  private static final String PRODUCT_ID_FIELD = "productId";
  private static final String PROMOTION_FIELD = "promotion";
  private static final String SHOP_ID_FIELD = "shopId";
  private static final String SHOP_FIELD = "shop";
  private final AccountRepository accountRepository;
  private final MongoTemplate mongoTemplate;
  private final MongoTransactionManager mongoTransactionManager;
  private final PromotionRepository promotionRepository;
  private final ReservedProductsRepository reservedProductsRepository;

  @Autowired
  public AggregationRepository(
      AccountRepository accountRepository,
      MongoTemplate mongoTemplate,
      MongoTransactionManager mongoTransactionManager,
      PromotionRepository promotionRepository,
      ReservedProductsRepository reservedProductsRepository) {
    this.accountRepository = accountRepository;
    this.mongoTemplate = mongoTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.promotionRepository = promotionRepository;
    this.reservedProductsRepository = reservedProductsRepository;
  }

  public List<ProductDTO> getProducts(
      String phone,
      int page,
      String field,
      boolean isDescending,
      String text,
      String category,
      String shopName) {
    final int countReviewsAtPage = 12;
    final String createdAtField = "order.createdAt";
    final String dateField = "date";
    final String priceField = "price";
    final String updatedAtField = "updatedAt";
    final String isNullField = "isNull";
    final String productField = "product";
    GroupOperation groupOperation = null;
    SortOperation sortOperation = null;
    boolean hasOrders = false;
    List<AggregationOperation> stages = new ArrayList<>();
    matchByTextCategoryOrShopName(stages, text, category, shopName, phone);
    switch (field) {
      case COUNT_FIELD:
        groupOperation =
            Aggregation.group(ID_FIELD).count().as(COUNT_FIELD).min(createdAtField).as(dateField);
        if (isDescending) {
          sortOperation =
              Aggregation.sort(
                  Sort.by(isNullField).ascending().and(Sort.by(COUNT_FIELD).descending()));
        } else {
          sortOperation =
              Aggregation.sort(
                  Sort.by(isNullField).descending().and(Sort.by(COUNT_FIELD).ascending()));
        }
        hasOrders = true;
        break;
      case dateField:
        if (isDescending) {
          groupOperation = Aggregation.group(ID_FIELD).max(createdAtField).as(dateField);
          sortOperation =
              Aggregation.sort(
                  Sort.by(isNullField).ascending().and(Sort.by(dateField).descending()));
        } else {
          groupOperation = Aggregation.group(ID_FIELD).min(createdAtField).as(dateField);
          sortOperation =
              Aggregation.sort(
                  Sort.by(isNullField).descending().and(Sort.by(dateField).ascending()));
        }
        hasOrders = true;
        break;
      case "price":
        if (isDescending) {
          sortOperation = Aggregation.sort(Sort.by(priceField).descending());
        } else {
          sortOperation = Aggregation.sort(Sort.by(priceField).ascending());
        }
        break;
      case "added":
        if (isDescending) {
          sortOperation = Aggregation.sort(Sort.by(updatedAtField).descending());
        } else {
          sortOperation = Aggregation.sort(Sort.by(updatedAtField).ascending());
        }
        break;
      default:
        break;
    }
    if (hasOrders) {
      stages.add(project(ID_FIELD));
      stages.add(
          new CustomProjectAggregationOperation(
              """
                                      {
                                          $lookup: {
                                            "from": "orders",
                                            "localField": "_id",
                                            "foreignField": "productId",
                                            "as": "order",
                                            "pipeline": [{
                                              $match: {
                                                "isUsed": true
                                              }
                                            },
                                            {
                                              $project: {
                                                "_id": 0,
                                                "createdAt": 1
                                              }
                                            }
                                            ]
                                          }
                                        }
                                    """));
      stages.add(unwind("order", true));
      stages.add(groupOperation);
      stages.add(
          new CustomProjectAggregationOperation(
              """
                                      {
                                             $addFields: {
                                               "isNull": {
                                                 $cond: {
                                                   if: {
                                                     $eq: ["$date", null]
                                                   },
                                                   then: 1,
                                                   else: 0,
                                                 }
                                               }
                                             }
                                           }
                                    """));
      stages.add(sortOperation);
      stages.add(lookup(PRODUCTS_COLLECTION, ID_FIELD, ID_FIELD, productField));
      stages.add(unwind(productField, true));
    } else {
      stages.add(sortOperation);
      stages.add(
          new CustomProjectAggregationOperation(
              """
                                    {
                                      $addFields: {
                                        "product._id": "$_id",
                                        "product.name": "$name",
                                        "product.description": "$description",
                                        "product.imageUrl": "$imageUrl",
                                        "product.price": "$price",
                                        "product.shopId": "$shopId",
                                        "product.updatedAt": "$updatedAt",
                                        "product._class": "$_class"
                                      }
                                    }
                                    """));
      stages.add(
          new CustomProjectAggregationOperation(
              """
                                    {
                                      $project: {
                                        "product._id": 1,
                                        "product.name": 1,
                                        "product.description": 1,
                                        "product.imageUrl": 1,
                                        "product.price": 1,
                                        "product.shopId": 1,
                                        "product.updatedAt": 1,
                                        "product._class": 1
                                      }
                                    }
                                    """));
    }
    stages.add(lookup("promotions", ID_FIELD, PRODUCT_ID_FIELD, PROMOTION_FIELD));
    stages.add(unwind(PROMOTION_FIELD, true));
    if (phone != null) {
      stages.add(lookup(BLOCKED_FIELD, ID_FIELD, PRODUCT_ID_FIELD, BLOCKED_FIELD));
      stages.add(unwind(BLOCKED_FIELD, true));
    }
    stages.add(project(productField, PROMOTION_FIELD, BLOCKED_FIELD).andExclude(ID_FIELD));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {
                                    $lookup: {
                                        "from": "reservedProducts",
                                        "localField": "promotion._id",
                                        "foreignField": "promotionId",
                                        "as": "reservedProduct",
                                        "pipeline": [
                                            {
                                                $group: {
                                                    "_id": "$promotionId",
                                                    "quantity": {
                                                        $count: {}
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                }
                                """));
    stages.add(unwind("reservedProduct", true));
    stages.add(skip((long) countReviewsAtPage * page));
    stages.add(limit(countReviewsAtPage));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                  {
                                    $addFields: {
                                      "productId": "$product._id",
                                      "productName": "$product.name",
                                      "description": "$product.description",
                                      "productImageUrl": "$product.imageUrl",
                                      "price": "$product.price",
                                      "shopId": "$product.shopId",
                                      "startAtPromotion": "$promotion.startAt",
                                      "expiredAtPromotion": "$promotion.expiredAt",
                                      "quantityPromotion": {
                                        $subtract: ["$promotion.quantity", {
                                          $ifNull: ["$reservedProduct.quantity", 0]}]},
                                      "newPricePromotion": "$promotion.newPrice",
                                      "isActive": {
                                        $cond: {
                                          if: {$lte: ["$blockedProducts", null]},
                                          then: true,
                                          else: false
                                        }
                                      }
                                    }
                                  }
                                """));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                  {
                                    $project: {
                                      "productId": 1,
                                      "productName": 1,
                                      "description": 1,
                                      "productImageUrl": 1,
                                      "price": 1,
                                      "shopId": 1,
                                      "startAtPromotion": 1,
                                      "expiredAtPromotion": 1,
                                      "quantityPromotion": 1,
                                      "newPricePromotion": 1,
                                      "isActive": 1
                                    }
                                  }
                                """));
    Aggregation aggregation = newAggregation(stages);
    return mongoTemplate
        .aggregate(aggregation, PRODUCTS_COLLECTION, ProductDTO.class)
        .getMappedResults();
  }

  public int getMaxPage(String text, String phone, String category, String shopName) {
    final float countProductsAtPage = 12.0F;
    List<AggregationOperation> stages = new ArrayList<>();
    matchByTextCategoryOrShopName(stages, text, category, shopName, phone);
    stages.add(group("id").count().as(COUNT_FIELD));
    stages.add(project(COUNT_FIELD).andExclude(ID_FIELD));
    Aggregation aggregation = newAggregation(stages);
    Document result =
        mongoTemplate
            .aggregate(aggregation, PRODUCTS_COLLECTION, Document.class)
            .getUniqueMappedResult();
    Integer pages = null;
    if (result != null) {
      pages = result.getInteger(COUNT_FIELD);
    }
    if (pages == null) {
      pages = 0;
    }
    return (int) Math.ceil(pages / countProductsAtPage);
  }

  private void matchByTextCategoryOrShopName(
      List<AggregationOperation> stages,
      String text,
      String category,
      String shopName,
      String phone) {
    if (phone != null) {
      ObjectId shopId = accountRepository.findIdByPhone(phone).getId();
      if ("".equals(text)) {
        stages.add(match(Criteria.where(SHOP_ID_FIELD).is(shopId)));
      } else {
        stages.add(
            match(
                Criteria.where("$text")
                    .is(new BasicDBObject("$search", text))
                    .and(SHOP_ID_FIELD)
                    .is(shopId)));
      }
    } else {
      if (!"".equals(text)) {
        stages.add(match(Criteria.where("$text").is(new BasicDBObject("$search", text))));
      }
      stages.add(lookup(BLOCKED_FIELD, ID_FIELD, PRODUCT_ID_FIELD, BLOCKED_FIELD));
      stages.add(match(Criteria.where(BLOCKED_FIELD).size(0)));
    }
    if (!"".equals(category)) {
      stages.add(unwind("categories", true));
      stages.add(
          new CustomProjectAggregationOperation(
              """
                                    {
                                      $lookup: {
                                        "from": "categories",
                                        "localField": "categories",
                                        "foreignField": "_id",
                                        "as": "category",
                                        "pipeline": [
                                          {
                                            $project: {
                                              "name": 1
                                            }
                                          }
                                        ]
                                      }
                                    }
                                    """));
      stages.add(unwind("category", true));
      stages.add(match(Criteria.where("category.name").is(category)));
    }
    if (!"".equals(shopName)) {
      stages.add(
          new CustomProjectAggregationOperation(
              """
                                    {
                                      $lookup: {
                                        "from": "shops",
                                        "localField": "shopId",
                                        "foreignField": "_id",
                                        "as": "shop",
                                        "pipeline": [
                                          {
                                            $project: {
                                              "name": 1
                                            }
                                          }
                                        ]
                                      }
                                    }
                                    """));
      stages.add(unwind(SHOP_FIELD, true));
      stages.add(match(Criteria.where("shop.name").is(shopName)));
    }
  }

  public List<ProductWithShopDTO> findProductsByIdsAndType(List<ProductInfo> productsInfo) {
    List<AggregationOperation> stages = new ArrayList<>();
    ProductInfo productInfo;
    String productId;
    StringBuilder query = new StringBuilder();
    for (int i = 0; i < productsInfo.size(); i++) {
      productInfo = productsInfo.get(i);
      productId = "ObjectId('" + productInfo.getProductId().toString() + "')]";
      if (i == 0) {
        stages.add(match(Criteria.where(ID_FIELD).is(productInfo.getProductId())));
        stages.add(lookup("shops", SHOP_ID_FIELD, ID_FIELD, SHOP_FIELD));
        stages.add(unwind(SHOP_FIELD, true));
        if (!productInfo.isHasPromotion()) {
          stages.add(
              new CustomProjectAggregationOperation(
                  """
                                              {
                                                $addFields: {
                                                  "productId": "$_id",
                                                  "productName": "$name",
                                                  "productImageUrl": "$imageUrl",
                                                  "isActive": true,
                                                  "shopName": "$shop.name",
                                                  "shopImageUrl": "$shop.imageUrl"
                                              }}
                                            """));
          stages.add(
              new CustomProjectAggregationOperation(
                  """
                                              {
                                                $project: {
                                                  "productId": 1,
                                                  "productName": 1,
                                                  "description": 1,
                                                  "productImageUrl": 1,
                                                  "price": 1,
                                                  "shopId": 1,
                                                  "isActive": 1,
                                                  "shopName": 1,
                                                  "shopImageUrl": 1
                                                }
                                              }
                                            """));
        } else {
          stages.add(lookup("promotions", ID_FIELD, PRODUCT_ID_FIELD, PROMOTION_FIELD));
          stages.add(unwind(PROMOTION_FIELD, true));
          stages.add(
              new CustomProjectAggregationOperation(
                  """
                                            {
                                                $lookup: {
                                                    "from": "reservedProducts",
                                                    "localField": "promotion._id",
                                                    "foreignField": "promotionId",
                                                    "as": "reservedProduct",
                                                    "let": {
                                                        "promotionId": "$promotion._id"
                                                    }
                                                    "pipeline": [
                                                        {
                                                            $group: {
                                                                "_id": "$promotionId",
                                                                "quantity": {
                                                                    $count: {}
                                                                }
                                                            }},{
                                                            $match: {
                                                                $expr: {
                                                                    $eq: ["$_id", "$$promotionId"]
                                                                }
                                                            }
                                                        }
                                                    ]
                                                }
                                            }
                                            """));
          stages.add(unwind("reservedProduct", true));
          stages.add(
              new CustomProjectAggregationOperation(
                  """
                                              {
                                                $addFields: {
                                                  "productId": "$_id",
                                                  "productName": "$name",
                                                  "productImageUrl": "$imageUrl",
                                                  "startAtPromotion": "$promotion.startAt",
                                                  "expiredAtPromotion": "$promotion.expiredAt",
                                                  "quantityPromotion": {
                                                    $subtract: ["$promotion.quantity", {
                                                      $ifNull: ["$reservedProduct.quantity", 0]}]},
                                                  "newPricePromotion": "$promotion.newPrice",
                                                  "isActive": true,
                                                  "shopName": "$shop.name",
                                                  "shopImageUrl": "$shop.imageUrl"

                                              }}
                                            """));
          stages.add(
              new CustomProjectAggregationOperation(
                  """
                                              {
                                                $project: {
                                                  "productId": 1,
                                                  "productName": 1,
                                                  "description": 1,
                                                  "productImageUrl": 1,
                                                  "price": 1,
                                                  "shopId": 1,
                                                  "startAtPromotion": 1,
                                                  "expiredAtPromotion": 1,
                                                  "quantityPromotion": 1,
                                                  "newPricePromotion": 1,
                                                  "isActive": 1,
                                                  "shopName": 1,
                                                  "shopImageUrl": 1
                                                }
                                              }
                                            """));
        }
      } else {
        query.setLength(0);
        if (!productInfo.isHasPromotion()) {
          query.append(
              """
                                {
                                    $unionWith: {
                                        coll: "products",
                                        pipeline: [
                                            {
                                                $match: {
                                                    $expr: {
                                                        $eq: ["$_id", """);
          query.append(productId);
          query.append(
              """
                                                        }
                                                    }
                                                },
                                                {
                                                            $lookup: {
                                                                "from": "shops",
                                                                "localField": "shopId",
                                                                "foreignField": "_id",
                                                                "as": "shop"
                                                            }
                                                        },
                                                        {
                                                            $unwind: {
                                                                "path": "$shop",
                                                                "preserveNullAndEmptyArrays": true
                                                            }
                                                        },
                                                        {
                                                            $addFields: {
                                                                "productId": "$_id",
                                                                "productName": "$name",
                                                                "productImageUrl": "$imageUrl",
                                                                "isActive": true,
                                                                "shopName": "$shop.name",
                                                                "shopImageUrl": "$shop.imageUrl"
                                                            }
                                                        },
                                                        {
                                                            $project: {
                                                            "productId": 1,
                                                            "productName": 1,
                                                            "description": 1,
                                                            "productImageUrl": 1,
                                                            "price": 1,
                                                            "shopId": 1,
                                                            "isActive": 1,
                                                            "shopName": 1,
                                                            "shopImageUrl": 1
                                                        }
                                                        }]}}
                                    """);
          stages.add(new CustomProjectAggregationOperation(query.toString()));
        } else {
          query.append(
              """
                                {
                                    $unionWith: {
                                        coll: "products",
                                        pipeline: [
                                            {
                                               $match: {
                                                    $expr: {
                                                        $eq: ["$_id", """);
          query.append(productId);
          query.append(
              """
                                                        }
                                                   }
                                           },
                                           {
                                                $lookup: {
                                                    "from": "shops",
                                                    "localField": "shopId",
                                                    "foreignField": "_id",
                                                    "as": "shop"
                                                    }
                                                },
                                           {
                                                $unwind: {
                                                    "path": "$shop",
                                                    "preserveNullAndEmptyArrays": true
                                                    }
                                           },
                                           {
                                                $lookup: {
                                                     "from": "promotions",
                                                     "localField": "_id",
                                                     "foreignField": "productId",
                                                     "as": "promotion"
                                                     }
                                           },
                                           {
                                                 $unwind: {
                                                      "path": "$promotion",
                                                      "preserveNullAndEmptyArrays": true
                                                      }
                                           },
                                           {
                                                $lookup: {
                                                    "from": "reservedProducts",
                                                    "localField": "promotion._id",
                                                    "foreignField": "promotionId",
                                                    "as": "reservedProduct",
                                                    "let": {
                                                        "promotionId": "$promotion._id"
                                                    },
                                                    "pipeline": [{
                                                        $group: {
                                                            "_id": "$promotionId",
                                                            "quantity": {
                                                                $count: {}
                                                                }
                                                            }
                                                        },
                                                     {
                                                        $match: {
                                                            $expr: {
                                                                $eq: ["$_id", "$$promotionId"]
                                                                }
                                                            }
                                                     }]

                                                }
                                            },
                                            {
                                                $unwind: {
                                                    "path": "$reservedProduct",
                                                    "preserveNullAndEmptyArrays": true
                                                    }
                                            },
                                            {
                                                $addFields: {
                                                    "productId": "$_id",
                                                    "productName": "$name",
                                                    "productImageUrl": "$imageUrl",
                                                    "startAtPromotion": "$promotion.startAt",
                                                    "expiredAtPromotion": "$promotion.expiredAt",
                                                    "quantityPromotion": {
                                                        $subtract: ["$promotion.quantity", {
                                                            $ifNull: ["$reservedProduct.quantity", 0]}
                                                            ]
                                                    },
                                                    "newPricePromotion": "$promotion.newPrice",
                                                    "isActive": true,
                                                    "shopName": "$shop.name",
                                                    "shopImageUrl": "$shop.imageUrl"
                                                    }
                                            },
                                            {
                                                $project: {
                                                    "productId": 1,
                                                    "productName": 1,
                                                    "description": 1,
                                                    "productImageUrl": 1,
                                                    "price": 1,
                                                    "shopId": 1,
                                                    "startAtPromotion": 1,
                                                    "expiredAtPromotion": 1,
                                                    "quantityPromotion": 1,
                                                    "newPricePromotion": 1,
                                                    "isActive": 1,
                                                    "shopName": 1,
                                                    "shopImageUrl": 1
                                                    }
                                            }]}}
                                            """);
          stages.add(new CustomProjectAggregationOperation(query.toString()));
        }
      }
    }
    stages.add(sort(Sort.by(SHOP_ID_FIELD).ascending().and(Sort.by(PRODUCT_ID_FIELD).ascending())));
    return mongoTemplate
        .aggregate(newAggregation(stages), PRODUCTS_COLLECTION, ProductWithShopDTO.class)
        .getMappedResults();
  }

  public boolean reservedProducts(
      Map<ObjectId, Integer> reducedProducts,
      String encryptedIp,
      ObjectId orderId,
      ObjectId cardId) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              ObjectId productId;
              ObjectId promotionId;
              for (Map.Entry<ObjectId, Integer> entry : reducedProducts.entrySet()) {
                productId = entry.getKey();
                if (!promotionRepository.existsByProductIdAndQuantityIsNotNull(productId)) {
                  continue;
                }
                promotionId = promotionRepository.findIdByProductId(productId).getId();
                for (int i = 1; i <= entry.getValue(); i++) {
                  ReservedProduct reservedProduct = new ReservedProduct();
                  reservedProduct.setEncryptedIp(encryptedIp);
                  reservedProduct.setPromotionId(promotionId);
                  reservedProduct.setOrderId(orderId);
                  reservedProduct.setCardId(cardId);
                  mongoTemplate.save(reservedProduct);
                }
                if (promotionRepository.findQuantityById(promotionId).getQuantity()
                    < reservedProductsRepository.countByPromotionId(promotionId)) {
                  throw new RuntimeException();
                }
              }
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}

// TODO - don't reset basket
// TODO - apply promotion for the lowest..
