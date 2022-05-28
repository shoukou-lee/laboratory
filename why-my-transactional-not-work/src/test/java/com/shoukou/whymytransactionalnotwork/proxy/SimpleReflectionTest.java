package com.shoukou.whymytransactionalnotwork.proxy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleReflectionTest {

    @Test
    public void invokeLength() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String str = "Lorem Ipsum";

        Method lengthMethod = str.getClass().getMethod("length");
        Object invoke = lengthMethod.invoke(str);

        assertThat(invoke).isEqualTo(str.length());
    }
}
