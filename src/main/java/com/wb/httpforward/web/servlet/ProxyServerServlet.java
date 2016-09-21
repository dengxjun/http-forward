//package com.wb.httpforward.web.servlet;
//
//import java.io.IOException;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.util.Assert;
//
//@WebServlet(urlPatterns = { "/proxyServer/*" })
//public class ProxyServerServlet extends HttpServlet {
//
//	@Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//	
//    	String operation_type = request.getParameter("operation_type");
//    	String serverName = request.getParameter("serverName");
//    	Assert.isNull(serverName);
//    	if(operation_type.equals("on")){
//    		
//    	}else if(operation_type.equals("off")){
//    		
//    	}
//	}
//	
//	@Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		doGet(request, response);
//	}
//}
