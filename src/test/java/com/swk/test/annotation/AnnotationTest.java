package com.swk.test.annotation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.istack.internal.logging.Logger;
import com.swk.common.annotation.AnnotationChecker;
import com.swk.common.exception.PostingException;
import com.swk.request.UserInfoRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class AnnotationTest {
	
	private Logger logger = Logger.getLogger(AnnotationTest.class);

	@Test
	public void testAnnotation(){
		UserInfoRequest request = new UserInfoRequest();
		try {
			AnnotationChecker.checkParam(request);
		} catch (PostingException e) {
			logger.info(e.getErrorCode()+":"+e.getErrorMessage());
		}
	}
}
