package com.thepapiok.multiplecard.repositories;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.addFields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import com.mongodb.BasicDBObject;
import com.thepapiok.multiplecard.collections.ReservedProduct;
import com.thepapiok.multiplecard.dto.PageCategoryDTO;
import com.thepapiok.multiplecard.dto.PageOwnerProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsDTO;
import com.thepapiok.multiplecard.dto.PageUserDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.misc.CustomProjectAggregationOperation;
import com.thepapiok.multiplecard.misc.ProductInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private static final String DATE_FIELD = "date";
  private static final String PRICE_FIELD = "price";
  private static final String ORDER_FIELD = "order";
  private static final String ID_FIELD = "_id";
  private static final String TOTAL_FIELD = "total";
  private static final String MAX_PAGE_FIELD = "maxPage";
  private static final String COMMA = ",";
  private static final String PRODUCTS_COLLECTION = "products";
  private static final String BLOCKED_FIELD = "blockedProducts";
  private static final String PRODUCT_ID_FIELD = "productId";
  private static final String PROMOTION_FIELD = "promotion";
  private static final String SHOP_ID_FIELD = "shopId";
  private static final String SHOP_FIELD = "shop";
  private static final String USER_FIELD = "user";
  private static final String PROMOTIONS_FIELD = "promotions";
  private static final String TOTAL_DOT_COUNT_VALUE = "$total.count";
  private static final String TEXT_KEY = "$text";
  private static final String SEARCH_KEY = "$search";
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

  public PageProductsDTO getProducts(
      String phone,
      int page,
      String field,
      boolean isDescending,
      String text,
      String category,
      String shopName,
      boolean hiddenBlocked) {
    final long skip = 12L * page;
    final String createdAtField = "order.createdAt";
    final String realPriceField = "realPrice";
    final String updatedAtField = "updatedAt";
    final String isNullField = "isNull";
    final String productField = "product";
    GroupOperation groupOperation = null;
    SortOperation sortOperation = null;
    boolean hasOrders = false;
    List<AggregationOperation> stages = new ArrayList<>();
    matchProducts(stages, text, category, shopName, phone, hiddenBlocked);
    switch (field) {
      case COUNT_FIELD:
        groupOperation =
            Aggregation.group(ID_FIELD).count().as(COUNT_FIELD).min(createdAtField).as(DATE_FIELD);
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
      case DATE_FIELD:
        if (isDescending) {
          groupOperation = Aggregation.group(ID_FIELD).max(createdAtField).as(DATE_FIELD);
          sortOperation =
              Aggregation.sort(
                  Sort.by(isNullField).ascending().and(Sort.by(DATE_FIELD).descending()));
        } else {
          groupOperation = Aggregation.group(ID_FIELD).max(createdAtField).as(DATE_FIELD);
          sortOperation =
              Aggregation.sort(
                  Sort.by(isNullField).descending().and(Sort.by(DATE_FIELD).ascending()));
        }
        hasOrders = true;
        break;
      case PRICE_FIELD:
        if (isDescending) {
          sortOperation = Aggregation.sort(Sort.by(realPriceField).descending());
        } else {
          sortOperation = Aggregation.sort(Sort.by(realPriceField).ascending());
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
      stages.add(unwind(ORDER_FIELD, true));
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
      stages.add(lookup(PROMOTIONS_FIELD, ID_FIELD, PRODUCT_ID_FIELD, PROMOTION_FIELD));
      stages.add(unwind(PROMOTION_FIELD, true));
    } else {
      stages.add(lookup(PROMOTIONS_FIELD, ID_FIELD, PRODUCT_ID_FIELD, PROMOTION_FIELD));
      stages.add(unwind(PROMOTION_FIELD, true));
      stages.add(
          new CustomProjectAggregationOperation(
              """
                                    {
                                      $addFields: {
                                        "realPrice": {
                                          $ifNull: ["$promotion.newPrice", "$price"]
                                        }
                                      }
                                    }
                                    """));
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
                                        "product._class": 1,
                                        "promotion": 1
                                      }
                                    }
                                    """));
    }
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
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {
                                                    $facet: {
                                                        "total": [
                                                            {
                                                                $group: {
                                                                    "_id": "_id",
                                                                    "count": {
                                                                        $count: {}
                                                                    }
                                                                }
                                                            },
                                                            {
                                                                $project: {
                                                                    "_id": 0
                                                                }
                                                            },
                                                            {
                                                                $addFields: {
                                                                    "count": {
                                                                        $ceil: {
                                                                            $divide: ["$count", 12]
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        ],
                                                        "products": [
                                                                                {
                                                                                    $skip: """
                + skip
                + COMMA
                + """
                                                                                },
                                                                                {
                                                                                  $limit: 12
                                                                                }
                                                                              ]
                                                    }
                                                }
                                """));
    stages.add(unwind(TOTAL_FIELD, true));
    stages.add(addFields().build().addField(MAX_PAGE_FIELD, TOTAL_DOT_COUNT_VALUE));
    Aggregation aggregation = newAggregation(stages);
    return mongoTemplate
        .aggregate(aggregation, PRODUCTS_COLLECTION, PageProductsDTO.class)
        .getUniqueMappedResult();
  }

  private void matchProducts(
      List<AggregationOperation> stages,
      String text,
      String category,
      String shopName,
      String phone,
      boolean hiddenBlocked) {
    if (phone != null) {
      ObjectId shopId = accountRepository.findIdByPhone(phone).getId();
      if ("".equals(text)) {
        stages.add(match(Criteria.where(SHOP_ID_FIELD).is(shopId)));
      } else {
        stages.add(
            match(
                Criteria.where(TEXT_KEY)
                    .is(new BasicDBObject(SEARCH_KEY, text))
                    .and(SHOP_ID_FIELD)
                    .is(shopId)));
      }
    } else {
      if (!"".equals(text)) {
        stages.add(match(Criteria.where(TEXT_KEY).is(new BasicDBObject(SEARCH_KEY, text))));
      }
      if (hiddenBlocked) {
        stages.add(lookup(BLOCKED_FIELD, ID_FIELD, PRODUCT_ID_FIELD, BLOCKED_FIELD));
        stages.add(match(Criteria.where(BLOCKED_FIELD).size(0)));
      }
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
          stages.add(lookup(PROMOTIONS_FIELD, ID_FIELD, PRODUCT_ID_FIELD, PROMOTION_FIELD));
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
                                                    },
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

  public PageOwnerProductsDTO getProductsByOwnerCard(
      int page,
      String field,
      boolean isDescending,
      String text,
      String category,
      String shopName,
      String cardId) {
    final long skip = 12L * page;
    SortOperation sortOperation = null;
    switch (field) {
      case COUNT_FIELD:
        if (isDescending) {
          sortOperation = Aggregation.sort(Sort.by(COUNT_FIELD).descending());
        } else {
          sortOperation = Aggregation.sort(Sort.by(COUNT_FIELD).ascending());
        }
        break;
      case DATE_FIELD:
        if (isDescending) {
          sortOperation = Aggregation.sort(Sort.by(DATE_FIELD).descending());
        } else {
          sortOperation = Aggregation.sort(Sort.by(DATE_FIELD).ascending());
        }
        break;
      case PRICE_FIELD:
        if (isDescending) {
          sortOperation = Aggregation.sort(Sort.by(PRICE_FIELD).descending());
        } else {
          sortOperation = Aggregation.sort(Sort.by(PRICE_FIELD).ascending());
        }
        break;
      default:
        break;
    }
    List<AggregationOperation> stages = new ArrayList<>();
    matchProducts(stages, text, category, shopName, null, false);
    stages.add(lookup("orders", ID_FIELD, "productId", ORDER_FIELD));
    stages.add(unwind(ORDER_FIELD, true));
    stages.add(
        match(
            Criteria.where("order.cardId").is(new ObjectId(cardId)).and("order.isUsed").is(false)));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {
                                    $group: {
                                        "_id": {
                                            "_id": "$order.productId",
                                            "price": "$order.price"
                                        },
                                        "count": {
                                            $count: {}
                                        },
                                        "date": {
                                            $max: "$order.createdAt"
                                        }
                                    }
                                }
                                """));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                    {
                                        $addFields: {
                                            "productId": "$_id._id",
                                            "price": "$_id.price",
                                        }
                                    }
                                """));
    stages.add(sortOperation);
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                    {
                                        $project: {
                                            "productId": 1,
                                            "price": 1,
                                            "count": 1
                                        }
                                    }
                                """));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {
                                    $lookup: {
                                        "from": "products",
                                        "localField": "productId",
                                        "foreignField": "_id",
                                        "as": "product",
                                        "pipeline": [
                                            {
                                                $project: {
                                                    "name": 1,
                                                    "description": 1,
                                                    "imageUrl": 1,
                                                    "shopId": 1
                                                }
                                            }
                                        ]
                                    }
                                }
                                """));
    stages.add(unwind("product", true));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {
                                    $lookup: {
                                        "from": "shops",
                                        "localField": "product.shopId",
                                        "foreignField": "_id",
                                        "as": "shop",
                                        "pipeline": [
                                            {
                                                $project: {
                                                    "name": 1,
                                                    "imageUrl": 1,
                                                }
                                            }
                                        ]
                                    }
                                }
                                """));
    stages.add(unwind(SHOP_FIELD, true));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {
                                    $addFields: {
                                        "productName": "$product.name",
                                        "description": "$product.description",
                                        "productImageUrl": "$product.imageUrl",
                                        "shopName": "$shop.name",
                                        "shopImageUrl": "$shop.imageUrl",
                                    }
                                }
                                """));
    stages.add(
        project(
            "productName",
            "description",
            "productImageUrl",
            PRICE_FIELD,
            "shopName",
            "shopImageUrl",
            COUNT_FIELD));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {
                                    $facet: {
                                        "total": [
                                            {
                                                $group: {
                                                    "_id": "_id",
                                                    "count": {
                                                        $count: {}
                                                    }
                                                }
                                            },
                                            {
                                                $project: {
                                                    "_id": 0
                                                }
                                            },
                                            {
                                                $addFields: {
                                                    "count": {
                                                        $ceil: {
                                                            $divide: ["$count", 12]
                                                        }
                                                    }
                                                }
                                            }
                                        ],
                                        "products": [
                                                                {
                                                                    $skip: """
                + skip
                + COMMA
                + """
                                                                },
                                                                {
                                                                  $limit: 12
                                                                }

                                                              ]
                                    }
                                }
                                """));
    stages.add(unwind(TOTAL_FIELD, true));
    stages.add(addFields().build().addField(MAX_PAGE_FIELD, TOTAL_DOT_COUNT_VALUE));
    return mongoTemplate
        .aggregate(newAggregation(stages), "products", PageOwnerProductsDTO.class)
        .getUniqueMappedResult();
  }

  public PageCategoryDTO getCategories(int page, String name) {
    final long skip = 100L * page;
    List<AggregationOperation> stages = new ArrayList<>();
    if (!"".equals(name)) {
      stages.add(match(Criteria.where(TEXT_KEY).is(new BasicDBObject(SEARCH_KEY, name))));
    }
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {

                                  $facet: {
                                    "categories": [
                                                                                                     {
                                                                                                         $skip: """
                + skip
                + COMMA
                + """
                                                                                                     },
                                                                                                     {
                                                                                                       $limit: 100
                                                                                                     }

                                                                                                   ],
                                    "total": [
                                                                {
                                                                    $group: {
                                                                        "_id": "_id",
                                                                        "count": {
                                                                            $count: {}
                                                                        }
                                                                    }
                                                                },
                                                                {
                                                                    $project: {
                                                                        "_id": 0
                                                                    }
                                                                },
                                                                {
                                                                    $addFields: {
                                                                        "count": {
                                                                            $ceil: {
                                                                                $divide: ["$count", 100]
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            ]
                                  }
                                }
                                """));
    stages.add(unwind(TOTAL_FIELD, true));
    stages.add(addFields().build().addField(MAX_PAGE_FIELD, TOTAL_DOT_COUNT_VALUE));
    return mongoTemplate
        .aggregate(newAggregation(stages), "categories", PageCategoryDTO.class)
        .getUniqueMappedResult();
  }

  public PageUserDTO getUsers(String type, String value, int page) {
    final long skip = 100L * page;
    final String type0 = "0";
    final String type1 = "1";
    final String type2 = "2";
    final String type3 = "3";
    final String type4 = "4";
    final String type5 = "5";
    final String type6 = "6";
    final String type7 = "7";
    final String type8 = "8";
    final String type9 = "9";
    List<AggregationOperation> stages = new ArrayList<>();
    stages.add(lookup("users", ID_FIELD, ID_FIELD, USER_FIELD));
    stages.add(unwind(USER_FIELD, true));
    stages.add(lookup("shops", ID_FIELD, ID_FIELD, SHOP_FIELD));
    stages.add(unwind(SHOP_FIELD, true));
    stages.add(
        new CustomProjectAggregationOperation(
            """
                {
                  $addFields: {
                    "firstName": {
                      $ifNull: ["$shop.firstName", "$user.firstName"]
                    },
                    "lastName": {
                      $ifNull: ["$shop.lastName", "$user.lastName"]
                    },
                    "restricted": "$user.restricted",
                    "shopName": "$shop.name"
                  }
                }
                """));
    stages.add(project().andExclude("password", SHOP_FIELD, USER_FIELD));
    if (!"".equals(type)) {
      switch (type) {
        case type0:
          stages.add(match(Criteria.where(ID_FIELD).is(new ObjectId(value))));
          break;
        case type1:
          stages.add(match(Criteria.where("shopName").is(value)));
          break;
        case type2:
          stages.add(match(Criteria.where("firstName").is(value)));
          break;
        case type3:
          stages.add(match(Criteria.where("lastName").is(value)));
          break;
        case type4:
          stages.add(match(Criteria.where("phone").is('+' + value.substring(1))));
          break;
        case type5:
          stages.add(match(Criteria.where("email").is(value)));
          break;
        case type6:
          stages.add(match(Criteria.where("role").is(value)));
          break;
        case type7:
          stages.add(match(Criteria.where("isActive").is(Boolean.parseBoolean(value))));
          break;
        case type8:
          stages.add(match(Criteria.where("isBanned").is(Boolean.parseBoolean(value))));
          break;
        case type9:
          stages.add(match(Criteria.where("restricted").is(Boolean.parseBoolean(value))));
          break;
        default:
          break;
      }
    }
    stages.add(
        new CustomProjectAggregationOperation(
            """
                                {

                                  $facet: {
                                    "users": [
                                                                                                     {
                                                                                                         $skip: """
                + skip
                + COMMA
                + """
                                                                                                     },
                                                                                                     {
                                                                                                       $limit: 100
                                                                                                     }

                                                                                                   ],
                                    "total": [
                                                                {
                                                                    $group: {
                                                                        "_id": "_id",
                                                                        "count": {
                                                                            $count: {}
                                                                        }
                                                                    }
                                                                },
                                                                {
                                                                    $project: {
                                                                        "_id": 0
                                                                    }
                                                                },
                                                                {
                                                                    $addFields: {
                                                                        "count": {
                                                                            $ceil: {
                                                                                $divide: ["$count", 100]
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            ]
                                  }
                                }
                                """));
    stages.add(unwind(TOTAL_FIELD, true));
    stages.add(addFields().build().addField(MAX_PAGE_FIELD, TOTAL_DOT_COUNT_VALUE));
    return mongoTemplate
        .aggregate(newAggregation(stages), "accounts", PageUserDTO.class)
        .getUniqueMappedResult();
  }
}
