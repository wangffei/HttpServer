package com.wf.net.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * session结构
 * @author Administrator
 *
 */
public class SessionNode implements Serializable {
	//用户存储值的容器
	private Map<Object, Object> data = new HashMap<Object, Object>() ;
	
	//设置值
	public void setAttribute(Object key , Object value){
		System.out.println(key+"\t"+value);
		data.put(key, value) ;
	}
	
	//取值
	public Object getAttribute(Object key){
		return data.get(key) ;
	}
	
	//删除
	public void remove(Object key){
		data.remove(key) ;
	}
}
