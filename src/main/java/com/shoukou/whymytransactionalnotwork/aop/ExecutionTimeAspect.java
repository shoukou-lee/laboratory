package com.shoukou.whymytransactionalnotwork.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(ExecutionTime)")
    public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("START");
        long start = System.nanoTime();

        Object proceed = joinPoint.proceed();

        long end = System.nanoTime();
        log.info("END");
        log.info("EXECUTION TIME : {}", end - start);

        return proceed;
    }

}
