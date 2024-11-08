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
import com.thepapiok.multiplecard.dto.ProductGetDTO;
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
  private static final String SHOP_ID_FIELD = "shopId";
  private static final String PRODUCTS_COLLECTION = "products";
  private static final String TEXT_OPERATOR = "$text";
  private static final String SEARCH_OPERATOR = "$search";
  private final AccountRepository accountRepository;
  private final MongoTemplate mongoTemplate;

  @Autowired
  public AggregationRepository(AccountRepository accountRepository, MongoTemplate mongoTemplate) {
    this.accountRepository = accountRepository;
    this.mongoTemplate = mongoTemplate;
  }

  public List<ProductGetDTO> getProducts(
      String phone, int page, String field, boolean isDescending, String text) {
    final int countReviewsAtPage = 12;
    final String createdAtField = "order.createdAt";
    final String dateField = "date";
    final String amountField = "amount";
    final String updatedAtField = "updatedAt";
    final String isNullField = "isNull";
    final String productField = "product";
    final String promotionField = "promotion";
    final String blockedField = "blocked";
    final String productIdField = "productId";
    ObjectId shopId = null;
    GroupOperation groupOperation = null;
    SortOperation sortOperation = null;
    boolean hasOrders = false;
    if (phone != null) {
      shopId = accountRepository.findIdByPhone(phone).getId();
    }
    String lookupWithPipeline =
        """
                          {
                              $lookup: {
                                "from": "orders",
                                "localField": "_id",
                                "foreignField": "productId",
                                "as": "order",
                                "pipeline": [{
                                  $project: {
                                    "_id": 0,
                                    "createdAt": 1
                                  }
                                }
                                ]
                              }
                            }
                        """;

    String addFields =
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
                        """;
    List<AggregationOperation> stages = new ArrayList<>();
    if (phone != null) {
      if ("".equals(text)) {
        stages.add(match(Criteria.where(SHOP_ID_FIELD).is(shopId)));
      } else {
        stages.add(
            match(
                Criteria.where(TEXT_OPERATOR)
                    .is(new BasicDBObject(SEARCH_OPERATOR, text))
                    .and(SHOP_ID_FIELD)
                    .is(shopId)));
      }
    } else {
      if (!"".equals(text)) {
        stages.add(
            match(Criteria.where(TEXT_OPERATOR).is(new BasicDBObject(SEARCH_OPERATOR, text))));
      }
      stages.add(lookup(blockedField, ID_FIELD, productIdField, blockedField));
      stages.add(match(Criteria.where(blockedField).size(0)));
    }
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
      stages.add(new CustomProjectAggregationOperation(lookupWithPipeline));
      stages.add(unwind("order", true));
      stages.add(groupOperation);
      stages.add(new CustomProjectAggregationOperation(addFields));
      stages.add(sortOperation);
      stages.add(lookup(PRODUCTS_COLLECTION, ID_FIELD, ID_FIELD, productField));
      stages.add(unwind(productField, true));
    } else {
      stages.add(sortOperation);
      stages.add(
          new CustomProjectAggregationOperation(
              """
              {
                $project: {
                  "product._id": "$_id",
                  "product.name": "$name",
                  "product.description": "$description",
                  "product.imageUrl": "$imageUrl",
                  "product.barcode": "$barcode",
                  "product.categories": "$categories",
                  "product.amount": "$amount",
                  "product.shopId": "$shopId",
                  "product.updatedAt": "$updatedAt",
                  "product._class": "$_class"
                }
              }
              """));
    }
    stages.add(lookup("promotions", ID_FIELD, productIdField, promotionField));
    stages.add(unwind(promotionField, true));
    if (phone != null) {
      stages.add(lookup(blockedField, ID_FIELD, productIdField, blockedField));
      stages.add(unwind(blockedField, true));
      stages.add(project(productField, promotionField, blockedField).andExclude(ID_FIELD));
      stages.add(skip((long) countReviewsAtPage * page));
      stages.add(limit(countReviewsAtPage));
    } else {
      stages.add(project(productField, promotionField).andExclude(ID_FIELD));
      stages.add(skip((long) countReviewsAtPage * page));
      stages.add(limit(countReviewsAtPage));
    }
    Aggregation aggregation = newAggregation(stages);
    return mongoTemplate
        .aggregate(aggregation, PRODUCTS_COLLECTION, ProductGetDTO.class)
        .getMappedResults();
  }

  public int getMaxPage(String text, String phone) {
    final float countProductsAtPage = 12.0F;
    List<AggregationOperation> stages = new ArrayList<>();
    if (phone != null) {
      ObjectId shopId = accountRepository.findIdByPhone(phone).getId();
      if ("".equals(text)) {
        stages.add(match(Criteria.where(SHOP_ID_FIELD).is(shopId)));
      } else {
        stages.add(
            Aggregation.match(
                Criteria.where(TEXT_OPERATOR)
                    .is(new BasicDBObject(SEARCH_OPERATOR, text))
                    .and(SHOP_ID_FIELD)
                    .is(shopId)));
      }
    } else {
      if (!"".equals(text)) {
        stages.add(
            match(Criteria.where(TEXT_OPERATOR).is(new BasicDBObject(SEARCH_OPERATOR, text))));
      }
    }
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
}
