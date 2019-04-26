package com.wf.event.inter;

import com.wf.listener.ContextNode;


/**
 * 监听者执行方法
 * @author Administrator
 *
 */
public interface Listener {
	/**
	 * 当数据有跟新时执行方法
	 */
	void update(ContextNode node) ;
	/**
	 * 回掉方法
	 * @param context
	 */
	void regist(ContextListener context) ;
}
