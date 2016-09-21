package com.wb.httpforward.web.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.wb.httpforward.constant.ServerConstant;
import com.wb.httpforward.enums.CodeEnum;
import com.wb.httpforward.service.ForwardHttpService;
import com.wb.httpforward.service.TranspondHttpService;
import com.wb.httpforward.util.CommonMethod;
import com.wb.httpforward.util.RequestMessage;
import com.wb.httpforward.util.RequestUtil;
import com.wb.httpforward.util.ResponseUtil;

/**
 * @author www
 * @date 2015年9月11日
 */

@WebServlet(urlPatterns = { "/transpondRequest/*" }, asyncSupported = true)
public class TranspondRequestServlet extends HttpServlet {

	private static final long serialVersionUID = -7744835458987531055L;
	
	private TranspondHttpService transpondHttpService = TranspondHttpService.getInstance();

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.length() < 1) {
    		ResponseUtil.setCommonHeader(response);
    		ResponseUtil.sendResponse(response, "{\"code\":" + CodeEnum.ERROR.val + ", \"message\":\"wrong uri.\"}");
    		return;
		}
    	String clientServer = null;
    	
    	// 组装请求数据
    	Integer code = CommonMethod.codeGenerator();
    	Map<String, Object> metaMap = RequestUtil.getMetaMap(request); 
    	metaMap.put("path", pathInfo);
		Map<String, Object> headerMap = RequestUtil.getHeaderMap(request); 
		Map<String, Object> parameterMap = RequestUtil.getParameterMap(request);
		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setCode(code);
		requestMessage.setMetaMap(metaMap);
		requestMessage.setHeaderMap(headerMap);
		requestMessage.setParameterMap(parameterMap);
    	String requestMessageString = JSON.toJSONString(requestMessage);
    	
    	System.out.println("-----------"+requestMessageString);
    	
    	if(parameterMap.get("token") != null){
    		String token = (String) parameterMap.get("token");
    		String serverName = token.substring(token.indexOf(".") + 1);
    		
    		// 过滤非法请求
    		if (!transpondHttpService.isCorrectClientServer(serverName)) {
    			ResponseUtil.setCommonHeader(response);
    			ResponseUtil.sendResponse(response, "{\"code\":" + CodeEnum.ERROR.val + ", \"message\":\"wrong uri.\"}");
    			return;
    		}
    		
    		clientServer = transpondHttpService.getClientName(serverName);
    		
    	}else if(pathInfo.indexOf("/login") > -1){
    		clientServer = transpondHttpService.getRandomClientName();
    	}
    	
    	// 过滤非法请求
		if (!transpondHttpService.isCorrectClientServer(clientServer)) {
			ResponseUtil.setCommonHeader(response);
			ResponseUtil.sendResponse(response, "{\"code\":" + CodeEnum.ERROR.val + ", \"message\":\"wrong uri.\"}");
			return;
		}
    	
    	System.out.println("---current clientServer---: "+clientServer);
    	
    	final AsyncContext asyncContext = request.startAsync(request,response);
    	
    	ForwardHttpService fs = new ForwardHttpService(requestMessage, asyncContext,clientServer);
    	asyncContext.start(fs);
    	
    	//asyncContext.addListener(new TranspondRequestAsyncListener(client));
    	asyncContext.setTimeout(ServerConstant.SERVER_HOLD_REQUEST_TIMEOUT);
	}
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}
