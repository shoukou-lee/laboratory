package com.shoukou.whymytransactionalnotwork.proxy;

public interface MailFormatter {
    String appendSignature(String content);
    String appendHeader(String content);
}
