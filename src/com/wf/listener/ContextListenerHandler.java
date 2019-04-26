package com.wf.listener;

import java.util.HashSet;
import java.util.Set;

import com.wf.event.inter.ContextListener;
import com.wf.event.inter.Listener;

/**
 * ���������۲���
 * @author Administrator
 * ʹ�õ���ģʽ
 */
public class ContextListenerHandler implements Listener {
	
	private static ContextListenerHandler handler ;
	
	/**
	 * �¼�����
	 */
	Set<ContextListener> listeners = new HashSet<ContextListener>() ;
	
	/**
	 * ˽�л����췽��
	 */
	private ContextListenerHandler(){}
	
	/**
	 * ��ȡ����
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
