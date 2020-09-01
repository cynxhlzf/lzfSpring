package com.lzf.spring.formework.aop.interceptor;

/**
 * 方法拦截器
 * 是aop代码的基本组成单元
 * 有MethodBeforeAdvice,MethodAfterReturningAdvice等子类
 */
public interface LZFMethodInterceptor {
        Object invoke(LZFMethodInvocation mi) throws Throwable;
}
