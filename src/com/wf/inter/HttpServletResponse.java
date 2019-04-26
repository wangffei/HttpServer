package com.wf.inter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public interface HttpServletResponse {
	/**
	 * 设置返回前端编码
	 **/
	void setCharactor(String charset) ;
	/**
	 * 获取浏览器io对象
	 * @return
	 */
	OutputStream getOutputStream() ;
	/**
	 * 获取字符流对象
	 * @throws UnsupportedEncodingException 
	 */
	PrintStream getWriter() throws UnsupportedEncodingException ;
	/**
	 * 设置返回数据类型
	 * @throws Exception 
	 */
	void setContentTyoe(String type) throws Exception ;
	/**
	 * 设置重定向地址
	 * @param url
	 */
	void sendRedirect(String url) ;
	/**
	 * 获取cookie对象
	 * @return
	 */
	HttpCookie getCookie() ;
}
