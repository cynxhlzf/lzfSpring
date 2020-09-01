package com.lzf.spring.formework.aop;

/**
 * 代理工厂的顶层接口，提供获取代理对象的顶层入口
 */
public interface LZFAopProxy {
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
