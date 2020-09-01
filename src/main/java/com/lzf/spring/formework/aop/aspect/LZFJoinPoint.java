package com.lzf.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 切点的抽象，这是aop的基础组成单元
 * 可以理解为某一业务方法的附加信息，可想而知，切点应该包含业务方法本身，实参列表和方法所属的实例对象
 * 还可以在JoinPoint中添加自定义属性
 */
public interface LZFJoinPoint {
    Object getThis();
    Method getMethod();
    Object [] getArguments();
    //？TODO
    void setUserAttribute(String key, Object value);
    Object getUserAttribute(String key);
}
