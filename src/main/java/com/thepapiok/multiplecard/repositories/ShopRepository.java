package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopRepository extends MongoRepository<Shop, ObjectId> {
  boolean existsByAccountNumber(String accountNumber);

  boolean existsByName(String name);

  @Aggregation(
      pipeline = {
        """
                       {
                       '$project' : {
                            "points": 1,
                        }
                       }

                    """,
        """
                {
                "$unwind": {
                    "path": "$points"
                }
                }
            """,
        """
            {
                "$project": {
                    "isFound": {
                        "$cond": {
                            if: {
                                $eq: ["$points", ?0]
                            },
                            then: 1,
                            else: 0

                        }
                    }
                }
            }
            """,
        """
            {
                "$group": {
                    "_id": "_id",
                    "sum": {
                        "$sum": "$isFound"
                    }
                }
            }
            """,
        """
            {
                "$project": {
                    "_id": 0,
                    "isFound": {
                        "$cond": {
                            if: {
                                $ne: ["$sum", 0]
                            },
                            then: true,
                            else: false
                        }
                    }
                }
            }
            """
      })
  Boolean existsByPoint(Address address);
}
