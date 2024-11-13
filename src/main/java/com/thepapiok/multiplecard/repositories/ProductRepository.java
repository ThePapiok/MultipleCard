package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductRepository extends MongoRepository<Product, ObjectId> {
  List<Product> getAllByShopId(ObjectId objectId);

  boolean existsByNameAndShopId(String name, ObjectId shopId);

  boolean existsByBarcodeAndShopId(String barcode, ObjectId shopId);

  @Query(value = "{'_id': ?0}", fields = "{'shopId': 1, '_id': 0}")
  Product findShopIdById(ObjectId productId);

  @Aggregation(
      pipeline = {
        """
                                {
                                  $match: {
                                    "_id": {$in: ?0}
                                  }
                                }
                            """,
        """
                              {
                                $lookup: {
                                  "from": "promotions",
                                  "localField": "_id",
                                  "foreignField": "productId",
                                  "as": "promotion"
                                }
                              }
                            """,
        """
                            {
                                $unwind: {
                                    "path": "$promotion",
                                    "preserveNullAndEmptyArrays": true
                                }
                            }
                            """,
        """
                             {
                               $lookup: {
                                 "from": "shops",
                                 "localField": "shopId",
                                 "foreignField": "_id",
                                 "as": "shop"
                               }
                             }
                            """,
        """
                            {
                                $unwind: {
                                    "path": "$shop",
                                    "preserveNullAndEmptyArrays": true
                                }
                            }
                            """,
        """
                                  {
                                      $skip: ?1
                                  }
                            """,
        """
                                 {
                                     $limit: 12
                                 }

                            """,
        """
                            {
                                            $addFields: {
                                              "productId": "$_id",
                                              "productName": "$name",
                                              "description": "$description",
                                              "productImageUrl": "$imageUrl",
                                              "barcode": "$barcode",
                                              "amount": "$amount",
                                              "shopId": "$shopId",
                                              "startAtPromotion": "$promotion.startAt",
                                              "expiredAtPromotion": "$promotion.expiredAt",
                                              "countPromotion": "$promotion.count",
                                              "amountPromotion": "$promotion.amount",
                                              "isActive": true,
                                              "shopName": "$shop.name",
                                              "shopImageUrl": "$shop.imageUrl"
                                            }
                                          }
                            """,
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
                                              "isActive": 1,
                                              "shopName": 1,
                                              "shopImageUrl": 1,
                                              "_id": 0
                                }
                            }
                            """,
        """
                                {
                                    $sort: {
                                        "shopId": 1
                                    }
                                }
                            """
      })
  List<ProductWithShopDTO> findProductsByIds(List<ObjectId> productsId, int skip);
}
