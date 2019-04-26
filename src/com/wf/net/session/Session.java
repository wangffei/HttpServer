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
 * session������
 * @author Administrator
 *
 */
public class Session implements HttpSession {
	
	//����HttpCookie���� ����ΪSession��ʵ��������Cookie��
	private HttpCookie cookie ;
	
	//��ȡsession�ļ���io����
	private ObjectInputStream obi ;
	
	//�����ȡ����SessionNode����
	private SessionNode session ;
	
	//���������sessionid
	private String sessionId ;

	/**
	 * ���췽��
	 */
	public Session(HttpCookie cookie){
		this.cookie = cookie ;
	}

	public synchronized void setAttribute(Object key, Object value) {
		//��ȡcookie�е�sessionid
		if(sessionId == null){
			sessionId = cookie.get("SESSIONID") ;
		}
		SessionContext temp = SessionContext.getInstance() ;
		if("".equals(sessionId) || sessionId == null){
			//������û��ǵ�һ��ʹ��session�ʹ���һ��session
			sessionId = temp.getUUID() ;
			//����sessionIdд��cookie���ظ�ǰ��
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
		//��ȡcookie�е�sessionid
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
		//��ȡcookie�е�sessionid
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
	
	//���û�sessionд�����
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
		//��ȡcookie�е�sessionid
		if(sessionId == null){
			sessionId = cookie.get("SESSIONID") ;
		}
		SessionContext temp = SessionContext.getInstance() ;
		if("".equals(sessionId) || sessionId == null){
			//������û��ǵ�һ��ʹ��session�ʹ���һ��session
			sessionId = temp.getUUID() ;
			//����sessionIdд��cookie���ظ�ǰ��
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
