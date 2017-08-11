package com.swk.interceptor;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sun.istack.internal.logging.Logger;

public class SwkInterceptor implements HandlerInterceptor {
	
	private Logger log = Logger.getLogger(SwkInterceptor.class);
	
	public static AtomicInteger applicationCurrents = new AtomicInteger();
	
	public static final int LIMIT = 100;
	
	public static String ipAddress;
	
	static{
		try {
			InetAddress address = InetAddress.getLocalHost();
			ipAddress = address.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		int currents = applicationCurrents.getAndIncrement();
		if(currents > LIMIT){
			log.info(String.format("[%s] is overlimit,and currents is [%s]",ipAddress,currents));
			return false;
		} 
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}
	

}
