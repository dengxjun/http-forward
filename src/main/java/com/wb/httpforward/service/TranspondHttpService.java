package com.wb.httpforward.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wb.httpforward.client.ClientHolder;
import com.wb.httpforward.client.ProxyServer;

/**
 * @author www
 * @date 2015年9月11日
 */

public class TranspondHttpService {
	
	private static final Logger log = LoggerFactory.getLogger(TranspondHttpService.class);
	
	private static TranspondHttpService instance = new TranspondHttpService();
	
	 private final static AtomicInteger next = new AtomicInteger(0);
	
	private TranspondHttpService() {
		
	}
	
	public static TranspondHttpService getInstance() {
		return instance;
	}

	public boolean isCorrectClientServer(String name){
		return ClientHolder.getInstance().isContainsClientName(name);
	}
	
	public synchronized String getRandomClientNo(){
		String clientName = "";
		Set<ProxyServer> keys = ClientHolder.getInstance().getAlivedClientServer();
		int size = keys.size();
		
		System.out.println("------current server contains clientno-----"+keys);
		
		if(size > 0){
			Random random = new Random();
			int index = random.nextInt(size);
			List<ProxyServer> lt = new ArrayList<ProxyServer>(keys);
			clientName = lt.get(index).getName();
		}
		
		return clientName;
	}
	
	public synchronized  String getRandomClientName(){
		String clientName = "";
		Set<ProxyServer> pservers = ClientHolder.getInstance().getAlivedClientServer();
		int incre = next.getAndIncrement();
		int size = pservers.size();
		if(size > 0){
			int selected = incre % size;
			List<ProxyServer> lt = new ArrayList<ProxyServer>(pservers);
			clientName = lt.get(selected).getName();
		}
//		System.out.println(clientNo);
		return clientName;
	}
	
	public String getClientName(String clientName){
		if(!ClientHolder.getInstance().isClientServerInUse(clientName)){
			return getRandomClientName();
		}else{
			return clientName;
		}
	}
}
