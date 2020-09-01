package com.lzf.spring.formework.beans.support;

import com.lzf.spring.formework.beans.LZFBeanDefinition;
import com.lzf.spring.formework.context.LZFAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LZFDefaultListableBeanFactory extends LZFAbstractApplicationContext {
    //伪ioc容器
    protected final Map<String, LZFBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, LZFBeanDefinition>();
}

