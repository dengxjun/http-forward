package com.wb.httpforward.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wb.httpforward.constant.ClientConstant;
import com.wb.httpforward.client.ClientHolder;
import com.wb.httpforward.util.Base64Util;
import com.wb.httpforward.util.HttpClientUtil;
import com.wb.httpforward.util.RequestMessage;
import com.wb.httpforward.util.ResponseMessage;
import com.wb.httpforward.util.ResponseUtil;

/**
 * @author www
 * @date 2015年9月11日
 */

public class ForwardHttpService implements Runnable{
	private RequestMessage requestMessage;
	
	private AsyncContext asyncContext;
	
	private String serverKey;
	
	private static final Logger log = LoggerFactory.getLogger(ForwardHttpService.class);
	
	public ForwardHttpService(RequestMessage requestMessage,AsyncContext asyncContext,String serverKey) {
		this.requestMessage = requestMessage;
		this.asyncContext = asyncContext;
		this.serverKey = serverKey;
	}
	
	
	private static int transpondRequestCount = 0;
	
	private static int transpondResponseCount = 0;
	

	/**
	 * 请求转发方法的简单实现
	 * @param clientNo
	 * @param responseValue
	 */
//	public void transpondHttpRequest(final String clientNo, final String requestMessageString) {
//		log.info(++transpondRequestCount + " server transpond request : " + requestMessageString);
//    	// 用开启新线程的方式简单实现异步，以后再改成线程池
//		new Thread(new Runnable() {
//			public void run() {
//				TransponderClient client = null;
//				// 尝试发送10次，这里简单实现下，当有多个消息积压的时候，会开启多个线程，会失去这些消息的顺序，而且50秒后客户端未接收消息，就会丢失。
//				// 更健壮的实现应该使用任务队列
//				for (int i = 0; i < 10; i++) {
//					//client = ClientHolder.getInstance().getTransponderClient(clientNo);
//					if (client == null) {
//						try {
//							TimeUnit.SECONDS.sleep(5); // 没获取到的话休眠5秒再重试
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						if (i == 9) {
//							log.error("no transponder client with clientNo: " + clientNo);
//						}
//						continue;
//					}
//					client.responseValue(requestMessageString);
//					break;
//				}
//			}
//		}).start();
//	}
	
	@Override
	public void run() {
		log.info(++transpondRequestCount + " transpond request : " + JSON.toJSONString(requestMessage));
		Integer code = requestMessage.getCode();
		Map<String, Object> metaMap = requestMessage.getMetaMap();
		Map<String, Object> headerMap = requestMessage.getHeaderMap();
		Map<String, Object> parameterMap = requestMessage.getParameterMap();
		
		String method = metaMap.get("method").toString();
		String queryString = metaMap.get("queryString") == null ? null : metaMap.get("queryString").toString();
		String path = metaMap.get("path").toString();
		String headerContentType = (String) (headerMap.get("Content-Type") == null ? headerMap.get("content-type") : "applicaton/json");
		
		String targetServerUrl = ClientHolder.getInstance().getProxyServer(serverKey).getBaseUrl() + path;
		
		HttpRequestBase httpRequest = null;
		if (method.equalsIgnoreCase("POST")) {
			HttpPost httpPost = new HttpPost(targetServerUrl);
			if(headerContentType.equals("application/json")){
				 JSONObject jsonParam = new JSONObject();  
				 for (String parameterName: parameterMap.keySet()) {
					 jsonParam.put(parameterName, String.valueOf(parameterMap.get(parameterName))); 
				 } 
				 
				 StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题    
		         entity.setContentEncoding("UTF-8");    
		         entity.setContentType("application/json"); 
		         httpPost.setEntity(entity);
			}else if(headerContentType.equals("application/xml")){
				String xmlString = "<loginCriteria> <groupId>RNDTEST6</groupId> <userId>admin</userId> <password>admin</password> <deviceType>web</deviceType> <deviceId>EF113400FB930E40022445248F606B21</deviceId></loginCriteria>";
				 StringEntity entity = new StringEntity(xmlString,"utf-8");
		         entity.setContentEncoding("UTF-8");    
		         entity.setContentType("application/xml"); 
		         httpPost.setEntity(entity);
			}else{
				List<NameValuePair> params = new ArrayList<NameValuePair>();  
				for (String parameterName: parameterMap.keySet()) {
					params.add(new BasicNameValuePair(parameterName, String.valueOf(parameterMap.get(parameterName)))); 
				} 
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);  
				httpPost.setEntity(entity);
			}
			httpRequest = httpPost;
		} else {
			httpRequest = new HttpGet(targetServerUrl 
					+ (queryString == null ? "" :"?" + queryString));
		}
		/*for (String headerName: headerMap.keySet()) {
			if (!headerName.equalsIgnoreCase("Content-Length") && !headerName.equalsIgnoreCase("Host")) {
				httpRequest.addHeader(headerName, String.valueOf(headerMap.get(headerName)));
			}
		}
		httpRequest.addHeader("X-Forwarded-For", clientIp);*/
		
		CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpRequest);
			System.out.println(response);
			int statusCode = response.getStatusLine().getStatusCode();
			Map<String, Object> responseHeaderMap = HttpClientUtil.getResponseHeaderMap(response);
			List<String> cookieList = HttpClientUtil.getResponseCookieList(response);
			boolean isBase64 = false;
			String bodyString = null;
			String contentType = response.getFirstHeader("Content-Type") == null 
					? null : response.getFirstHeader("Content-Type").getValue();
			if (contentType != null && (contentType.contains("text")
					|| contentType.contains("json") || contentType.contains("javascript"))) {
				bodyString = HttpClientUtil.getBodyStringFromResponse(response);
			} else {
				isBase64 = true;
				byte[] bodyByteArray = HttpClientUtil.getBodyByteArrayFromResponse(response);
				bodyString = Base64Util.encode(bodyByteArray);
			}
			
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.setCode(code);
			responseMessage.setStatusCode(statusCode);
			responseMessage.setHeaderMap(responseHeaderMap);
			responseMessage.setCookieList(cookieList);
			responseMessage.setBody(bodyString);
			responseMessage.setBase64(isBase64);
			String responseMessageString = JSON.toJSONString(responseMessage);
			
			if (ClientConstant.LOG_WITH_BODY || responseMessage.getBody() == null) {
				log.info(++transpondResponseCount + " transpond response: " + responseMessageString);
			} else {
				log.info(++transpondResponseCount + " transpond response: " + responseMessage.logStringWithNoBody());
			}
			
			response(responseMessage);
			
		} catch (ClientProtocolException e) {
			log.error("transpord request error: ", e);
		} catch (IOException e) {
			log.error("transpord request error: ", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					log.error("transpord request error: ", e);
				}
			}
		}
	}
	
	
	public void response(ResponseMessage responseMessage) {
		try {
			HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
			if (response.isCommitted()) {
	            //表示PrintWriter的close方法已经调用，所有数据已经从缓冲区刷新到客户端。
	            // response.isCommitted()返回true；
	            return;
	        }
			int statusCode = responseMessage.getStatusCode();
			response.setStatus(statusCode);
			Map<String, Object> headerMap = responseMessage.getHeaderMap();
			List<String> cookieList = responseMessage.getCookieList();
			String body = responseMessage.getBody();
			for (String headerName: headerMap.keySet()) {
			//	if (!headerName.equalsIgnoreCase("Content-Length")) {
					response.setHeader(headerName, String.valueOf(headerMap.get(headerName)));
			//	}
			}
			for (String cookieValue: cookieList) {
				// addHeader()会增加，setHeader()会覆盖，
				// 此处如果有多个cookie值，使用setHeader()方法会覆盖成只有最有一个cookie，所以用addHeader()
				response.addHeader("Set-Cookie", cookieValue);
			}
			if (body != null) {
				if (responseMessage.isBase64()) {
					ResponseUtil.sendResponse(response, Base64Util.decode(body));
				} else {
					ResponseUtil.sendResponse(response, body);
				}
			}
			asyncContext.complete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
//			remove(); // 发送这个异常表示response已返回过，需要remove掉。
		}
	}
	
	
	
	/*public synchronized String getRandomClientNo(){
		String clientNo = "";
		int size = ClientHolder.getInstance().getClientServerMapSize();
		Set<String> keys = ClientHolder.getInstance().getClientServerMapKeys();
		
		System.out.println("------current server contains clientno-----"+keys);
		
		if(size > 0){
			Random random = new Random();
			int index = random.nextInt(size);
			List<String> lt = new ArrayList<String>(keys);
			clientNo = lt.get(index);
		}
		
		return clientNo;
	}*/
	
//	public String getClientServer(String name){
//		if(!ClientHolder.getInstance().isContainsClientName(name)){
//			return getRandomClientNo();
//		}else{
//			return name;
//		}
//	}

}
