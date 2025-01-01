package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Product;
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
      "email": "$account.email"
    }
  }
"""
      })
  Account findAccountByProductId(ObjectId id);
}
