package com.wf.listener;

import java.util.Hashtable;

/**
 * 一个全局的容器，整个服务器共享
 * @author Administrator
 * 单例模式
 */
public class MyContext {
	//容器
	private Hashtable<String, Object> table = new Hashtable<String, Object>() ;
	
	private static MyContext context ;
	
	private ContextListenerHandler listener ;
	
	/**
	 * 私有化构造方法
	 */
	private MyContext(){
		listener = ContextListenerHandler.getInstence() ;
	} 
	
	/**
	 * 获取对象
	 */
	public synchronized static MyContext getInstance(){
		if(context == null){
			context = new MyContext() ;
		}
		return context ;
	}
	
	public void setAttribute(String key , Object value){
		ContextNode node = new ContextNode(key, value) ;
		listener.update(node);
		table.put(key, value) ;
	}
	
	public Object getAttribute(String key){
		return table.get(key) ;
	}
	
	public void delete(String key){
		if(table.get(key) == null){
			return ;
		}
		table.remove(key) ;
	}
}
