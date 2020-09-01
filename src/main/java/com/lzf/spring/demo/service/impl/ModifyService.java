package com.lzf.spring.demo.service.impl;

import com.lzf.spring.demo.service.IModifyService;
import com.lzf.spring.formework.annotation.LZFService;

/**
 * 增删改业务
 * @author Tom
 *
 */
@LZFService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception {
		throw new Exception("这是Tom老师故意抛的异常！！");
		//return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
