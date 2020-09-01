package com.lzf.spring.formework.webmvc.servlet;

import com.lzf.spring.demo.service.IModifyService;
import com.lzf.spring.demo.service.IQueryService;
import com.lzf.spring.formework.annotation.LZFAutowired;
import com.lzf.spring.formework.annotation.LZFController;
import com.lzf.spring.formework.annotation.LZFRequestMapping;
import com.lzf.spring.formework.context.LZFApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LZFDispatcherServlet extends HttpServlet{
    private LZFApplicationContext context;
    private final String CONFIG_LOCATION = "contextConfigLocation";
    private List<LZFHandlerMapping> handlerMappings = new ArrayList<LZFHandlerMapping>();
    private Map <LZFHandlerMapping,LZFHandlerAdapter> handlerAdapters = new HashMap<LZFHandlerMapping,LZFHandlerAdapter>();
    private List<LZFViewResolver>viewResolvers = new ArrayList<LZFViewResolver>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        LZFHandlerMapping handler = getHandler(req);
        if(null == handler){
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("teacher","飞龙在天");
            LZFModelAndView welcome = new LZFModelAndView("first",model);
            processDispatchResult(req, resp, welcome);
            return;
        }
        LZFHandlerAdapter ha = getHandlerAdapter(handler);
        LZFModelAndView mv = ha.handler(req, resp, handler);
        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LZFModelAndView mv) throws Exception{
        if(null==mv){
            return;
        }
        if(this.viewResolvers.isEmpty()){
            return;
        }
        for (LZFViewResolver viewResolver : this.viewResolvers) {
            LZFView view = viewResolver.resolveViewName(mv.getViewName());
            view.render(mv.getModel(),req,resp);
            return;
        }

    }

    private LZFHandlerAdapter getHandlerAdapter(LZFHandlerMapping handler) {
        if(null == handler){
            return null;
        }
        if(this.handlerAdapters.containsKey(handler)){
            LZFHandlerAdapter ha = this.handlerAdapters.get(handler);
            if(ha.supports(handler)){
                return ha;
            }
        }
        return null;
    }

    private LZFHandlerMapping getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for (LZFHandlerMapping handlerMapping : this.handlerMappings) {
            Pattern pattern = handlerMapping.getPattern();
            Matcher matcher = pattern.matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return handlerMapping;
        }
        return null;

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        context = new LZFApplicationContext(config.getInitParameter(CONFIG_LOCATION));
        initStrategies(context);
    }

    private void initStrategies(LZFApplicationContext context) {
        //初始化处理器映射器
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化视图转换器，必须实现
        initViewResolvers(context);
    }

    private void initViewResolvers(LZFApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new LZFViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(LZFApplicationContext context) {
        if(this.handlerMappings.isEmpty()){
            return;
        }
        for (LZFHandlerMapping handlerMapping : this.handlerMappings) {
            handlerAdapters.put(handlerMapping,new LZFHandlerAdapter());
        }
    }

    private void initHandlerMappings(LZFApplicationContext context) {
        //处理器映射器就是将方法和对象建立起联系   方法
        //最少知道原则
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        try {
            for (String beanDefinitionName : beanDefinitionNames) {
                Object instance = context.getBean(beanDefinitionName);
                Class<?> clazz = instance.getClass();
                if(!clazz.isAnnotationPresent(LZFController.class)){
                    continue;
                }
                String baseUrl = "";
                if(clazz.isAnnotationPresent(LZFRequestMapping.class)){
                    baseUrl = clazz.getAnnotation(LZFRequestMapping.class).value().replaceAll("/+","/");
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if(!method.isAnnotationPresent(LZFRequestMapping.class)){
                        continue;
                    }
                    String value = method.getAnnotation(LZFRequestMapping.class).value();
                    String regex = ("/"+baseUrl+"/"+value.replaceAll("\\*",".*")).replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new LZFHandlerMapping(method,instance,pattern));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
