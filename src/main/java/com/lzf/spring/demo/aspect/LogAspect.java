package com.lzf.spring.demo.aspect;

import com.lzf.spring.formework.aop.aspect.LZFJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogAspect {
    public void before(LZFJoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_"+joinPoint.getMethod().getName(),System.currentTimeMillis());
        log.info("Invoker Before Method!!!"+"\nTargetObject:"+joinPoint.getThis()
                +"\n参数是："+ Arrays.toString(joinPoint.getArguments())+"喊声爸爸听听");
    }
    //在调用一个方法之后，执行after方法
    public void after(LZFJoinPoint joinPoint){
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    public void afterThrowing(LZFJoinPoint joinPoint, Throwable ex){
        log.info("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }
}
