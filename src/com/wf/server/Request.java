package com.wf.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.jboss.com.sun.net.httpserver.Headers;
import org.jboss.com.sun.net.httpserver.HttpExchange;

import com.wf.inter.HttpCookie;
import com.wf.inter.HttpServletRequest;
import com.wf.listener.MyContext;
import com.wf.net.session.Session;
import com.wf.session.inter.HttpSession;

/**
 * ����������ԭ��exange�����ֳ�Request��Response��������
 * @author wf
 *
 */
public class Request implements HttpServletRequest {
	//����һ��Exange����
	private HttpExchange exchange ;
	
	/**
	 * �����ַ�������
	 */
	private String charset = "utf-8" ;
	
	/**
	 * �����������
	 */
	private Map<String, String> query = null ;
	
	/**
	 * ���������ַ���
	 */
	private String paramesString = "" ;
	
	/**
	 * ���浱ǰ���ʶ����cookie
	 */
	private HttpCookie cookie ;
	
	/**
	 * ���췽��
	 * @param exchange ÿ�������exange����
	 */
	public Request(HttpExchange exchange){
		this.exchange = exchange ;
	}

	public String getParameter(String key) {
		if(query == null){
			query = queryFormat() ;
		}
		return query.get(key);
	}
	
	public Map<String, String> getParameters() {
		if(query == null){
			query = queryFormat() ;
		}
		return query;
	}

	public void setCharacter(String charset) {
		this.charset = charset ;
		query = null ;
		queryFormat() ;
	}

	public String getHeadField(String key) {
		Headers header = exchange.getRequestHeaders() ;
		return (header.get(key) != null ? header.get(key).toString() : null); 
	}
	

	public String getRequestIP() {
		return exchange.getRemoteAddress().getHostName();
	}
	
	public MyContext getContext() {
		return MyContext.getInstance() ;
	}
	
	public HttpSession getSession() {
		if(SystemContext.getInstance().get("session") == null){
			SystemContext.getInstance().setAttribute("session", new Session(this.getCookie()));
		}
		return (HttpSession)SystemContext.getInstance().get("session") ;
	}
	
	public HttpCookie getCookie() {
		if(SystemContext.getInstance().get("cookie") == null){
			SystemContext.getInstance().setAttribute("cookie", new Cookie(exchange));
		}
		return (Cookie)SystemContext.getInstance().get("cookie") ;
	}
	
	/**
	 * ����������
	 */
	private Map<String, String> queryFormat(){
		if(paramesString == null || "".equals(paramesString)){
			paramesString = queryGet() ;
		}
        return getQuerys() ;
	}
	
	/**
	 * ����������
	 */
	private String queryGet(){
		//��ȡ����ĵ�ַ
		String urlString = exchange.getRequestURI().toString() ;
		
		//�õ����������
		OutputStream out = null ;
		
		BufferedReader read = new BufferedReader(new InputStreamReader(exchange.getRequestBody())) ;
        String line = "" ;
        urlString = urlString.contains("?") ? urlString+"&" : urlString + "?" ;
        
        //��ȡPOst�������
        try{
        	while((line = read.readLine()) != null){
            	urlString += line ;
            }
        }catch(Exception e){
        	System.out.println(e);
			int len = e.toString().getBytes().length ;
			exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
			try {
				exchange.sendResponseHeaders(500, len);
				out = exchange.getResponseBody() ;
				out.write(e.toString().getBytes());
			} catch (Exception e2) {
				System.out.println(e);
			} finally{
				if(out != null){
					try {
						out.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
        }finally{
        	if(read != null){
        		try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        //��ֲ��������ַ���
        String parames = "";
        if (urlString.contains("?")) {
        	String[] strs = urlString.split("\\?");
            if(strs.length > 1){
            	parames = strs[1];
            }
        }
        return parames ;
	}
	
	/**
	 * ����������
	 * @param parames
	 * @return
	 */
	private Map<String, String> getQuerys() {
		Map<String , String> map = new HashMap() ;
        if (!paramesString.equals("")) {
            String[] strs = null ;
            if(!paramesString.contains("&")){
                strs = new String[]{paramesString};
            }else{
                strs = paramesString.split("&");
            }
            for (String string : strs) {
                if (string.contains("=")) {
                	String[] strs1 = string.split("=") ;
                    if(strs1.length > 1){
                    	String k = strs1[0];
                        String v = strs1[1];
                        try {
                        	v = URLDecoder.decode(v, charset) ;
							map.put(k, v);
						} catch (UnsupportedEncodingException e) {
							map.put(k, v);
						}
                    }
                }
            }
        }
        return map;
	}
}
