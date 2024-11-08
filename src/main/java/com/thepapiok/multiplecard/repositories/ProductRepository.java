package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.dto.ProductWithPromotionDTO;
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
                                        $skip: ?1
                                    }
                              """,
        """
                                   {
                                       $limit: 12
                                   }

                              """
      })
  List<ProductWithPromotionDTO> findProductsByIds(List<ObjectId> productsId, int skip);
}
