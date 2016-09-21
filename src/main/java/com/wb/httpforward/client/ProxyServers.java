package com.wb.httpforward.client;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement  
public class ProxyServers {
	private List<ProxyServer> proxyServer;

	public List<ProxyServer> getProxyServer() {
		return proxyServer;
	}

	public void setProxyServer(List<ProxyServer> proxyServer) {
		this.proxyServer = proxyServer;
	}
}
