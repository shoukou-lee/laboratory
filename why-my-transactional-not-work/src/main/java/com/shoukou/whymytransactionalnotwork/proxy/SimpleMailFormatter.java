package com.shoukou.whymytransactionalnotwork.proxy;

public class SimpleMailFormatter implements MailFormatter {

    @Override
    public String appendSignature(String content) {
        return content + " - shoukou lee";
    }

    @Override
    public String appendHeader(String content) {
        return "[header] " + content;
    }

}
