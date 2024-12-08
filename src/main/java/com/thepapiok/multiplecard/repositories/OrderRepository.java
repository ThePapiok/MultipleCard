package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Order;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OrderRepository extends MongoRepository<Order, ObjectId> {
  @Query(value = "{'productId': ?0, 'isUsed': ?1}")
  List<Order> findAllByProductIdAndIsUsed(ObjectId id, boolean isUsed);

  @Aggregation(
      pipeline = {
        """
      {
        $match: {
          "shopId": ?0,
          "isUsed": true
        }
      }
""",
        """
                  {
                    $group: {
                      "_id": "_id",
                      "sum": {
                        $sum: "$price"
                      }
                    }
                  }
                  """,
        """
        {
          $project: {
            "_id": 0,
            "sum": 1
          }
        }
"""
      })
  Long sumTotalAmountForShop(ObjectId shopId);

  boolean existsByOrderId(ObjectId orderId);
}
