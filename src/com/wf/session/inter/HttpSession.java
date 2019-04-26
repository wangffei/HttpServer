package com.wf.session.inter;

public interface HttpSession {
	/**
	 * ��session��ֵ
	 * @param key
	 * @param value
	 */
	void setAttribute(Object key , Object value) ;
	/**
	 * ��ȡsession�еĶ���
	 * @param key
	 * @return
	 */
	Object getAttribute(Object key) ;
	/**
	 * ɾ��һ��session�е�ֵ
	 * @param obj
	 */
	void remove(Object obj) ;
	/**
	 * ����session�������Ч��
	 */
	void setMaxInactiveInterval(Long arg) ;
}
