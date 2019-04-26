package com.wf.inter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public interface HttpServletResponse {
	/**
	 * ���÷���ǰ�˱���
	 **/
	void setCharactor(String charset) ;
	/**
	 * ��ȡ�����io����
	 * @return
	 */
	OutputStream getOutputStream() ;
	/**
	 * ��ȡ�ַ�������
	 * @throws UnsupportedEncodingException 
	 */
	PrintStream getWriter() throws UnsupportedEncodingException ;
	/**
	 * ���÷�����������
	 * @throws Exception 
	 */
	void setContentTyoe(String type) throws Exception ;
	/**
	 * �����ض����ַ
	 * @param url
	 */
	void sendRedirect(String url) ;
	/**
	 * ��ȡcookie����
	 * @return
	 */
	HttpCookie getCookie() ;
}
