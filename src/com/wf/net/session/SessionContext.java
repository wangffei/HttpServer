package com.wf.net.session;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import com.wf.listener.MyContext;

/**
 * session容器（单例模式）此类仅负责删除过期session和保存存在session的id
 * @author Administrator
 *
 */
public class SessionContext {
	
	private static SessionContext sessionContext ;
	
	//保存产生的session的id和过期时间 
	private Map<String, Long> sessions = new Hashtable<String, Long>() ;
	
	//保存session的有效期
	private Map<String, Long> time = new Hashtable<String, Long>() ;
	
	private SessionContext(){}
	
	public static SessionContext getInstance(){
		if(sessionContext == null){
			sessionContext = new SessionContext() ;
		}
		return sessionContext ;
	}
	
	//添加cookie
	public void add(String key , long value){
		sessions.put(key, value) ;
	}
	
	//更新session的有效期
	public void update(String key){
		if(key == null || sessions.get(key) == null){
			return ;
		}
		if(time.get(key) != null){
			sessions.put(key, System.currentTimeMillis()+time.get(key)) ;
		}else{
			sessions.put(key, System.currentTimeMillis()+1000*60*30) ;
		}
	}
	
	//产生一个唯一的sessionId
	public String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-" , "") ;
	}
	
	//保存session的有效期
	public void setTime(String key , Long value){
		time.put(key, value) ;
	}
	
	//删除指定的session
	public void delete(Object obj){
		if(time.get(obj) != null){
			time.remove(obj) ;
		}
		if(sessions.get(obj) != null){
			sessions.remove(obj) ;
		}
	}
	
	/**
	 * 查找过期的session，并删除
	 * @return 返回删除session个数
	 */
	public int delete(Long now){
		int count = 0 ;
		for (Map.Entry<String, Long> entry : sessions.entrySet()) { 
			if(entry.getValue() < now){
				File file = new File(MyContext.getInstance().getAttribute("sessionDir")+"/"+entry.getKey()) ;
				//判断此session是否正在被操作
				if(file.renameTo(file)){
					file.delete() ;
					sessions.remove(entry.getKey()) ;
					count++ ;
				}			
			}else{
				break ;
			}
		}
		return count ;
	}
}
