package com.swk.request;

import com.swk.common.annotation.CustomAnnotation;

public class UserInfoRequest {

	@CustomAnnotation(isNull=false,maxLength=4,description="姓名")
	private String name;
	
	@CustomAnnotation(isNull=false,maxLength=11,description="手机号")
	private String mobile;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
