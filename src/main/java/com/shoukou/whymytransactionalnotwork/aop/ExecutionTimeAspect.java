package com.shoukou.whymytransactionalnotwork.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

@Component
@Aspect
@Slf4j
public class ExecutionTimeAspect {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Around("@annotation(ExecutionTime)")
    public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        TransactionStatus txStatus = transactionManager.getTransaction(null);
        System.out.println("Aspect.isNewTransaction() = " + txStatus.isNewTransaction());

        log.info("START");
        long start = System.nanoTime();

        Object proceed = joinPoint.proceed();

        long end = System.nanoTime();
        log.info("END");
        log.info("EXECUTION TIME : {}", end - start);

        return proceed;
    }

}
