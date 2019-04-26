package com.servlet;

import com.wf.inter.HttpCookie;
import com.wf.inter.HttpServlet;
import com.wf.inter.HttpServletRequest;
import com.wf.inter.HttpServletResponse;
import com.wf.zj.RequestMapper;

@RequestMapper(path="/person.do")
public class PersonServlet implements HttpServlet {

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpCookie cookie = request.getCookie() ;
		cookie.set("language", "java");
		for(int i = 0 ; i < 1024*1024 ; i++){
			response.getWriter().println(request.getContext().getAttribute("name"));
		}
		System.out.println(cookie.get("name"));
	}

}
