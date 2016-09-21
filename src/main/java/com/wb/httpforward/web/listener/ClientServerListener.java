package com.wb.httpforward.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.wb.httpforward.service.HeartBeatTaskPool;

@WebListener
public class ClientServerListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		HeartBeatTaskPool taskPool = HeartBeatTaskPool.getInstance();
		taskPool.startTask();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
