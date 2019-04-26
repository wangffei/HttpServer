package com.wf.net.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.wf.inter.HttpCookie;
import com.wf.listener.MyContext;
import com.wf.session.inter.HttpSession;

/**
 * session处理类
 * @author Administrator
 *
 */
public class Session implements HttpSession {
	
	//保存HttpCookie对象 （因为Session的实现依赖于Cookie）
	private HttpCookie cookie ;
	
	//获取session文件的io对象
	private ObjectInputStream obi ;
	
	//保存读取到的SessionNode对象
	private SessionNode session ;
	
	//保存访问者sessionid
	private String sessionId ;

	/**
	 * 构造方法
	 */
	public Session(HttpCookie cookie){
		this.cookie = cookie ;
	}

	public synchronized void setAttribute(Object key, Object value) {
		//获取cookie中的sessionid
		if(sessionId == null){
			sessionId = cookie.get("SESSIONID") ;
		}
		SessionContext temp = SessionContext.getInstance() ;
		if("".equals(sessionId) || sessionId == null){
			//如果该用户是第一次使用session就创建一个session
			sessionId = temp.getUUID() ;
			//将此sessionId写入cookie返回给前端
			cookie.set("SESSIONID", sessionId);
			temp.add(sessionId , System.currentTimeMillis()+1000*60*30);
			session = new SessionNode() ;
		}else{
			if(session == null){
				File f = new File(MyContext.getInstance().getAttribute("sessionDir")+File.separator+sessionId) ;
				if(f.exists()){
					try {
						if(obi == null){
							obi = new ObjectInputStream(new FileInputStream(f)) ;
						}
						session = (SessionNode) obi.readObject() ;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					session = new SessionNode() ;
				}
			}
		}
		session.setAttribute(key, value);
	}

	public Object getAttribute(Object key) {
		//获取cookie中的sessionid
		if(sessionId == null){
			sessionId = cookie.get("SESSIONID") ;
		}
		if("".equals(sessionId) || sessionId == null){
			return null ;
		}else{
			if(session == null){
				File f = new File(MyContext.getInstance().getAttribute("sessionDir")+File.separator+sessionId) ;
				if(f.exists()){
					try {
						if(obi == null){
							obi = new ObjectInputStream(new FileInputStream(f)) ;
						}
						session = (SessionNode)obi.readObject() ;
						return session.getAttribute(key) ;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					return null ;
				}
			}else{
				return session.getAttribute(key) ;
			}
		}
		return null;
	}

	public void remove(Object obj) {
		//获取cookie中的sessionid
		if(sessionId == null){
			sessionId = cookie.get("SESSIONID") ;
		}
		if("".equals(sessionId) || sessionId == null){
			return ;
		}else{
			if(session == null){
				File f = new File(MyContext.getInstance().getAttribute("sessionDir")+File.separator+sessionId) ;
				if(f.exists()){
					try {
						if(obi == null){
							obi = new ObjectInputStream(new FileInputStream(f)) ;
						}
						session = (SessionNode)obi.readObject() ;
						session.remove(obj) ;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					SessionContext.getInstance().delete(obj);
					return ;
				}
			}else{
				session.remove(obj) ;
			}
		}
	}	
	
	public SessionNode getSession() {
		return session;
	}
	
	//将用户session写入磁盘
	public void write() throws Exception{
		if(obi != null){
			obi.close();
		}
		if(session != null){
			if(!"".equals(sessionId) & sessionId != null){
				ObjectOutputStream obo = new ObjectOutputStream(new FileOutputStream(MyContext.getInstance().getAttribute("sessionDir")+File.separator+sessionId)) ;
				try{
					obo.writeObject(session);
				}catch(Exception e){
					System.out.println(e);
				}finally{
					obo.close();
				}
			}
		}
	}

	public void setMaxInactiveInterval(Long arg) {
		//获取cookie中的sessionid
		if(sessionId == null){
			sessionId = cookie.get("SESSIONID") ;
		}
		SessionContext temp = SessionContext.getInstance() ;
		if("".equals(sessionId) || sessionId == null){
			//如果该用户是第一次使用session就创建一个session
			sessionId = temp.getUUID() ;
			//将此sessionId写入cookie返回给前端
			cookie.set("SESSIONID", sessionId);
			temp.add(sessionId , System.currentTimeMillis()+arg);
			temp.setTime(sessionId , arg);
			session = new SessionNode() ;
		}else{
			temp.add(sessionId , System.currentTimeMillis()+arg);
			temp.setTime(sessionId , arg);
		}
	}
}
