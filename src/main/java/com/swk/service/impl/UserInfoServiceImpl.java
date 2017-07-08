package com.swk.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.swk.bean.UserInfo;
import com.swk.dao.UserInfoMapper;
import com.swk.service.IUserInfoService;

@Service("userInfoService")
public class UserInfoServiceImpl implements IUserInfoService {
	
	@Resource
	private UserInfoMapper mapper;

	@Override
	public UserInfo getUserInfoById(long id) {
		return mapper.selectByPrimaryKey(id);
	}

}
