package com.wf.server;

/******************************************************************************
 *                           ��jdk��HttpServer�ٷ�װ                                               *
 *                           ʵ��һ��ע�⣨���·�ɱ�&������ע�ᣩ                         *
 *                           ģ��javaweb�Ļػ����٣�Session��                                *
 *                           ����Ŀ������ϰjava�����ģʽ                                        *
 *                           ʹ�õ��ˣ����������������۲��ߣ�����                       *
 ******************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.jboss.com.sun.net.httpserver.HttpHandler;
import org.jboss.com.sun.net.httpserver.HttpServer;

import com.wf.event.inter.ContextListener;
import com.wf.inter.HttpServletRequest;
import com.wf.inter.HttpServletResponse;
import com.wf.listener.ContextListenerHandler;
import com.wf.listener.MyContext;
import com.wf.net.session.Session;
import com.wf.net.session.SessionContext;
import com.wf.time.TimerHandler;
import com.wf.zj.ContextListenerRegist;
import com.wf.zj.RequestMapper;

/**
 * ��jdk�Դ���HttpServer����ж��η�װ
 * @author wf
 */
public class Server {
	/**
	 * �󶨶˿�
	 */
	private int port = 9999 ;
	/**
	 * servlet���ڰ���
	 */
	private String servlet = "com.servlet" ;
	/**
	 * ������ʵ�ַ��ӳ��
	 */
	private Map<String , Object> urls = new HashMap() ;
	/**
	 * ���淽����ӳ��
	 */
	private Map<Object, Map<String, Method>> murls = new HashMap<Object, Map<String,Method>>() ;
	/**
	 * 404ҳ������
	 */
	private final String S_404 = "<h1>404�Ҳ���ҳ��</h1>" ;
	/**
	 * ���췽��
	 * @param port �󶨶˿�
	 * @param servlet servlet���ڰ���
	 */
	public Server(int port , String servlet){
		this.port = port ;
		this.servlet = servlet ;
	}
	/**
	 * ��������ʼ��
	 * @throws Exception 
	 */
	public HttpServer init() throws Exception{
		//����HttpServer������
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0) ;
		
		//�жϱ���session��Ŀ¼�Ƿ����
		File f = new File(System.getProperty("java.io.tmpdir")+File.separator+"server") ;
		if(!f.exists()){
			f.mkdirs() ;
		}
		//����session����·��
		MyContext.getInstance().setAttribute("sessionDir", System.getProperty("java.io.tmpdir")+File.separator+"server");
		
		//��ע�������
		regist(server) ;
		
		//������ʱ������ʱɾ��session��
		TimerHandler time = new TimerHandler() ;
		new Timer().schedule(time , 500 , time.getSpaceTime());
		
		//����������
		server.start();
		
		return server ;
	}
	/**
	 * ע��ʵ��
	 * @param server ����������
	 * @throws Exception 
	 */
	public void regist(HttpServer server) throws Exception{
		//����servlet����ע���е������Map����
		String packageName = servlet.replaceAll("\\.", "/") ;
		packageName = Thread.currentThread().getContextClassLoader().getResource(packageName) != null ? Thread.currentThread().getContextClassLoader().getResource(packageName).getFile() : null ;
		//ɨ����е���
		if(packageName != null){
			File[] files = new File(packageName).listFiles() ;
			for (File f : files) {
				String className = f.getName().replace(".class", "") ;
				Class c = Class.forName(servlet+"."+className) ;
				doRegist(c);
			}
		}
		
		//����һ�����Ĵ�����
		server.createContext("/", new HttpHandler() {
			//���ļ���io����
			public FileInputStream in = null ;
			//д��io����
			public OutputStream out = null ;
			
			//����������
			@SuppressWarnings("unused")  //��ע����Ժ��Ա���������
			public void handle(HttpExchange exchange) throws IOException {
				//�Է��ʵ�ַ���в��
                String urlString = exchange.getRequestURI().toString();  
                          
                String url = urlString.contains("?") ? urlString.split("\\?" , 2)[0] : urlString ;   
                
                //ʵ�����������
                Request request = new Request(exchange) ;
                Response response = new Response(exchange) ;
                
                //ÿ�������������Ҫˢ��session����Ч��
                SessionContext.getInstance().update(request.getCookie().get("SESSIONID"));
                
                //��Servlet��������
                if(urls.get(url) != null){
                	try {
                		String method = request.getParameter("method") ;
						if(method != null && murls.get(urls.get(url)) != null && murls.get(urls.get(url)).get(method) != null){	
						//	System.err.println("time:"+new Date().toLocaleString()+" IP:"+request.getRequestIP()+" path:"+url+" params:"+paramepFormat(request));
							murls.get(urls.get(url)).get(method).invoke(urls.get(url), request , response) ;
							return ;
						}else{
							Method m = urls.get(url).getClass().getDeclaredMethod("service", HttpServletRequest.class , HttpServletResponse.class) ;
							m.setAccessible(true);
							if(m != null){
							//	System.err.println("time:"+new Date().toLocaleString()+" IP:"+request.getRequestIP()+" path:"+url+" params:"+paramepFormat(request));
								m.invoke(urls.get(url), request , response) ;
								return ;
							}else{
							//	System.err.println("ERROR === time:"+new Date().toLocaleString()+" IP:"+request.getRequestIP()+" path:"+url+" params:"+paramepFormat(request));
								response.response(404 , S_404) ;
								return ;
							}
						}
					} catch (Exception e) {
						response.response(500 , e.toString()) ;
					} finally{
						if(exchange.getResponseBody() == null){
							return ;
						}
						try {
							Method m = response.getClass().getDeclaredMethod("flush") ;
							m.setAccessible(true);
							m.invoke(response) ;
						} catch (Exception e) {} 
						if(exchange.getResponseBody() != null){
							exchange.getResponseBody().close();
						}
						if(SystemContext.getInstance().get("session") != null){
							try {
								((Session)SystemContext.getInstance().get("session")).write();
							} catch (Exception e) {
								e.printStackTrace();
							} 
						}
						SystemContext.getInstance().delete(); 
					}
                	return ;
                }
                
                //��������ļ�
                try {
					writeHtml(exchange , url) ;
				//	System.err.println("time:"+new Date().toLocaleString()+" IP:"+request.getRequestIP()+" file:"+url);
				} catch (Exception e) {
				//	System.err.println("ERROR === time:"+new Date().toLocaleString()+" IP:"+request.getRequestIP()+" file:"+url);
					//System.out.println(e);
					response.response(404 , S_404) ;
				} finally{
					if(this.in != null){
						this.in.close();
					}
					if(exchange.getResponseBody() != null){
						exchange.getResponseBody().close();
					}
					if(SystemContext.getInstance().get("session") != null){
						try {
							((Session)SystemContext.getInstance().get("session")).write();
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
					SystemContext.getInstance().delete(); 
				}
			}
			
			/**
			 * �ļ��ĵ�ַ
			 * @param path
			 * @throws Exception 
			 * @throws Exception 
			 */
			public void writeHtml(HttpExchange exchange , String path) throws Exception{
				String filePath = "./WebRoot"+path ;
				this.in = new FileInputStream(filePath);
				this.out = exchange.getResponseBody() ;
				exchange.sendResponseHeaders(200, new File(filePath).length());
				byte[] data = new byte[1024] ;
				int len = -1 ;
				while((len = in.read(data)) != -1){
					out.write(data, 0, len);
				}
			}
			
			public String paramepFormat(HttpServletRequest request){
				//�����ַ���
				StringBuffer buf = new StringBuffer("[") ;
				//��ȡ����ȫ������
				Map<String, String> params = request.getParameters() ;
				//��ȡ�����Ľ�
				Set<String> keys = params.keySet() ;
				//��������
				for (String k : keys) {
					buf.append(k+" => "+ params.get(k)+" ,") ;
				}
				if(buf.length() == 1){
					buf.append("]") ;
				}else{
					buf = buf.replace(buf.length()-1, buf.length(), "]") ;
				}
				return buf.toString() ;
			}
		}) ;
	}
	
	//ע�⴦����
	public void doRegist(Class<?> c) throws Exception{
		//���÷�������һ������
		Object o ;
		try{
			o = c.getConstructor().newInstance() ;
		}catch(Exception e){
			return ;
		}
		//�жϸö����Ƿ�д��ע��
		RequestMapper reMapper = o.getClass().getAnnotation(RequestMapper.class) ;
		if(reMapper != null){
			System.err.println("time:"+new Date().toLocaleString()+" path:"+reMapper.path()+" file:"+c +" successful");
			urls.put(reMapper.path() , o) ;
			findMethod(o) ;
		}
		
		ContextListenerRegist regist = o.getClass().getAnnotation(ContextListenerRegist.class) ;
		if(regist != null){
			Class<?>[] inters = o.getClass().getInterfaces() ;
			for (Class<?> ic : inters) {
				if(ic == ContextListener.class){
					ContextListenerHandler.getInstence().regist((ContextListener)o);
					return ;
				}
			}
			throw new RuntimeException("����������Ƿ�ʵ��ContextListener�ӿ�") ;
		}
	}
	
	/**
	 * �����������ҵ�����RequestMapper�ķ�������
	 */
	private void findMethod(Object o){
		Method[] methods = o.getClass().getDeclaredMethods() ;
		Map<String, Method> map = new HashMap<String, Method>() ;
		for (Method m : methods) {
			//����˽�з���������
			m.setAccessible(true);
			RequestMapper reMapper = m.getAnnotation(RequestMapper.class) ;
			if(reMapper != null){
				//��ʵ��ע��ķ�������murls������
				Parameter[] fields = m.getParameters() ;
				if(fields.length != 2){
					throw new RuntimeException("ʵ��ע��ķ����봫��HttpServletRequest��HttpServletResponse�Ķ���") ;
				}else{
					if(fields[0].getType() != HttpServletRequest.class || fields[1].getType() != HttpServletResponse.class){
						throw new RuntimeException("ʵ��ע��ķ����봫��HttpServletRequest��HttpServletResponse�Ķ���") ;
					}
				}
				System.err.println("time:"+new Date().toLocaleString()+" path:"+reMapper.path()+" file:"+m +" successful");
				map.put(reMapper.path(), m) ;
			}
		}
		murls.put(o, map) ;
	}
}
