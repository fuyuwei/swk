package com.swk.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.swk.bean.UserInfo;
import com.swk.service.IUserInfoService;

@Controller
@RequestMapping("/user")
public class UserInfoController {
	
	@Resource
	private IUserInfoService userInfoService;
	
	@RequestMapping("/showUser")
	public String toIndex(HttpServletRequest request,Model model){  
		long id = Long.parseLong(request.getParameter("userId"));
        UserInfo userInfo = this.userInfoService.getUserInfoById(id);
        model.addAttribute("user", userInfo);  
        return "showUser";  
    }  
}
