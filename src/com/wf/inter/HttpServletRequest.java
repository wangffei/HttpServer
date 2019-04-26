package com.wf.inter;

import java.util.Map;

import com.wf.listener.MyContext;
import com.wf.session.inter.HttpSession;


public interface HttpServletRequest {
	/**
	 * ��ȡ�������
	 * @param key �������
	 * @return ���������ֵ
	 */
	String getParameter(String key) ;
	/**
	 * �����������
	 * @param charset ��������
	 */
	void setCharacter(String charset) ;
	/**
	 * ��ȡ����ͷ����
	 * @param key
	 * @return
	 */
	String getHeadField(String key) ;
	/**
	 * ��ȡ����ip
	 */
	String getRequestIP() ;
	
	/**
	 * ��ȡ�����������
	 * @return
	 */
	Map<String, String> getParameters() ;
	/**
	 * �õ���������������
	 */
	MyContext getContext() ;
	/**
	 * ��ȡ������session
	 * @return
	 */
	HttpSession getSession() ;
	/**
	 * ��ȡcookie����
	 */
	HttpCookie getCookie() ;
}
