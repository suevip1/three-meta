package com.coatardbul.river.interceptor;

import com.coatardbul.baseCommon.interceptor.InterfaceURLInterceptor;
import com.coatardbul.river.service.LoginDetailService;
import com.coatardbul.river.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 拦截器
 */
@Component
public class RiverInterceptor extends InterfaceURLInterceptor {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LoginDetailService loginDetailService;

    @Override
    public Boolean
    verifyUserValid(String token) {
        return userInfoService.verifyUserValid(token);

    }

    @Override
    public void logLoginInfo(HttpServletRequest request) {

        loginDetailService.logLoginInfo(request);
    }


}