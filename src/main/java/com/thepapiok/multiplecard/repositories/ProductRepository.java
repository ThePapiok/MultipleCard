package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Account;
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

  @Query(value = "{'_id': ?0}", fields = "{'_id': 0, 'price': 1}")
  Product findPriceById(ObjectId productId);

  @Aggregation(
      pipeline = {
        """
  {
    $match: {
      "_id": ?0
    }
  }
""",
        """
  {
    $lookup: {
      "from": "accounts",
      "localField": "shopId",
      "foreignField": "_id",
      "as": "account"
    }
  }
""",
        """
  {
    $unwind: {
      "path": "$account",
      "preserveNullAndEmptyArrays": true
    }
  }
""",
        """
  {
    $project: {
      "account": 1,
      "_id": 0
    }
  }
""",
        """
  {
    $addFields: {
      "phone": "$account.phone",
      "email": "$account.email",
      "_id": "$account._id"
    }
  }
"""
      })
  Account findAccountByProductId(ObjectId id);

  @Aggregation(
      pipeline = {
        """
  {
    $project: {
      "categories": 1
    }
  }
""",
        """
  {
    $unwind: {
      "path": "$categories",
      "preserveNullAndEmptyArrays": true
    }
  }
""",
        """
  {
    $match: {
      "categories": ?0
    }
  }
""",
        """
    {
      $project: {
        "categories": 0,
      }
    }
"""
      })
  List<ObjectId> getProductsIdByCategoryId(ObjectId categoryId);

  @Aggregation(
      pipeline = {
        """
    {
      $match: {
        "_id": ?0
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
        "from": "reservedProducts",
        "localField": "promotion.id",
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
          }},{
          $match: {
            $expr: {
              $eq: ["$_id", "$$promotionId"]
              }
            }
          }]
      }
    }
  """,
        """
    {
      $unwind: {
        "path": "$reservedProduct",
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
    }
  """,
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
  """
      })
  ProductWithShopDTO getProductWithShopDTOById(ObjectId id);
}
