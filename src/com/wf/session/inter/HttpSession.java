package com.wf.session.inter;

public interface HttpSession {
	/**
	 * 给session赋值
	 * @param key
	 * @param value
	 */
	void setAttribute(Object key , Object value) ;
	/**
	 * 获取session中的对象
	 * @param key
	 * @return
	 */
	Object getAttribute(Object key) ;
	/**
	 * 删除一个session中的值
	 * @param obj
	 */
	void remove(Object obj) ;
	/**
	 * 设置session的最大有效期
	 */
	void setMaxInactiveInterval(Long arg) ;
}
