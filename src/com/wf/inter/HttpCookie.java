package com.wf.inter;

public interface HttpCookie {
	/**
	 * 给cookie添加字段(返回cookie)
	 * @param key
	 * @param value
	 */
	void set(String key , String value) ;
	/**
	 * 获取cookie字段(请求cookie)
	 * @param key
	 * @return
	 */
	String get(String key) ;
}
