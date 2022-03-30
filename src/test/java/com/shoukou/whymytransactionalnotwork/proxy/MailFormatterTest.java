package com.shoukou.whymytransactionalnotwork.proxy;

import org.junit.jupiter.api.Test;

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

}
