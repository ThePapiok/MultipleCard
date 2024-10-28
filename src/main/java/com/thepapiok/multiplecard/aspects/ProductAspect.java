package com.thepapiok.multiplecard.aspects;

import com.thepapiok.multiplecard.dto.AddProductDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProductAspect {

  @Before("execution(* com.thepapiok.multiplecard.controllers.ProductController.addProduct(..))")
  public void removeZlFromAmount(JoinPoint joinPoint) {
    AddProductDTO addProductDTO = (AddProductDTO) joinPoint.getArgs()[0];
    addProductDTO.setAmount(addProductDTO.getAmount().replaceAll("zł", ""));
    addProductDTO.setCategory(
        (addProductDTO.getCategory().stream().filter(e -> !e.equals("")).toList()));
  }
}
