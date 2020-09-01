package com.lzf.spring.formework.webmvc.servlet;

import java.util.Map;

public class LZFModelAndView {
    private String viewName;
    private Map<String,?> model;
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }


    public  LZFModelAndView(String viewName){
        this.viewName = viewName;
    }
    public LZFModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
