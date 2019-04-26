package com.servlet;

import java.util.UUID;

import com.wf.inter.HttpCookie;
import com.wf.inter.HttpServlet;
import com.wf.inter.HttpServletRequest;
import com.wf.inter.HttpServletResponse;
import com.wf.session.inter.HttpSession;
import com.wf.zj.RequestMapper;

@RequestMapper(path = "/user.do")
public class UserServlet implements HttpServlet {

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession session = request.getSession() ;
		session.setMaxInactiveInterval(1000*15L);
		session.setAttribute("name", "wf");
		
		request.getContext().setAttribute("name", "wf");
		//request.getContext().setAttribute("say", "hello");
		response.setContentTyoe("text/html;utf-8");
		HttpCookie cookie = request.getCookie() ;
		cookie.set("userId", UUID.randomUUID().toString()) ;
		cookie.set("goodsname", "iphone") ;
		response.getWriter().print("<meta charset='utf-8' />cookie…Ë÷√≥…π¶£¨name=wf") ;
		//response.getWriter().print("Hello World");
	}
	
}
