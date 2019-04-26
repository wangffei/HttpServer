package com.wf.net.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * session�ṹ
 * @author Administrator
 *
 */
public class SessionNode implements Serializable {
	//�û��洢ֵ������
	private Map<Object, Object> data = new HashMap<Object, Object>() ;
	
	//����ֵ
	public void setAttribute(Object key , Object value){
		System.out.println(key+"\t"+value);
		data.put(key, value) ;
	}
	
	//ȡֵ
	public Object getAttribute(Object key){
		return data.get(key) ;
	}
	
	//ɾ��
	public void remove(Object key){
		data.remove(key) ;
	}
}
