package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Role;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountRepository extends MongoRepository<Account, ObjectId> {
  @Query(value = "{}", fields = "{'_id': 0, 'phone': 1}")
  List<Account> findAllPhones();

  @Query(value = "{}", fields = "{'_id': 0, 'email': 1}")
  List<Account> findAllEmails();

  Account findByPhone(String phone);

  @Query(value = "{'phone': ?0}", fields = "{'_id': 1}")
  Account findIdByPhone(String phone);

  @Query(value = "{'phone': ?0}", fields = "{'_id': 0, 'password': 1}")
  Account findPasswordByPhone(String phone);

  boolean existsByPhone(String phone);

  boolean existsByEmail(String email);

  @Aggregation(
      pipeline = {
        """
                              {
                                $match: {
                                "phone": "?0"
                                }
                              }
                            """,
        """
                            {
                                $addFields: {
                                    "isFound": {
                                    $cond: {
                                      if: {
                                        $eq: ["$role", "?1"]
                                        },
                                      then: true,
                                      else: false
                                    }
                                  }
                                }
                            }
                            """,
        """
                              {
                                $project: {
                                  "_id": 0,
                                  "isFound": 1
                                }
                              }
                            """
      })
  Boolean hasRole(String phone, Role role);

  @Aggregation(
      pipeline = {
        """
                            {
                                $match: {
                                 $expr: {
                                    $ne: ["$phone", ?1]
                                 }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "_id": 1
                                }
                              }
                            """,
        """
                            {
                                $lookup: {
                                    "from": "shops",
                                    "localField": "_id",
                                    "foreignField": "_id",
                                    "as": "shop",
                                    "pipeline": [{
                                        $match: {
                                            "name": ?0
                                        }
                                    },
                                    {
                                        $project: {
                                            "name": 1
                                        }
                                    }

                                    ]
                                }
                              }
                            """,
        """
                            {
                                $addFields: {
                                    "isFound": {
                                        $cond: {
                                            if: {
                                                $eq: [ {
                                                    $size: "$shop" }, 0]},
                                            then: 0,
                                            else: 1
                                        }
                                    }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "isFound": 1,
                                    "_id": 0
                                }
                            }
                            """,
        """
                            {
                                $group: {
                                    "_id": "_id",
                                    "sum": {$sum: "$isFound"}
                                }
                              }
                            """,
        """
                            {
                                $addFields: {
                                    "isFound": {
                                        $cond: {
                                            if: {
                                                $eq: ["$sum", 0]
                                            },
                                            then: false,
                                            else: true
                                        }
                                    }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "_id": 0,
                                    "isFound": 1
                                }
                            }
                            """
      })
  boolean existsByNameOtherThanPhone(String name, String phone);

  @Aggregation(
      pipeline = {
        """
                            {
                                $match: {
                                 $expr: {
                                    $ne: ["$phone", ?1]
                                 }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "_id": 1
                                }
                              }
                            """,
        """
                            {
                                $lookup: {
                                    "from": "shops",
                                    "localField": "_id",
                                    "foreignField": "_id",
                                    "as": "shop",
                                    "pipeline": [{
                                        $match: {
                                            "accountNumber": ?0
                                        }
                                    },
                                    {
                                        $project: {
                                            "accountNumber": 1
                                        }
                                    }

                                    ]
                                }
                              }
                            """,
        """
                            {
                                $addFields: {
                                    "isFound": {
                                        $cond: {
                                            if: {
                                                $eq: [ {
                                                    $size: "$shop" }, 0]},
                                            then: 0,
                                            else: 1
                                        }
                                    }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "isFound": 1,
                                    "_id": 0
                                }
                            }
                            """,
        """
                            {
                                $group: {
                                    "_id": "_id",
                                    "sum": {$sum: "$isFound"}
                                }
                              }
                            """,
        """
                            {
                                $addFields: {
                                    "isFound": {
                                        $cond: {
                                            if: {
                                                $eq: ["$sum", 0]
                                            },
                                            then: false,
                                            else: true
                                        }
                                    }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "_id": 0,
                                    "isFound": 1
                                }
                            }
                            """
      })
  boolean existsByAccountNumberOtherThanPhone(String accountNumber, String phone);

  @Aggregation(
      pipeline = {
        """
                            {
                                $match: {
                                 $expr: {
                                    $ne: ["$phone", ?1]
                                 }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "_id": 1
                                }
                              }
                            """,
        """
                            {
                                $lookup: {
                                    "from": "shops",
                                    "localField": "_id",
                                    "foreignField": "_id",
                                    "as": "shop",
                                    "pipeline": [{
                                                 "$unwind": {
                                                     "path": "$points"
                                                 }
                                                 },{
                                                     '$project' : {
                                                          "points": 1,
                                                      }
                                                     },
                                                 {
                                                 $match: {
                                                     "points": ?0
                                                 }
                                             }
                                    ]
                                }
                              }
                            """,
        """
                            {
                                $addFields: {
                                    "isFound": {
                                        $cond: {
                                            if: {
                                                $eq: [ {
                                                    $size: "$shop" }, 0]},
                                            then: 0,
                                            else: 1
                                        }
                                    }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "_id": 0,
                                    "isFound": 1
                                }
                            }
                            """,
        """
                            {
                                $group: {
                                    "_id": "_id",
                                    "sum": {$sum: "$isFound"}
                                }
                              }
                            """,
        """
                            {
                                $addFields: {
                                    "isFound": {
                                        $cond: {
                                            if: {
                                                $eq: ["$sum", 0]
                                            },
                                            then: false,
                                            else: true
                                        }
                                    }
                                }
                              }
                            """,
        """
                            {
                                $project: {
                                    "isFound": 1,
                                    "_id": 0
                                }
                            }
                            """
      })
  boolean existsByPointsOtherThanPhone(Address address, String phone);

  @Query(value = "{'_id': ?0}", fields = "{'_id': 0, 'phone': 1}")
  Account findPhoneById(ObjectId id);

  @Query(value = "{'_id': ?0}", fields = "{'_id': 0, 'phone': 1, 'email': 1}")
  Account findAccountById(ObjectId id);
}
