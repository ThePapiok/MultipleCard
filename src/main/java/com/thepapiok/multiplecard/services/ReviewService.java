package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.misc.ReviewConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.LikeRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
  private final ReviewConverter reviewConverter;
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final LikeRepository likeRepository;

  @Autowired
  public ReviewService(
      ReviewConverter reviewConverter,
      AccountRepository accountRepository,
      UserRepository userRepository,
      LikeRepository likeRepository) {
    this.reviewConverter = reviewConverter;
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.likeRepository = likeRepository;
  }

  @Transactional
  public boolean addReview(ReviewDTO reviewDTO, String phone) {
    try {
      Review review = reviewConverter.getEntity(reviewDTO);
      review.setCreatedAt(LocalDateTime.now());
      String id = accountRepository.findIdByPhone(phone).getId();
      Optional<User> user = userRepository.findById(id);
      if (user.isEmpty()) {
        return false;
      }
      ObjectId objectId = new ObjectId(id);
      Optional<Like> like = likeRepository.findByReviewUserId(objectId);
      if (like.isPresent()) {
        likeRepository.deleteAllByReviewUserId(objectId);
      }
      User notEmptyUser = user.get();
      notEmptyUser.setReview(review);
      userRepository.save(notEmptyUser);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean addLike(String id, String phone) {
    try {
      List<ObjectId> objectIds = getObjectId(phone, id);
      if (objectIds == null) {
        return false;
      }
      if (!atLike(id)) {
        return false;
      }
      if (likeRepository
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

  public boolean deleteLike(String id, String phone) {
    try {
      List<ObjectId> objectIds = getObjectId(phone, id);
      if (objectIds == null) {
        return false;
      }
      if (!atLike(id)) {
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

  private boolean atLike(String id) {
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

  private List<ObjectId> getObjectId(String phone, String id) {
    ObjectId userId = new ObjectId(accountRepository.findIdByPhone(phone).getId());
    if (userId.toHexString().length() == 0) {
      return null;
    }
    ObjectId reviewUserId = new ObjectId(id);
    if (reviewUserId.toHexString().length() == 0) {
      return null;
    }
    return List.of(reviewUserId, userId);
  }

  public boolean removeReview(String id, String phone) {
    try {
      String userId = accountRepository.findIdByPhone(phone).getId();
      Optional<User> optionalUser = userRepository.findById(id);
      if (optionalUser.isEmpty()) {
        return false;
      }
      if (!userId.equals(id)) {
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
}
