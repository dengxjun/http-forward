package com.wb.httpforward.client;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ProxyServerManager {
	private  ProxyServers servers;
	
	private static ProxyServerManager instance;

	private ProxyServerManager(){
		init();
	}
	
	public static ProxyServerManager getInstance(){
		if(instance == null){
			instance = new ProxyServerManager();
		}
		return instance;
	}
	
	private void init(){
		servers = praseToBean();
	}
	
	public ProxyServers praseToBean(){  
    	
		ProxyServers servers = null;
	    InputStream is = getClass().getClassLoader().getResourceAsStream("proxy_servers.xml");
	    	
	    try {  
	        JAXBContext context = JAXBContext.newInstance(ProxyServers.class);  
	        Unmarshaller unmarshaller = context.createUnmarshaller();  
	        servers = (ProxyServers)unmarshaller.unmarshal(is);  
	        System.out.println(servers.getProxyServer().get(0).getName());  
	    } catch (JAXBException e) {  
	        e.printStackTrace();  
	    }  
	      return servers;
    }  
	
	public List<ProxyServer> getServers() {
		return servers.getProxyServer();
	}

}
