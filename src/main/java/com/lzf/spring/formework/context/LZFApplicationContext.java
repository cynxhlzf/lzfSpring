package com.lzf.spring.formework.context;

import com.lzf.spring.demo.service.IModifyService;
import com.lzf.spring.demo.service.IQueryService;
import com.lzf.spring.formework.annotation.LZFAutowired;
import com.lzf.spring.formework.annotation.LZFController;
import com.lzf.spring.formework.annotation.LZFRequestMapping;
import com.lzf.spring.formework.annotation.LZFService;
import com.lzf.spring.formework.aop.LZFAopProxy;
import com.lzf.spring.formework.aop.LZFJdkDynamicAopProxy;
import com.lzf.spring.formework.aop.config.LZFAopConfig;
import com.lzf.spring.formework.aop.support.LZFAdvisedSupport;
import com.lzf.spring.formework.beans.LZFBeanDefinition;
import com.lzf.spring.formework.beans.LZFBeanWrapper;
import com.lzf.spring.formework.beans.config.LZFBeanDefinitionReader;
import com.lzf.spring.formework.beans.support.LZFDefaultListableBeanFactory;
import com.lzf.spring.formework.core.LZFBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class LZFApplicationContext extends LZFDefaultListableBeanFactory implements LZFBeanFactory {
    private String [] configLocations ;
    private LZFBeanDefinitionReader reader;
    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    //真正的ioc容器
    private Map<String,LZFBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, LZFBeanWrapper>();
    public LZFApplicationContext(String ... configLocations){
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //定位
        reader = new LZFBeanDefinitionReader(configLocations);
        //扫描
        List<LZFBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //注册
        doRegisterBeanDefinition(beanDefinitions);
        //将需要提前初始化的类初始化（DI操作）
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, LZFBeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            boolean isLazy = entry.getValue().isLazy();
            String beanName = entry.getKey();
            if(isLazy){
                continue;
            }
            try {
                getBean(beanName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doRegisterBeanDefinition(List<LZFBeanDefinition> beanDefinitions) throws Exception{
        for (LZFBeanDefinition beanDefinition : beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getBeanFactoryName())){
                throw new Exception("this"+beanDefinition.getBeanFactoryName()+"is exist!!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getBeanFactoryName(),beanDefinition);
        }
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        Object instance = null;
        instance = instantiateBean(beanName);
        LZFBeanWrapper beanWrapper = new LZFBeanWrapper(instance);
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        populateBean(beanName,new LZFBeanDefinition(),beanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();

    }

    private void populateBean(String beanName, LZFBeanDefinition gpBeanDefinition, LZFBeanWrapper gpBeanWrapper) {
        Class<?> wrapperClass = gpBeanWrapper.getWrappedClass();
        Object wrapperInstance = gpBeanWrapper.getWrappedInstance();
        if(!(wrapperClass.isAnnotationPresent(LZFController.class)||wrapperClass.isAnnotationPresent(LZFService.class))){
            return;
        }
        Field[] fields = wrapperClass.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(LZFAutowired.class)){
                continue;
            }

            String autowired = field.getAnnotation(LZFAutowired.class).value().trim();
            if("".equals(autowired)){
                autowired = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                if(this.factoryBeanInstanceCache.get(autowired) == null){ continue; }
                field.set(wrapperInstance,this.factoryBeanInstanceCache.get(autowired).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName) {
        LZFBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        String className = beanDefinition.getBeanName();
        Object instance = null;
        try {
            if(factoryBeanObjectCache.containsKey(className)){
                instance = factoryBeanObjectCache.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                LZFAdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //符合PointCut的规则的话，创建代理对象
                if(config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                factoryBeanObjectCache.put(className,instance);
                factoryBeanObjectCache.put(beanDefinition.getBeanFactoryName(),instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private LZFAopProxy createProxy(LZFAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new LZFJdkDynamicAopProxy(config);
        }
        return null;
    }

    private LZFAdvisedSupport instantionAopConfig(LZFBeanDefinition beanDefinition) {
        LZFAopConfig config = new LZFAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new LZFAdvisedSupport(config);
    }


    public String [] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig() {
        return reader.getConfig();
    }
}
