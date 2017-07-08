package com.swk.test;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.swk.bean.UserInfo;
import com.swk.service.impl.UserInfoServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class JuntTest {

	private static Logger logger = Logger.getLogger(JuntTest.class);
	
	@Resource
	private UserInfoServiceImpl userInfoService;
	
	@Test
	public void test(){
		UserInfo userInfo = userInfoService.getUserInfoById(234242353453l);
		logger.info(userInfo.getRealityName());
	}
	
}
