package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

  private final PromotionRepository promotionRepository;

  public PromotionService(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  public Promotion getPromotion(String id) {
    return promotionRepository.findByProductId(new ObjectId(id));
  }
}
