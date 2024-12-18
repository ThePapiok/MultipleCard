package com.thepapiok.multiplecard.aspects;

import com.thepapiok.multiplecard.dto.PromotionDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PromotionAspect {
  @Before(
      "execution(* com.thepapiok.multiplecard.controllers.PromotionController.addPromotion(..))")
  public void removeZlFromPrice(JoinPoint joinPoint) {
    int index;
    PromotionDTO promotionDTO = (PromotionDTO) joinPoint.getArgs()[0];
    promotionDTO.setNewPrice(promotionDTO.getNewPrice().replaceAll("zł ", ""));
    index = promotionDTO.getNewPrice().indexOf("(");
    promotionDTO.setNewPrice(promotionDTO.getNewPrice().substring(0, index));
  }
}
