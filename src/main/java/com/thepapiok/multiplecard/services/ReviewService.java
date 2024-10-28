package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.misc.ReviewConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.LikeRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ReviewService {
  private final ReviewConverter reviewConverter;
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final LikeRepository likeRepository;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;

  @Autowired
  public ReviewService(
      ReviewConverter reviewConverter,
      AccountRepository accountRepository,
      UserRepository userRepository,
      LikeRepository likeRepository,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate) {
    this.reviewConverter = reviewConverter;
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.likeRepository = likeRepository;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
  }

  public boolean addReview(ReviewDTO reviewDTO, String phone) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              Review review = reviewConverter.getEntity(reviewDTO);
              review.setCreatedAt(LocalDateTime.now());
              ObjectId id = accountRepository.findIdByPhone(phone).getId();
              Optional<User> user = userRepository.findById(id);
              if (user.isEmpty()) {
                throw new RuntimeException();
              }
              Optional<Like> like = likeRepository.findByReviewUserId(id);
              if (like.isPresent()) {
                likeRepository.deleteAllByReviewUserId(id);
              }
              User notEmptyUser = user.get();
              notEmptyUser.setReview(review);
              userRepository.save(notEmptyUser);
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean addLike(ObjectId id, String phone) {
    try {
      List<ObjectId> objectIds = getObjectId(phone, id);
      if (objectIds == null) {
        return false;
      } else if (!atLike(id)) {
        return false;
      } else if (likeRepository
          .findByReviewUserIdAndUserId(objectIds.get(0), objectIds.get(1))
          .isPresent()) {
        return false;
      }
      Like like = new Like();
      like.setReviewUserId(objectIds.get(0));
      like.setUserId(objectIds.get(1));
      likeRepository.save(like);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean deleteLike(ObjectId id, String phone) {
    try {
      List<ObjectId> objectIds = getObjectId(phone, id);
      if (objectIds == null) {
        return false;
      } else if (!atLike(id)) {
        return false;
      }
      Optional<Like> like =
          likeRepository.findByReviewUserIdAndUserId(objectIds.get(0), objectIds.get(1));
      if (like.isEmpty()) {
        return false;
      }
      likeRepository.delete(like.get());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean atLike(ObjectId id) {
    try {
      Optional<Account> reviewUser = accountRepository.findById(id);
      if (reviewUser.isEmpty()) {
        return false;
      }
      Optional<User> user = userRepository.findById(reviewUser.get().getId());
      return user.isPresent() && user.get().getReview() != null;
    } catch (Exception e) {
      return false;
    }
  }

  private List<ObjectId> getObjectId(String phone, ObjectId id) {
    ObjectId userId = accountRepository.findIdByPhone(phone).getId();
    if (userId.toHexString().length() == 0) {
      return null;
    } else if (id.toHexString().length() == 0) {
      return null;
    }
    return List.of(id, userId);
  }

  public boolean removeReview(ObjectId id, String phone) {
    try {
      ObjectId userId = accountRepository.findIdByPhone(phone).getId();
      Optional<User> optionalUser = userRepository.findById(id);
      if (optionalUser.isEmpty()) {
        return false;
      } else if (!userId.equals(id)) {
        return false;
      }
      User user = optionalUser.get();
      user.setReview(null);
      userRepository.save(user);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public List<ReviewGetDTO> getReviewsFirst3(String phone) {
    final int maxSize = 3;
    List<ReviewGetDTO> reviewGetDTOS = getReviews(phone, 0, "count", true, "");
    return reviewGetDTOS.subList(0, Math.min(maxSize, reviewGetDTOS.size()));
  }

  public List<ReviewGetDTO> getReviews(
      String phone, int page, String field, boolean isDescending, String text) {
    try {
      int sort;
      final int countReviewsAtPage = 12;
      if (isDescending) {
        sort = -1;
      } else {
        sort = 1;
      }
      ObjectId objectId = null;
      if (phone != null) {
        objectId = accountRepository.findIdByPhone(phone).getId();
      }
      if ("".equals(text)) {
        return userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            objectId, field, sort, page * countReviewsAtPage);
      } else {
        return userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            objectId, field, sort, page * countReviewsAtPage, text);
      }
    } catch (Exception e) {
      return List.of();
    }
  }

  public ReviewGetDTO getReview(String phone) {
    try {
      ObjectId objectId = accountRepository.findIdByPhone(phone).getId();
      return userRepository.findReview(objectId);
    } catch (Exception e) {
      return null;
    }
  }

  public int getMaxPage() {
    final float countReviewsAtPage = 12.0F;
    int count = userRepository.countAllByReviewIsNotNull();
    return (int) Math.ceil(count / countReviewsAtPage);
  }
}
