package com.lzf.spring.formework.aop.aspect;

import com.lzf.spring.formework.aop.interceptor.LZFMethodInterceptor;
import com.lzf.spring.formework.aop.interceptor.LZFMethodInvocation;

import java.lang.reflect.Method;

public class LZFMethodBeforeAdviceInterceptor extends LZFAbstractAspectJAdvice implements LZFAdvice, LZFMethodInterceptor {
    private LZFJoinPoint joinPoint;

    public LZFMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }
    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }
    @Override
    public Object invoke(LZFMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        //调用一遍
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        //为什么又调用了一遍？
        return mi.proceed();
    }
}
