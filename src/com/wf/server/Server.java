package com.wf.server;

/******************************************************************************
 *                           对jdk的HttpServer再封装                                               *
 *                           实现一套注解（后端路由表&监听器注册）                         *
 *                           模仿javaweb的回话跟踪（Session）                                *
 *                           此项目的是练习java的设计模式                                        *
 *                           使用到了（单例，适配器，观察者，代理）                       *
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
 * 对jdk自带的HttpServer类进行二次封装
 * @author wf
 */
public class Server {
	/**
	 * 绑定端口
	 */
	private int port = 9999 ;
	/**
	 * servlet所在包名
	 */
	private String servlet = "com.servlet" ;
	/**
	 * 保存访问地址的映射
	 */
	private Map<String , Object> urls = new HashMap() ;
	/**
	 * 保存方法的映射
	 */
	private Map<Object, Map<String, Method>> murls = new HashMap<Object, Map<String,Method>>() ;
	/**
	 * 404页面内容
	 */
	private final String S_404 = "<h1>404找不到页面</h1>" ;
	/**
	 * 构造方法
	 * @param port 绑定端口
	 * @param servlet servlet所在包名
	 */
	public Server(int port , String servlet){
		this.port = port ;
		this.servlet = servlet ;
	}
	/**
	 * 服务器初始化
	 * @throws Exception 
	 */
	public HttpServer init() throws Exception{
		//创建HttpServer服务器
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0) ;
		
		//判断保存session的目录是否存在
		File f = new File(System.getProperty("java.io.tmpdir")+File.separator+"server") ;
		if(!f.exists()){
			f.mkdirs() ;
		}
		//定义session保存路径
		MyContext.getInstance().setAttribute("sessionDir", System.getProperty("java.io.tmpdir")+File.separator+"server");
		
		//给注解类加上
		regist(server) ;
		
		//启动定时器（定时删除session）
		TimerHandler time = new TimerHandler() ;
		new Timer().schedule(time , 500 , time.getSpaceTime());
		
		//启动服务器
		server.start();
		
		return server ;
	}
	/**
	 * 注册实现
	 * @param server 服务器对象
	 * @throws Exception 
	 */
	public void regist(HttpServer server) throws Exception{
		//遍历servlet包将注解中的类加入Map集合
		String packageName = servlet.replaceAll("\\.", "/") ;
		packageName = Thread.currentThread().getContextClassLoader().getResource(packageName) != null ? Thread.currentThread().getContextClassLoader().getResource(packageName).getFile() : null ;
		//扫描包中的类
		if(packageName != null){
			File[] files = new File(packageName).listFiles() ;
			for (File f : files) {
				String className = f.getName().replace(".class", "") ;
				Class c = Class.forName(servlet+"."+className) ;
				doRegist(c);
			}
		}
		
		//创建一个核心处理器
		server.createContext("/", new HttpHandler() {
			//读文件的io对象
			public FileInputStream in = null ;
			//写的io对象
			public OutputStream out = null ;
			
			//处理器代码
			@SuppressWarnings("unused")  //此注解可以忽略编译器警告
			public void handle(HttpExchange exchange) throws IOException {
				//对访问地址进行拆分
                String urlString = exchange.getRequestURI().toString();  
                          
                String url = urlString.contains("?") ? urlString.split("\\?" , 2)[0] : urlString ;   
                
                //实例化请求对象
                Request request = new Request(exchange) ;
                Response response = new Response(exchange) ;
                
                //每次请求服务器都要刷新session的有效期
                SessionContext.getInstance().update(request.getCookie().get("SESSIONID"));
                
                //向Servlet发起请求
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
                
                //请求的是文件
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
			 * 文件的地址
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
				//返回字符串
				StringBuffer buf = new StringBuffer("[") ;
				//获取请求全部参数
				Map<String, String> params = request.getParameters() ;
				//获取参数的建
				Set<String> keys = params.keySet() ;
				//遍历参数
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
	
	//注解处理器
	public void doRegist(Class<?> c) throws Exception{
		//利用反射生成一个对象
		Object o ;
		try{
			o = c.getConstructor().newInstance() ;
		}catch(Exception e){
			return ;
		}
		//判断该对象是否写了注解
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
			throw new RuntimeException("请检查监听器是否实现ContextListener接口") ;
		}
	}
	
	/**
	 * 遍历方法的找到含有RequestMapper的方法反射
	 */
	private void findMethod(Object o){
		Method[] methods = o.getClass().getDeclaredMethods() ;
		Map<String, Method> map = new HashMap<String, Method>() ;
		for (Method m : methods) {
			//允许私有方法被访问
			m.setAccessible(true);
			RequestMapper reMapper = m.getAnnotation(RequestMapper.class) ;
			if(reMapper != null){
				//将实现注解的方法加入murls集合中
				Parameter[] fields = m.getParameters() ;
				if(fields.length != 2){
					throw new RuntimeException("实现注解的方法请传入HttpServletRequest和HttpServletResponse的对象") ;
				}else{
					if(fields[0].getType() != HttpServletRequest.class || fields[1].getType() != HttpServletResponse.class){
						throw new RuntimeException("实现注解的方法请传入HttpServletRequest和HttpServletResponse的对象") ;
					}
				}
				System.err.println("time:"+new Date().toLocaleString()+" path:"+reMapper.path()+" file:"+m +" successful");
				map.put(reMapper.path(), m) ;
			}
		}
		murls.put(o, map) ;
	}
}
