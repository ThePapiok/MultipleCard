package com.thepapiok.multiplecard.aspects;

import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProductAspect {

  private static final String REGEX_ZL = "zł";

  @Before("execution(* com.thepapiok.multiplecard.controllers.ProductController.addProduct(..))")
  public void removeZlFromAmountAndRemoveNullCategoriesAtAddProduct(JoinPoint joinPoint) {
    AddProductDTO addProductDTO = (AddProductDTO) joinPoint.getArgs()[0];
    addProductDTO.setAmount(addProductDTO.getAmount().replaceAll(REGEX_ZL, ""));
    addProductDTO.setCategory(
        (addProductDTO.getCategory().stream().filter(e -> !e.equals("")).toList()));
  }

  @Before("execution(* com.thepapiok.multiplecard.controllers.ProductController.editProduct(..))")
  public void removeZlFromAmountAndRemoveNullCategoriesAtEditProduct(JoinPoint joinPoint) {
    EditProductDTO editProductDTO = (EditProductDTO) joinPoint.getArgs()[0];
    editProductDTO.setAmount(editProductDTO.getAmount().replaceAll(REGEX_ZL, ""));
    editProductDTO.setCategory(
        (editProductDTO.getCategory().stream().filter(e -> !e.equals("")).toList()));
  }
}
