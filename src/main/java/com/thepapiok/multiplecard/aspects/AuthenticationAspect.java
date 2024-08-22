package com.thepapiok.multiplecard.aspects;

import com.thepapiok.multiplecard.dto.RegisterDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthenticationAspect {

  @Before(
      "execution(* com.thepapiok.multiplecard.controllers.AuthenticationController.createUser(..))")
  public void removeWhiteSpaceRegister(JoinPoint joinPoint) {
    RegisterDTO registerDTO = (RegisterDTO) joinPoint.getArgs()[0];
    registerDTO.setPhone(registerDTO.getPhone().replaceAll(" ", ""));
  }

  @Around("execution(* com.thepapiok.multiplecard.services.UserService.loadUserByUsername(..))")
  public Object removeWhiteSpaceLogin(ProceedingJoinPoint joinPoint) throws Throwable {
    Object[] args = joinPoint.getArgs();
    String phone = (String) args[0];
    args[0] = phone.replaceAll(" ", "");
    return joinPoint.proceed(args);
  }
}
