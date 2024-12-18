package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.dto.ProductOrderDTO;
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

  @Aggregation(
      pipeline = {
        """
    {
      $match: {
        "cardId": ?1,
        "isUsed": false
      }
    }
""",
        """
  {
    $lookup: {
      "from": "products",
      "localField": "productId",
      "foreignField": "_id",
      "as": "product"
    }
  }
""",
        """
    {
      $unwind: {
        "path": "$product",
        "preserveNullAndEmptyArrays": true
      }
    }
""",
        """
    {
      $match: {
        "product.shopId": ?0
      }
    }
""",
        """
    {
      $addFields: {
        "name": "$product.name",
        "description": "$product.description",
        "imageUrl": "$product.imageUrl",
        "barcode": "$product.barcode",
        "id": "$_id"
      }
    }
""",
        """
  {
    $project: {
      "name": 1,
      "description": 1,
      "imageUrl": 1,
      "barcode": 1,
      "id": 1
    }
  }
"""
      })
  List<ProductOrderDTO> getProductsAtCard(ObjectId shopId, ObjectId cardId);

  Order findByIdAndCardIdAndShopId(ObjectId id, ObjectId cardId, ObjectId shopId);
}
