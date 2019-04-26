package com.wf.server;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 一个供系统使用的全局容器(单例模式)  *****同一个线程都共享此容器
 * @author Administrator
 * 使用禁忌:不要再多线程中使用，因为此对象中的键是当前线程
 */
public class SystemContext {
	private static SystemContext context ;
	
	private SystemContext(){}
	
	private Hashtable<Thread, Map<String, Object>> table = new Hashtable<Thread, Map<String,Object>>() ; 
	
	public synchronized static SystemContext getInstance(){
		if(context == null){
			context = new SystemContext() ;
		}
		return context ;
	}
	
	public synchronized void setAttribute(String key , Object value){
		if(table.get(Thread.currentThread()) == null){
			table.put(Thread.currentThread(), new HashMap<String, Object>()) ;
		}
		table.get(Thread.currentThread()).put(key, value) ;
	}
	
	public Object get(String key){
		if(table.get(Thread.currentThread()) == null){
			return null ;
		}
		return table.get(Thread.currentThread()).get(key) ;
	}
	
	public synchronized void delete(){
		table.remove(Thread.currentThread()) ;
	}
}
