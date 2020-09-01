package com.lzf.spring.demo.action;


import com.lzf.spring.demo.service.IModifyService;
import com.lzf.spring.demo.service.IQueryService;
import com.lzf.spring.formework.annotation.LZFAutowired;
import com.lzf.spring.formework.annotation.LZFController;
import com.lzf.spring.formework.annotation.LZFRequestMapping;
import com.lzf.spring.formework.annotation.LZFRequestParam;
import com.lzf.spring.formework.webmvc.servlet.LZFModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import com.gupaoedu.vip.spring.formework.webmvc.servlet.GPModelAndView;

/**
 * 公布接口url
 * @author Tom
 *
 */
@LZFController
@LZFRequestMapping("/web")
public class MyAction {

	@LZFAutowired
	IQueryService queryService;
	@LZFAutowired
	IModifyService modifyService;

	@LZFRequestMapping("/query.json")
	public LZFModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                @LZFRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}

	@LZFRequestMapping("/add*.json")
	public LZFModelAndView add(HttpServletRequest request, HttpServletResponse response,
                              @LZFRequestParam("name") String name, @LZFRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
//			return new LZFModelAndView("500",model);
			return null;
		}

	}

	@LZFRequestMapping("/remove.json")
	public LZFModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                 @LZFRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}

	@LZFRequestMapping("/edit.json")
	public LZFModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                               @LZFRequestParam("id") Integer id,
                               @LZFRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}



	private LZFModelAndView out(HttpServletResponse resp, String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
