package com.lzf.spring.formework.beans.config;

import com.lzf.spring.formework.beans.LZFBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LZFBeanDefinitionReader {
    private Properties config = new Properties();
    private final String SCAN_PACKAGE = "scanPackage";
    private List<String> registyClass = new ArrayList<String>();
    public LZFBeanDefinitionReader(String ... configLocations){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
//        scanPackage=com.lzf.spring.demo
        URL url = this.getClass().getResource("/"+scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage+"."+file.getName());
            }else{
                if(!file.getName().endsWith(".class")){
                    continue;
                }
                String className = scanPackage+"."+file.getName().replace(".class","");
                this.registyClass.add(className);
            }
        }

    }

    public List<LZFBeanDefinition> loadBeanDefinitions() {
        List<LZFBeanDefinition> beanDefinitions = new ArrayList<LZFBeanDefinition>();
        if(registyClass.isEmpty()){
            return null;
        }
        try {
            for (String className : this.registyClass) {
                Class<?> clazz = Class.forName(className);
                if(clazz.isInterface()){
                    continue;
                }
                beanDefinitions.add(doCreateBeanDefinition(toLowerFirstCase(clazz.getSimpleName()),clazz.getName()));
                Class<?> [] interfaces = clazz.getInterfaces();
                for (Class<?> i : interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    beanDefinitions.add(doCreateBeanDefinition(i.getName(),clazz.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return beanDefinitions;
    }

    private LZFBeanDefinition doCreateBeanDefinition(String factoryName,String beanName) {
        LZFBeanDefinition beanDefinition = new LZFBeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setBeanFactoryName(factoryName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return this.config;
    }
}
