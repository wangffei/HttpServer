package com.wf.server;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * һ����ϵͳʹ�õ�ȫ������(����ģʽ)  *****ͬһ���̶߳����������
 * @author Administrator
 * ʹ�ý���:��Ҫ�ٶ��߳���ʹ�ã���Ϊ�˶����еļ��ǵ�ǰ�߳�
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
