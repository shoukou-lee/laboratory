package com.shoukou.whymytransactionalnotwork.proxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class MailFormatterTest {

    @Test
    void simpleProxyUpperCaseMailFormatterTest() {
        MailFormatter targetFormatter = new SimpleMailFormatter();
        MailFormatter proxyFormatter = new SimpleProxyUpperCaseMailFormatter(targetFormatter);
        String content = "abc";

        String withHeader = proxyFormatter.appendHeader(content);
        assertThat(withHeader).isEqualTo("[HEADER] ABC");

        String withSignature = proxyFormatter.appendSignature(content);
        assertThat(withSignature).isEqualTo("ABC - SHOUKOU LEE");
    }

    @Test
    void dynamicProxyTest() {
        // create dynamic proxy
        MailFormatter proxiedFormatter = (MailFormatter) Proxy.newProxyInstance(
                getClass().getClassLoader(), // 다이나믹 프록시가 정의되는 클래스 로더
                new Class[] {MailFormatter.class}, // 다이나믹 프록시가 구현할 인터페이스 (여러개일 수 있으니 배열로)
                new UpperCaseInvocationHandler(new SimpleMailFormatter()) // 인보케이션 핸들러와 위임할 타겟 오브젝트
        );

        String content = "Lorem ipsum";
        String ret = proxiedFormatter.appendSignature(content);

        assertThat(ret).isEqualTo("LOREM IPSUM - SHOUKOU LEE");
    }

}
