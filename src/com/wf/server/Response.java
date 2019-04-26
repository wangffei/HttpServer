package com.wf.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import org.jboss.com.sun.net.httpserver.Headers;
import org.jboss.com.sun.net.httpserver.HttpExchange;

import com.wf.inter.HttpCookie;
import com.wf.inter.HttpServletResponse;

public class Response implements HttpServletResponse {
	//保存浏览器访问对象
	private HttpExchange exchange ;
	
	/**
	 * 保存返回前端编码
	 */
	
	private String charset = "utf-8" ; 
	
	/**
	 * 定义缓冲区大小
	 */
	
	private final int SIZE = 1024*1024 ;
	
	/**
	 * 定义一个缓冲区(采用静态代理)
	 */
	private MyByteArrayOutputStream byteBuf = new MyByteArrayOutputStream(SIZE) ;
	
	/**
	 * 请求头是否已经返回
	 */
	
	private boolean isSendHead = false ;
	
	/**
	 * 返回头
	 */
	
	private Headers header ;
	
	/**
	 * 重定向地址
	 */
	
	private String reUrl = null ;
	
	/**
	 * 保存cookie的对象
	 */
	private Cookie cookie ;
	
	/**
	 * @param exchange
	 */
	public Response(HttpExchange exchange) {
		this.exchange = exchange ;
		//初始化headers对象
		header = exchange.getResponseHeaders() ;
		//默认以text/html返回
		header.add("Content-Type", "text/html");
		//实例化cookie对象
		if(SystemContext.getInstance().get("cookie") == null){
			SystemContext.getInstance().setAttribute("cookie", new Cookie(exchange));
		}
		this.cookie = (Cookie)SystemContext.getInstance().get("cookie") ;
	}
	
	public void setCharactor(String charset) {
		this.charset = charset ;
	}

	public OutputStream getOutputStream() {
		return byteBuf ;
	}

	public PrintStream getWriter() throws UnsupportedEncodingException {
		return new PrintStream(byteBuf , true , charset);
	}

	public void setContentTyoe(String type) throws Exception {
		if(isSendHead){
			throw new Exception("header already send !!!") ;
		}
		if(header.containsKey("Content-Type")){
			header.set("Content-Type", type);
			return ;
		}
		header.add("Content-Type", type);
	}

	public void sendRedirect(String url) {
		reUrl = url ;
	}
	
	public String getReUrl(){
		return reUrl ;
	}
	
	public HttpCookie getCookie() {
		return this.cookie;
	}
	
	/**
	 * 以下方法全部设置为私有方法供反射调用
	 */
	private synchronized void flush(){
		try {
			if(!isSendHead){
				setCookie() ;
				exchange.sendResponseHeaders(200 , 0);
				isSendHead = true ;
			}
			exchange.getResponseBody().write(byteBuf.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setCookie(){
		if(!cookie.isEmpty()){
			if(header.get("Set-Cookie") == null){
				header.add("Set-Cookie", cookieFormat(cookie.getResponseCookie()));
			}else{
				header.set("Set-Cookie", cookieFormat(cookie.getResponseCookie()));
			}
		}
	}
	
	private String cookieFormat(Map<String, String> map){
		Set<String> set = map.keySet() ;
		StringBuffer buf = new StringBuffer() ;
		for (String s : set) {
			buf.append(s+"="+map.get(s)+";") ;
		}
		return buf.replace(buf.length()-1, buf.length(), "").toString() ;
	}
	
	/**
	 * 直接返回
	 */
	protected synchronized void response(int code , String data){
		try {
			if(!isSendHead){
				setCookie() ;
				Headers head = exchange.getResponseHeaders();
				head.add("Content-Type", "text/html;charset="+charset);
				exchange.sendResponseHeaders(code, data.getBytes(charset).length);
				isSendHead = true ;
			}
			exchange.getResponseBody().write(data.getBytes(charset));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 定义一个自己的字节流对象
	 * @author Administrator
	 *
	 */
	class MyByteArrayOutputStream extends ByteArrayOutputStream {
		public MyByteArrayOutputStream(int size){
			super(size) ;
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			if(this.size() + 1 >= SIZE){
				if(!isSendHead){
					Response.this.setCookie() ;
					exchange.sendResponseHeaders(202, 0);
					isSendHead = true ;
				}
				exchange.getResponseBody().write(byteBuf.toByteArray());
				this.reset();
			}
			super.write(b);
		}
		
		@Override
		public synchronized void write(byte[] b, int off, int len) {
			if(this.size() + len >= SIZE){
				try {
					if(!isSendHead){
						Response.this.setCookie() ;
						exchange.sendResponseHeaders(202, 0);
						isSendHead = true ;
					}
					exchange.getResponseBody().write(byteBuf.toByteArray());
					this.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			super.write(b, off, len);
		}
		
		@Override
		public synchronized void write(int b) {
			if(this.size() + 1 >= SIZE){
				try {
					if(!isSendHead){
						Response.this.setCookie() ;
						exchange.sendResponseHeaders(202, 0);
						isSendHead = true ;
					}
					exchange.getResponseBody().write(byteBuf.toByteArray());
					this.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			super.write(b);
		}
	}
}
