package com.wf.event.inter;

import com.wf.listener.ContextNode;


/**
 * ������ִ�з���
 * @author Administrator
 *
 */
public interface Listener {
	/**
	 * �������и���ʱִ�з���
	 */
	void update(ContextNode node) ;
	/**
	 * �ص�����
	 * @param context
	 */
	void regist(ContextListener context) ;
}
