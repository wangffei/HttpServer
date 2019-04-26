package com.wf.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.com.sun.net.httpserver.Headers;
import org.jboss.com.sun.net.httpserver.HttpExchange;

import com.wf.inter.HttpCookie;

/**
 * cookie的处理器
 * @author Administrator
 *
 */
public class Cookie implements HttpCookie {
	
	//保存链接对象
	private HttpExchange exchange ;
	
	//保存请求Cookie
	private Map<String, String> requestCookie ;
	
	//保存响应cookie
	private Map<String, String> responseCookie ;
	
	public Map<String, String> getResponseCookie() {
		return responseCookie;
	}

	public Cookie(HttpExchange exchange){
		this.exchange = exchange ;
	}
	
	//解析cookie
	public String get(String key){
		if(requestCookie == null){
			cookieFormat();
		}
		return requestCookie.get(key) ;
	}
	
	//设置返回cookie
	public void set(String key , String value){
		if(responseCookie == null){
			responseCookie = new HashMap<String, String>() ;
		}
		responseCookie.put(key, value) ;
	}
	
	public boolean isEmpty(){
		if(responseCookie == null){
			responseCookie = new HashMap<String, String>() ;
		}
		if(responseCookie.isEmpty()){
			return true ;
		}
		return false ;
	}
	
	private void cookieFormat(){
		Headers header = exchange.getRequestHeaders() ;
		List<String> list = header.get("Cookie") ;
		Map<String , String> cookie = new HashMap<String, String>() ;
		for (String s : list) {
			if(s.contains(";")){
				String[] fs = s.split(";") ;
				for (String string : fs) {
					if(string.contains("=")){
						String[] strs = string.split("=" , 2) ;
						cookie.put(strs[0].trim(), strs[1].trim()) ;
					}
				}
			}else{
				if(s.contains("=")){
					String[] strs = s.split("=" , 2) ;
					cookie.put(strs[0], strs[1]) ;
				}
			}
		}
		requestCookie = cookie ;
	}
}
