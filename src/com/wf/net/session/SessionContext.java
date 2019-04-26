package com.wf.net.session;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import com.wf.listener.MyContext;

/**
 * session����������ģʽ�����������ɾ������session�ͱ������session��id
 * @author Administrator
 *
 */
public class SessionContext {
	
	private static SessionContext sessionContext ;
	
	//���������session��id�͹���ʱ�� 
	private Map<String, Long> sessions = new Hashtable<String, Long>() ;
	
	//����session����Ч��
	private Map<String, Long> time = new Hashtable<String, Long>() ;
	
	private SessionContext(){}
	
	public static SessionContext getInstance(){
		if(sessionContext == null){
			sessionContext = new SessionContext() ;
		}
		return sessionContext ;
	}
	
	//���cookie
	public void add(String key , long value){
		sessions.put(key, value) ;
	}
	
	//����session����Ч��
	public void update(String key){
		if(key == null || sessions.get(key) == null){
			return ;
		}
		if(time.get(key) != null){
			sessions.put(key, System.currentTimeMillis()+time.get(key)) ;
		}else{
			sessions.put(key, System.currentTimeMillis()+1000*60*30) ;
		}
	}
	
	//����һ��Ψһ��sessionId
	public String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-" , "") ;
	}
	
	//����session����Ч��
	public void setTime(String key , Long value){
		time.put(key, value) ;
	}
	
	//ɾ��ָ����session
	public void delete(Object obj){
		if(time.get(obj) != null){
			time.remove(obj) ;
		}
		if(sessions.get(obj) != null){
			sessions.remove(obj) ;
		}
	}
	
	/**
	 * ���ҹ��ڵ�session����ɾ��
	 * @return ����ɾ��session����
	 */
	public int delete(Long now){
		int count = 0 ;
		for (Map.Entry<String, Long> entry : sessions.entrySet()) { 
			if(entry.getValue() < now){
				File file = new File(MyContext.getInstance().getAttribute("sessionDir")+"/"+entry.getKey()) ;
				//�жϴ�session�Ƿ����ڱ�����
				if(file.renameTo(file)){
					file.delete() ;
					sessions.remove(entry.getKey()) ;
					count++ ;
				}			
			}else{
				break ;
			}
		}
		return count ;
	}
}
