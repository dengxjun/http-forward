package com.wb.httpforward.web.servlet;



import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wb.httpforward.client.ProxyServer;
import com.wb.httpforward.client.ProxyServerManager;
import com.wb.httpforward.enums.CodeEnum;
import com.wb.httpforward.service.HeartBeatTaskPool;

/**
 * Created by www on 2015/9/7 0007.
 * 
 * 用来转发请求的控制器
 */

@Controller
public class ProxyServerController {
	
//	@Autowired
//	private TranspondHttpService transpondHttpService;
    
	@RequestMapping(value="/index",method=RequestMethod.GET)  
    public ModelAndView index(){  
        ModelAndView modelAndView = new ModelAndView("/index");  
        List<ProxyServer> ps =  ProxyServerManager.getInstance().getServers();
        modelAndView.addObject("servers", ps);  
        return modelAndView;  
    }  
	
    /**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
    @RequestMapping(value = "/proxyServer", method = {RequestMethod.POST, RequestMethod.GET})
    public
    //@ResponseBody
    String operation(HttpServletRequest request,
    		@RequestParam(value = "response", required = false) String responseMessageString) {
    	String operation_type = request.getParameter("operation_type");
    	String serverName = request.getParameter("serverName");
    	Assert.notNull(operation_type);
    	if(operation_type.equals("on")){
    		HeartBeatTaskPool.getInstance().startHeartBeatTask(serverName);
    	}else if(operation_type.equals("off")){
    		HeartBeatTaskPool.getInstance().stopHeartBeatTask(serverName);
    	}
    	 return "redirect:/web/index";
    }
    
    @RequestMapping(value = "/showServers", method = {RequestMethod.POST, RequestMethod.GET})
    public
    @ResponseBody
    String showServers(HttpServletRequest request,
    		@RequestParam(value = "response", required = false) String responseMessageString) {
    	List<ProxyServer> ps =  ProxyServerManager.getInstance().getServers();
    	return "{\"code\":" + ps.size() + ", \"message\":\"successs\"}";
    }
    
    
}
