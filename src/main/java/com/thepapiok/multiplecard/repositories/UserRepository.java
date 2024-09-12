package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, ObjectId> {

  @Aggregation(
      pipeline = {
        """
                            {
                                "$match": {
                                    "review": {
                                        $exists: true
                                    },
                                    $text: {
                                        $search: ?4
                                    }
                                }
                            }
                                        """,
        """
                            {
                                $lookup: {
                                    "from": "likes",
                                    "localField": "_id",
                                    "foreignField": "reviewUserId",
                                    "as": "like",
                                    "pipeline": [{
                                        $project: {
                                            "reviewUserId": 1,
                                            "isAdded": {
                                                $cond: {
                                                    if: {
                                                        $eq: ["$userId", ?0]
                                                    },
                                                    then: 1,
                                                    else: 0
                                                }
                                            }
                                        }
                                    }, {
                                                 $group: {
                                                     "_id": "$reviewUserId",
                                                     "count": {
                                                         $count: {}
                                                     },
                                                     "isAdded": {
                                                         $sum: "$isAdded"
                                                     }
                                                 }
                                             }]
                                         }
                                     }
                            """,
        """
                            {
                                $unwind: {
                                    "path": "$like",
                                    "preserveNullAndEmptyArrays": true
                                }
                            }
                            """,
        """
                            {
                                $project: {
                                    "firstName": 1,
                                    "review": 1,
                                    "count": {$ifNull: ["$like.count", 0]},
                                    "isAdded": {$ifNull: ["$like.isAdded", 0]},
                                    "owner": {
                                        $cond: {
                                            if: {
                                                    $eq: ["$_id", ?0]
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
                                $sort: {?1: ?2}
                            }
                            """,
        """
                                  {
                                      $skip: ?3
                                  }
                            """,
        """
                                 {
                                     $limit: 12
                                 }

                            """
      })
  List<ReviewGetDTO> findPageOfReviewWithCountAndIsAddedCheckWithText(
      ObjectId id, String field, int sortType, int skip, String text);

  @Aggregation(
      pipeline = {
        """
                            {
                                "$match": {
                                    "review": {
                                        $exists: true
                                    }
                                }
                            }
                                        """,
        """
                            {
                                $lookup: {
                                    "from": "likes",
                                    "localField": "_id",
                                    "foreignField": "reviewUserId",
                                    "as": "like",
                                    "pipeline": [{
                                        $project: {
                                            "reviewUserId": 1,
                                            "isAdded": {
                                                $cond: {
                                                    if: {
                                                        $eq: ["$userId", ?0]
                                                    },
                                                    then: 1,
                                                    else: 0
                                                }
                                            }
                                        }
                                    }, {
                                                 $group: {
                                                     "_id": "$reviewUserId",
                                                     "count": {
                                                         $count: {}
                                                     },
                                                     "isAdded": {
                                                         $sum: "$isAdded"
                                                     }
                                                 }
                                             }]
                                         }
                                     }
                            """,
        """
                            {
                                $unwind: {
                                    "path": "$like",
                                    "preserveNullAndEmptyArrays": true
                                }
                            }
                            """,
        """
                            {
                                $project: {
                                    "firstName": 1,
                                    "review": 1,
                                    "count": {$ifNull: ["$like.count", 0]},
                                    "isAdded": {$ifNull: ["$like.isAdded", 0]},
                                    "owner": {
                                        $cond: {
                                            if: {
                                                    $eq: ["$_id", ?0]
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
                                $sort: {?1: ?2}
                            }
                            """,
        """
                                  {
                                      $skip: ?3
                                  }
                            """,
        """
                                 {
                                     $limit: 12
                                 }

                            """
      })
  List<ReviewGetDTO> findPageOfReviewWithCountAndIsAddedCheck(
      ObjectId id, String field, int sortType, int skip);

  int countAllByReviewIsNotNull();

  @Aggregation(
      pipeline = {
        """
                            {
                                "$match": {
                                    "_id": ?0,
                                    "review": {
                                        $exists: true
                                    }
                                }
                            }
                                        """,
        """
                            {
                                $lookup: {
                                    "from": "likes",
                                    "localField": "_id",
                                    "foreignField": "reviewUserId",
                                    "as": "like",
                                    "pipeline": [{
                                        $project: {
                                            "reviewUserId": 1,
                                            "isAdded": {
                                                $cond: {
                                                    if: {
                                                        $eq: ["$userId", ?0]
                                                    },
                                                    then: 1,
                                                    else: 0
                                                }
                                            }
                                        }
                                    }, {
                                                 $group: {
                                                     "_id": "$reviewUserId",
                                                     "count": {
                                                         $count: {}
                                                     },
                                                     "isAdded": {
                                                         $sum: "$isAdded"
                                                     }
                                                 }
                                             }]
                                         }
                                     }
                            """,
        """
                            {
                                $unwind: {
                                    "path": "$like",
                                    "preserveNullAndEmptyArrays": true
                                }
                            }
                            """,
        """
                            {
                                $project: {
                                    "firstName": 1,
                                    "review": 1,
                                    "count": {$ifNull: ["$like.count", 0]},
                                    "isAdded": {$ifNull: ["$like.isAdded", 0]}
                                }
                            }
                            """
      })
  ReviewGetDTO findReview(ObjectId objectId);

  @Query(value = "{'_id': ?0}", fields = "{'_id': 0, 'card': 1}")
  User findCardById(ObjectId id);
}
