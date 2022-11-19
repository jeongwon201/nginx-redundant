package com.psc.cloud.standard.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class AopHandler {

    @Around("execution(* com.psc.cloud.standard.controller..*Controller.*(..))")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.nanoTime();
        log.debug("=> " + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName());
        Object result = pjp.proceed();
        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime)/1000000000.0;
        log.debug("<= " + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName() + " / " + elapsedTime + " 초");
        return result;
    }
}
