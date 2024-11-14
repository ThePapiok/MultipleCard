package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.PromotionDTO;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.util.Locale;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromotionConverter {
  private final PromotionRepository promotionRepository;

  @Autowired
  public PromotionConverter(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  public PromotionDTO getDTO(Promotion promotion) {
    final double centsPerZl = 100.0;
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount(String.format(Locale.US, "%.2f", promotion.getAmount() / centsPerZl));
    if (promotion.getCount() == 0) {
      promotionDTO.setCount("");
    } else {
      promotionDTO.setCount(String.valueOf(promotion.getCount()));
    }
    promotionDTO.setStartAt(promotion.getStartAt());
    promotionDTO.setExpiredAt(promotion.getExpiredAt());
    promotionDTO.setProductId(promotion.getProductId().toString());
    return promotionDTO;
  }

  public Promotion getEntity(PromotionDTO promotionDTO) {
    final int centsPerZl = 100;
    final ObjectId productId = new ObjectId(promotionDTO.getProductId());
    Promotion promotion = promotionRepository.findByProductId(productId);
    if (promotion == null) {
      promotion = new Promotion();
    }
    promotion.setProductId(productId);
    promotion.setStartAt(promotionDTO.getStartAt());
    promotion.setExpiredAt(promotionDTO.getExpiredAt());
    if ("".equals(promotionDTO.getCount())) {
      promotion.setCount(0);
    } else {
      promotion.setCount(Integer.parseInt(promotionDTO.getCount()));
    }
    promotion.setAmount((int) (Double.parseDouble(promotionDTO.getAmount()) * centsPerZl));
    return promotion;
  }
}
