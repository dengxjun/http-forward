package com.wb.httpforward.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.wb.httpforward.client.ClientHolder;
import com.wb.httpforward.client.ProxyServer;
import com.wb.httpforward.client.ProxyServerManager;
import com.wb.httpforward.util.HttpClientUtil;

public class HeartBeatCheckService implements Runnable,Callable<Integer>{
	private  String target_server_heart_beat_url = null;
	private String target_servet_key = null;
	
	public int failCount = 0;
	
	public HeartBeatCheckService(String key,String url){
		this.target_server_heart_beat_url = url;
		this.target_servet_key = key;
	}
	
	public boolean sendGet(){
		CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();
		HttpGet httpGet = new HttpGet(this.target_server_heart_beat_url);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
//			System.out.println(code + "---- "+ this.target_servet_key + "---"+this.target_server_heart_beat_url);
			response.close();
			if(code > 200)
				return false;
			else 
				return true;
		} catch (ClientProtocolException e) {
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		} 
		
	}

	
	public static void main(String[] args){
		ExecutorService es = Executors.newFixedThreadPool(3);
		List<ProxyServer> servers = ProxyServerManager.getInstance().getServers();
		for(ProxyServer server : servers){
			if(server.isStarted()){
				Callable<Integer> hc = new HeartBeatCheckService(server.getName(), server.getHeartBeatUrl());
//				es.execute(hc);
				Future<Integer> f = es.submit(hc);
				int code  = 0;
				try {
					code = f.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				System.out.println(code);
			}
		}
		
	}

	@Override
	public Integer call() throws Exception {
		if(!sendGet()){
			return -1;
		}else{
			return 1;
		}
	}


	@Override
	public void run() {
		while(true){
			//System.out.println("-----Comming here is "+ this.target_servet_key +" and url is "+ this.target_server_heart_beat_url);
			if(!sendGet() && failCount < 3){
				failCount ++;
				try {
					Thread.sleep(1000*1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else if(failCount > 2){
				ClientHolder.getInstance().getProxyServer(this.target_servet_key).stop();
				System.out.println("********Server "+this.target_servet_key+" heart beat stop and left client server is " + ClientHolder.getInstance().getAlivedClientServer());
				break;
			}else{
				failCount = 0;
				if(!ClientHolder.getInstance().getProxyServer(this.target_servet_key).isStarted()){
					break;
				}
				try {
					Thread.sleep(1000*30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
