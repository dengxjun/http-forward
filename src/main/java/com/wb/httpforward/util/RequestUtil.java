package com.wb.httpforward.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by wangbo on 14-12-24.
 */
public class RequestUtil {
    /**
     * 取得客户端ip地址（可能有多个，如：192.168.10.2,192.168.10.1）<br>
     *
     * @param request HttpServletRequest
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
    	// getHeader不区分大小写、getParameter方法区分大小写
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    public static Map<String, Object> getMetaMap(HttpServletRequest request) {
    	Map<String, Object> metaMap = new HashMap<String, Object>();
    	String method = request.getMethod();
    	String queryString = request.getQueryString();
    	String clientIp = RequestUtil.getClientIp(request);
    	
    	metaMap.put("method", method);
    	metaMap.put("queryString", queryString);
    	metaMap.put("clientIp", clientIp);
    	return metaMap;
    }
    
    public static Map<String, Object> getHeaderMap(HttpServletRequest request) {
    	Map<String, Object> headerMap = new HashMap<String, Object>();
    	Enumeration<String> headerNames = request.getHeaderNames();
    	while (headerNames.hasMoreElements()) {
    		String headerName = headerNames.nextElement();
    		headerMap.put(headerName, request.getHeader(headerName));
    	}
    	return headerMap;
    }

	public static Map<String, Object> getParameterMap(HttpServletRequest request) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		Enumeration<String> parameterNames = request.getParameterNames();
    	while (parameterNames.hasMoreElements()) {
    		String parameterName = parameterNames.nextElement();
    		parameterMap.put(parameterName, request.getParameter(parameterName));
    	}
    	if(!parameterNames.hasMoreElements() && request.getMethod().equalsIgnoreCase("POST") && request.getContentType().equals("application/json")){
    		parameterMap = JsonUtils.stringToMap(getInputStreamData(request),parameterMap);
    	}else if(!parameterNames.hasMoreElements() && request.getMethod().equalsIgnoreCase("POST") && request.getContentType().equals("application/xml")){
    		String s = getInputStreamData(request);
    		System.out.println(s);
    	}
    	
    	return parameterMap;
	}
	
	public static String getInputStreamData(HttpServletRequest request) {
		 String acceptjson = "";  
         BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "utf-8"));
			 StringBuffer sb = new StringBuffer("");  
	         String temp;  
	         while ((temp = br.readLine()) != null) {  
			     sb.append(temp);  
			 }
	         br.close();  
	         acceptjson = sb.toString(); 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
        return acceptjson;
	}
}
