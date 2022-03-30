package com.shoukou.whymytransactionalnotwork.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 다이나믹 프록시로브터 메서드 호출 정보를 받아 invoke 하는 핸들러
 * 스트링을 Upper case로 변환하는 부가 기능, 위임 역할
 */
public class UpperCaseInvocationHandler implements InvocationHandler {
    Object target;

    public UpperCaseInvocationHandler(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args); // 타겟으로 위임 혹은 또 다른 인터페이스 메서드 호출
        if (ret instanceof String) {
            return ((String)ret).toUpperCase(); // invoke 결과가 스트링이면 부가기능 적용 (toUpperCase)
        } else {
            return ret; // 아니면 그냥 리턴
        }
    }
}
