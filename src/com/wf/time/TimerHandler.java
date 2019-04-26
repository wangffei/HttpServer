package com.wf.time;

import java.util.TimerTask;

import com.wf.net.session.SessionContext;
import com.wf.session.inter.HttpSession;

/**
 * 此类是定时器的处理类，用于删除过期的session
 * @author wf
 *
 */
public class TimerHandler extends TimerTask {
	
	//获取当前线程的Session对象
	private HttpSession session ;
	
	//记录当前时间的毫秒数
	private Long millinute = System.currentTimeMillis() ;
	
	//每多少秒执行一次此方法
	private int spaceTime = 1000*10 ;

	@Override
	public void run() {
		//每次执行定时器让当前时间增加
		millinute += spaceTime ;
		//删除过期的session
		SessionContext.getInstance().delete(millinute) ;
	}
	
	public int getSpaceTime() {
		return spaceTime;
	}
}
