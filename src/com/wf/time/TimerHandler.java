package com.wf.time;

import java.util.TimerTask;

import com.wf.net.session.SessionContext;
import com.wf.session.inter.HttpSession;

/**
 * �����Ƕ�ʱ���Ĵ����࣬����ɾ�����ڵ�session
 * @author wf
 *
 */
public class TimerHandler extends TimerTask {
	
	//��ȡ��ǰ�̵߳�Session����
	private HttpSession session ;
	
	//��¼��ǰʱ��ĺ�����
	private Long millinute = System.currentTimeMillis() ;
	
	//ÿ������ִ��һ�δ˷���
	private int spaceTime = 1000*10 ;

	@Override
	public void run() {
		//ÿ��ִ�ж�ʱ���õ�ǰʱ������
		millinute += spaceTime ;
		//ɾ�����ڵ�session
		SessionContext.getInstance().delete(millinute) ;
	}
	
	public int getSpaceTime() {
		return spaceTime;
	}
}
