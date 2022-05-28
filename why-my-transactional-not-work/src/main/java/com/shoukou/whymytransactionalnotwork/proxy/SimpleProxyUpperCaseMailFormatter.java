package com.shoukou.whymytransactionalnotwork.proxy;

public class SimpleProxyUpperCaseMailFormatter implements MailFormatter {
    private final MailFormatter mailFormatter;

    @Override
    public String appendSignature(String content) {
        return this.mailFormatter.appendSignature(content).toUpperCase();
    }

    @Override
    public String appendHeader(String content) {
        return this.mailFormatter.appendHeader(content).toUpperCase();
    }

    public SimpleProxyUpperCaseMailFormatter(MailFormatter mailFormatter) {
        this.mailFormatter = mailFormatter;
    }

}
