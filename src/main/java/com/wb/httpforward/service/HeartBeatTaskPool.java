package com.wb.httpforward.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.wb.httpforward.client.ClientHolder;
import com.wb.httpforward.client.ProxyServer;
import com.wb.httpforward.client.ProxyServerManager;

public class HeartBeatTaskPool {

	private static HeartBeatTaskPool pool;
	
	private ExecutorService es ;
	
	private HeartBeatTaskPool() {
		init();
	}
	
	public static HeartBeatTaskPool getInstance(){
		if(pool == null){
			pool = new HeartBeatTaskPool();
		}
		return pool;
	}
	
	private void init(){
		es = Executors.newFixedThreadPool(3);
	}
	
	public void startTask(){
		List<ProxyServer> servers = ProxyServerManager.getInstance().getServers();
		for(ProxyServer server : servers){
			if(server.isStarted()){
				HeartBeatCheckService hc = new HeartBeatCheckService(server.getName(), server.getHeartBeatUrl());
				es.execute(hc);
			}
		}
	}
	
	public boolean startHeartBeatTask(String serverName){
		
		if(!ClientHolder.getInstance().isContainsClientName(serverName)){
			return false;
		}
		
		ProxyServer ps = ClientHolder.getInstance().getProxyServer(serverName);
		Callable<Integer> hc = new HeartBeatCheckService(ps.getName(), ps.getHeartBeatUrl());
		Future<Integer> callBack = es.submit(hc);
		int code = 0;
		try {
			code = callBack.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
		
		if(code == 1){
			ps.start();
		}
		
		return code == 1 ? true : false; 
	}
	
	public void stopHeartBeatTask(String serverName){
		
		if(!ClientHolder.getInstance().isContainsClientName(serverName)){
			return;
		}
		
		ClientHolder.getInstance().getProxyServer(serverName).stop();
		
	}

}
