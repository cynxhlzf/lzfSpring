package com.lzf.spring.formework.aop;

import com.lzf.spring.formework.aop.interceptor.LZFMethodInvocation;
import com.lzf.spring.formework.aop.support.LZFAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class LZFJdkDynamicAopProxy implements LZFAopProxy, InvocationHandler {
    private LZFAdvisedSupport advised;
    public LZFJdkDynamicAopProxy(LZFAdvisedSupport config){
        this.advised = config;
    }
    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        LZFMethodInvocation invocation = new LZFMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
