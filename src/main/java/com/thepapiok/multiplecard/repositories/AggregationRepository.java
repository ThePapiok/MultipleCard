package com.thepapiok.multiplecard.repositories;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import com.mongodb.BasicDBObject;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.misc.CustomProjectAggregationOperation;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class AggregationRepository {
  private static final String COUNT_FIELD = "count";
  private static final String ID_FIELD = "_id";
  private static final String PRODUCTS_COLLECTION = "products";
  private static final String BLOCKED_FIELD = "blocked";
  private static final String PRODUCT_ID_FIELD = "productId";
  private final AccountRepository accountRepository;
  private final MongoTemplate mongoTemplate;

  @Autowired
  public AggregationRepository(AccountRepository accountRepository, MongoTemplate mongoTemplate) {
    this.accountRepository = accountRepository;
    this.mongoTemplate = mongoTemplate;
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
    final String amountField = "amount";
    final String updatedAtField = "updatedAt";
    final String isNullField = "isNull";
    final String productField = "product";
    final String promotionField = "promotion";
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
          sortOperation = Aggregation.sort(Sort.by(amountField).descending());
        } else {
          sortOperation = Aggregation.sort(Sort.by(amountField).ascending());
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
                  "product.barcode": "$barcode",
                  "product.amount": "$amount",
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
                          "product.barcode": 1,
                          "product.amount": 1,
                          "product.shopId": 1,
                          "product.updatedAt": 1,
                          "product._class": 1
                        }
                      }
                      """));
    }
    stages.add(lookup("promotions", ID_FIELD, PRODUCT_ID_FIELD, promotionField));
    stages.add(unwind(promotionField, true));
    if (phone != null) {
      stages.add(lookup(BLOCKED_FIELD, ID_FIELD, PRODUCT_ID_FIELD, BLOCKED_FIELD));
      stages.add(unwind(BLOCKED_FIELD, true));
    }
    stages.add(project(productField, promotionField, BLOCKED_FIELD).andExclude(ID_FIELD));
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
                  "barcode": "$product.barcode",
                  "amount": "$product.amount",
                  "shopId": "$product.shopId",
                  "startAtPromotion": "$promotion.startAt",
                  "expiredAtPromotion": "$promotion.expiredAt",
                  "countPromotion": "$promotion.count",
                  "amountPromotion": "$promotion.amount",
                  "isActive": {
                    $cond: {
                      if: {$lte: ["$blocked", null]},
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
                          "barcode": 1,
                          "amount": 1,
                          "shopId": 1,
                          "startAtPromotion": 1,
                          "expiredAtPromotion": 1,
                          "countPromotion": 1,
                          "amountPromotion": 1,
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
        stages.add(match(Criteria.where("shopId").is(shopId)));
      } else {
        stages.add(
            match(
                Criteria.where("$text")
                    .is(new BasicDBObject("$search", text))
                    .and("shopId")
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
      stages.add(unwind("shop", true));
      stages.add(match(Criteria.where("shop.name").is(shopName)));
    }
  }
}
