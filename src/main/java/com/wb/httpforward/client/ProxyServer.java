package com.wb.httpforward.client;

public class ProxyServer {
	private String name;
	
	private String baseUrl;
	
	private String heartBeatUrl;
	
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getHeartBeatUrl() {
		return heartBeatUrl;
	}

	public void setHeartBeatUrl(String heartBeatUrl) {
		this.heartBeatUrl = heartBeatUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void start(){
		this.status = "on";
	}
	
	public void stop(){
		this.status = "off";
	}
	
	public boolean isStarted(){
		return this.status.equalsIgnoreCase("on") ? true : false;
	}

	@Override
	public String toString() {
		return "ProxyServer [name=" + name + "]";
	}
	
}
