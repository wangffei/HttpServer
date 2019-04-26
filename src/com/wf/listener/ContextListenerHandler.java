package com.wf.listener;

import java.util.HashSet;
import java.util.Set;

import com.wf.event.inter.ContextListener;
import com.wf.event.inter.Listener;

/**
 * 处理器，观察者
 * @author Administrator
 * 使用单例模式
 */
public class ContextListenerHandler implements Listener {
	
	private static ContextListenerHandler handler ;
	
	/**
	 * 事件保存
	 */
	Set<ContextListener> listeners = new HashSet<ContextListener>() ;
	
	/**
	 * 私有化构造方法
	 */
	private ContextListenerHandler(){}
	
	/**
	 * 获取对象
	 */
	public synchronized static ContextListenerHandler getInstence(){
		if(handler == null){
			handler = new ContextListenerHandler() ;
		}
		return handler ;
	}
	
	public void update(ContextNode node) {
		for(ContextListener c : listeners){
			c.onValueChange(node);
		}
	}
	
	public void regist(ContextListener context) {
		listeners.add(context) ;
	}
}
