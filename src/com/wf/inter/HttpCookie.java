package com.wf.inter;

public interface HttpCookie {
	/**
	 * ��cookie����ֶ�(����cookie)
	 * @param key
	 * @param value
	 */
	void set(String key , String value) ;
	/**
	 * ��ȡcookie�ֶ�(����cookie)
	 * @param key
	 * @return
	 */
	String get(String key) ;
}
