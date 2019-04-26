package com.servlet;

import com.wf.event.inter.ContextListener;
import com.wf.listener.ContextNode;
import com.wf.zj.ContextListenerRegist;

@ContextListenerRegist
public class MyContextListener implements ContextListener {

	public void onValueChange(ContextNode node) {
		System.out.println(node);
	}

}
