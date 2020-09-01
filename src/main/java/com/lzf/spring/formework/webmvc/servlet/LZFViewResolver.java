package com.lzf.spring.formework.webmvc.servlet;

import java.io.File;

public class LZFViewResolver {
    private File templateRootDir;
    private final String DEFAULT_TEMPLATE_SUFFX = ".html";
    public LZFViewResolver(String templateRoot){
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public LZFView resolveViewName(String viewName) {
        if(viewName==null||"".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX)?viewName:(viewName+DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath()+"/"+viewName).replaceAll("/+","/"));
        return new LZFView(templateFile);
    }
}
