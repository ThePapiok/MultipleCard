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
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
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

  public List<ProductGetDTO> getProductsOwner(
      String phone, int page, String field, boolean isDescending, String text) {
    final int countReviewsAtPage = 12;
    final String createdAtField = "order.createdAt";
    final String dateField = "date";
    final String isNullField = "isNull";
    final String productField = "product";
    final String promotionField = "promotion";
    ObjectId shopId = accountRepository.findIdByPhone(phone).getId();
    GroupOperation groupOperation = null;
    SortOperation sortOperation = null;
    Criteria matchOperation = null;
    if (COUNT_FIELD.equals(field)) {
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
    } else if (dateField.equals(field)) {
      if (isDescending) {
        groupOperation = Aggregation.group(ID_FIELD).max(createdAtField).as(dateField);
        sortOperation =
            Aggregation.sort(Sort.by(isNullField).ascending().and(Sort.by(dateField).descending()));
      } else {
        groupOperation = Aggregation.group(ID_FIELD).min(createdAtField).as(dateField);
        sortOperation =
            Aggregation.sort(Sort.by(isNullField).descending().and(Sort.by(dateField).ascending()));
      }
    }
    if ("".equals(text)) {
      matchOperation = Criteria.where(SHOP_ID_FIELD).is(shopId);
    } else {
      matchOperation =
          Criteria.where(TEXT_OPERATOR)
              .is(new BasicDBObject(SEARCH_OPERATOR, text))
              .and(SHOP_ID_FIELD)
              .is(shopId);
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
    Aggregation aggregation =
        newAggregation(
            match(matchOperation),
            project(ID_FIELD),
            new CustomProjectAggregationOperation(lookupWithPipeline),
            unwind("order", true),
            groupOperation,
            new CustomProjectAggregationOperation(addFields),
            sortOperation,
            lookup(PRODUCTS_COLLECTION, ID_FIELD, ID_FIELD, productField),
            unwind(productField, true),
            lookup("promotions", ID_FIELD, "productId", promotionField),
            unwind(promotionField, true),
            project(productField, promotionField).andExclude(ID_FIELD),
            skip((long) countReviewsAtPage * page),
            limit(countReviewsAtPage));
    return mongoTemplate
        .aggregate(aggregation, PRODUCTS_COLLECTION, ProductGetDTO.class)
        .getMappedResults();
  }

  public int getMaxPage(String text, String phone) {
    final float countProductsAtPage = 12.0F;
    MatchOperation matchOperation;
    ObjectId shopId = accountRepository.findIdByPhone(phone).getId();
    if ("".equals(text)) {
      matchOperation = Aggregation.match(Criteria.where(SHOP_ID_FIELD).is(shopId));
    } else {
      matchOperation =
          Aggregation.match(
              Criteria.where(TEXT_OPERATOR)
                  .is(new BasicDBObject(SEARCH_OPERATOR, text))
                  .and(SHOP_ID_FIELD)
                  .is(shopId));
    }
    Aggregation aggregation =
        newAggregation(
            matchOperation,
            group("id").count().as(COUNT_FIELD),
            project(COUNT_FIELD).andExclude(ID_FIELD));
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
