package com.shoukou.whymytransactionalnotwork.aop;

import com.shoukou.whymytransactionalnotwork.service.DummyTxStatus;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Aspect
@Slf4j
public class ExecutionTimeAspect {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Around("@annotation(ExecutionTime)")
    public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        TransactionStatus txStatus = TransactionAspectSupport.currentTransactionStatus();
        // TransactionStatus txStatus = new DummyTxStatus();

        log.info("\n======\n 트랜잭션 로깅 \n isActualTransactionActive() : {}\nisNewTransaction() : {}\n=====", TransactionSynchronizationManager.isActualTransactionActive(), txStatus.isNewTransaction());

        log.info("START");
        long start = System.nanoTime();

        Object proceed = joinPoint.proceed();

        long end = System.nanoTime();
        log.info("END");
        log.info("EXECUTION TIME : {}", end - start);

        log.info("\n======\n 트랜잭션 로깅 \n isActualTransactionActive() : {}\nisNewTransaction() : {}\n=====", TransactionSynchronizationManager.isActualTransactionActive(), txStatus.isNewTransaction());

        return proceed;
    }

}
