package com.lzf.spring.formework.core;

public interface LZFBeanFactory {
    Object getBean(Class<?> beanClass) throws Exception;
    Object getBean(String beanName) throws Exception;
}
