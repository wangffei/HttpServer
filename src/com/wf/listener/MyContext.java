package com.wf.listener;

import java.util.Hashtable;

/**
 * һ��ȫ�ֵ���������������������
 * @author Administrator
 * ����ģʽ
 */
public class MyContext {
	//����
	private Hashtable<String, Object> table = new Hashtable<String, Object>() ;
	
	private static MyContext context ;
	
	private ContextListenerHandler listener ;
	
	/**
	 * ˽�л����췽��
	 */
	private MyContext(){
		listener = ContextListenerHandler.getInstence() ;
	} 
	
	/**
	 * ��ȡ����
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
