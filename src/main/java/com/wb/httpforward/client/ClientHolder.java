package com.wb.httpforward.client;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author www
 * @date 2015年9月11日
 */

public class ClientHolder {

	
	private static Map<String, ProxyServer> proxyServerMap = new ConcurrentHashMap<String, ProxyServer>();
	
	private ProxyServerManager proxyServerManager;
	
	private static ClientHolder instance = new ClientHolder();
	
	private ClientHolder() {
		init();
	}
	
	public static ClientHolder getInstance() {
		return instance;
	}
	
	private void init(){
		proxyServerManager = ProxyServerManager.getInstance();
		/*clientServerMap.put("rnd4", "http://uat02-rnd4.wisers.com:9000/vs/api/v1");
		clientServerMap.put("server1", "http://192.168.44.29:8080/vs/api/v1");
		
		clientHeartBeatUrlMap.put("rnd4", "http://192.168.44.217:8088/appledaily");
		clientHeartBeatUrlMap.put("server1", "http://192.168.44.217:8080/appledaily");*/
		
		
		initProxyServerMap();
		
		
//		clientServerMap.put("server1", "http://192.168.44.8:8089/loveWS");
//		clientServerMap.put("rnd4", "http://192.168.44.8:8051/loveWS");
//		
//		clientHeartBeatUrlMap.put("server1", "http://192.168.44.8:8089/loveWS/heartBeat");
//		clientHeartBeatUrlMap.put("rnd4", "http://192.168.44.8:8051/loveWS/heartBeat");
	}
	
	private void initProxyServerMap(){
		List<ProxyServer> proxyServers = proxyServerManager.getServers();
		for(ProxyServer ps : proxyServers){
			proxyServerMap.put(ps.getName(), ps);
		}
	}
	
	
	public Set<ProxyServer> getAlivedClientServer(){
		Set<ProxyServer> psSet = new HashSet<ProxyServer>();
		List<ProxyServer> proxyServers = proxyServerManager.getServers();
		for(ProxyServer ps : proxyServers){
			if(ps.getStatus().equalsIgnoreCase("on")){
				psSet.add(ps);
			}
		}
		return psSet;
	}
	
	public ProxyServer getProxyServer(String serverName){
		return proxyServerMap.get(serverName);
	}
	
	public boolean isContainsClientName(String key){
		return proxyServerMap.keySet().contains(key);
	}
	
	public boolean isClientServerInUse(String serverName){
		return proxyServerMap.get(serverName).getStatus().equalsIgnoreCase("on") ? true : false;
	}
	
	public void startClientServer(String serverName) {
		proxyServerMap.get(serverName).start();
	}
	
	public void stopClientServer(String serverName) {
		proxyServerMap.get(serverName).stop();
	}
	
}
