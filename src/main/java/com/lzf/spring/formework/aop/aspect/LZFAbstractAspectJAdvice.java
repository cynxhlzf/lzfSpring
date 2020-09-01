package com.lzf.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 使用模板设计模式设计
 * 封装拦截器回调的通用逻辑，主要封装反射动态调用方法，其子类只需要控制调用顺序即可
 */
public abstract class LZFAbstractAspectJAdvice implements LZFAdvice{
    private Object aspectTarget;
    private Method aspectMethod;
    public LZFAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }
    //反射动态调用方法  和proceed方法的区别？
    public Object invokeAdviceMethod(LZFJoinPoint joinPoint,Object returnValue,Throwable ex) throws Throwable{
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        if(null == paramTypes || paramTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }else{
            Object [] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i ++) {
                if(paramTypes[i] == LZFJoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramTypes[i] == Throwable.class){
                    args[i] = ex;
                }else if(paramTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);
        }
    }

}
