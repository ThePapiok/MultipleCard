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

  @Before("execution(* com.thepapiok.multiplecard.controllers.ProductController.addProduct(..))")
  public void removeZlFromPriceAndRemoveNullCategoriesAtAddProduct(JoinPoint joinPoint) {
    AddProductDTO addProductDTO = (AddProductDTO) joinPoint.getArgs()[0];
    addProductDTO.setPrice(addProductDTO.getPrice().replaceAll("zł", ""));
    addProductDTO.setCategory(
        (addProductDTO.getCategory().stream().filter(e -> !e.equals("")).toList()));
  }

  @Before("execution(* com.thepapiok.multiplecard.controllers.ProductController.editProduct(..))")
  public void removeZlFromPriceAndRemoveNullCategoriesAtEditProduct(JoinPoint joinPoint) {
    EditProductDTO editProductDTO = (EditProductDTO) joinPoint.getArgs()[0];
    editProductDTO.setPrice(editProductDTO.getPrice().replaceAll("zł", ""));
    editProductDTO.setCategory(
        (editProductDTO.getCategory().stream().filter(e -> !e.equals("")).toList()));
  }
}
