package com.servlet;

import com.wf.inter.HttpServlet;
import com.wf.inter.HttpServletRequest;
import com.wf.inter.HttpServletResponse;
import com.wf.session.inter.HttpSession;
import com.wf.zj.RequestMapper;

@RequestMapper(path="/test.do")
public class TestServlet implements HttpServlet {

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
//		response.setContentTyoe("image/jpeg");
//		BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_BGR) ;
//		Graphics g = img.createGraphics() ;
//		g.setColor(Color.gray);
//		g.fillRect(0, 0, 500, 500);
//		ImageIO.write(img, "png", response.getOutputStream()) ;
		
		HttpSession session = request.getSession() ;
		response.getWriter().print(session.getAttribute("name"));
	}
	
}
