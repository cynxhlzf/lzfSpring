package com.lzf.spring.formework.aop.support;

import com.lzf.spring.formework.aop.aspect.LZFMethodBeforeAdviceInterceptor;
import com.lzf.spring.formework.aop.config.LZFAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主要是用于对aop配置的解析
 */
public class LZFAdvisedSupport {
    private Class <?> targetClass;
    private Object target;
    private Pattern pointCutClassPattern;
    private LZFAopConfig config;
    private transient Map<Method, List<Object>> methodCache;
    public LZFAdvisedSupport(LZFAopConfig config){
        this.config = config;
    }

    /**
     * 根据aop配置，将需要回调的方法封装成一个拦截器链并返回提供给外部获取
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method,Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        if(null == cached){
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached =methodCache.get(m);
            this.methodCache.put(m,cached);
        }
        return cached;
    }
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
//        public .* com\.lzf\.spring\.demo\.service\..*Service\..*\(.*\)
        System.out.println(pointCut);
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        //玩正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));
        System.out.println(pointCutClassPattern);
        try {
            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            //logAspect方法
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }
            //目标方法
            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每一个方法包装成 MethodIterceptor
                    //before
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        //创建一个Advivce
                        advices.add(new LZFMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    //after
//                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
//                        //创建一个Advivce
//                        advices.add(new GPAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
//                    }
//                    //afterThrowing
//                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
//                        //创建一个Advivce
//                        GPAfterThrowingAdviceInterceptor throwingAdvice =
//                                new GPAfterThrowingAdviceInterceptor(
//                                        aspectMethods.get(config.getAspectAfterThrow()),
//                                        aspectClass.newInstance());
//                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
//                        advices.add(throwingAdvice);
//                    }
                    methodCache.put(m,advices);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //用于判断目标类时候符合切面规则，从而决定是否要生成代理类，对目标方法进行增强
    public boolean pointCutMatch(){
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Pattern getPointCutClassPattern() {
        return pointCutClassPattern;
    }

    public void setPointCutClassPattern(Pattern pointCutClassPattern) {
        this.pointCutClassPattern = pointCutClassPattern;
    }

    public LZFAopConfig getConfig() {
        return config;
    }

    public void setConfig(LZFAopConfig config) {
        this.config = config;
    }

    public Map<Method, List<Object>> getMethodCache() {
        return methodCache;
    }

    public void setMethodCache(Map<Method, List<Object>> methodCache) {
        this.methodCache = methodCache;
    }

}
