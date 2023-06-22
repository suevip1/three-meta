package com.coatardbul.sail.interceptor;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.interceptor.InterfaceURLInterceptor;
import com.coatardbul.baseService.feign.RiverServerFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 拦截器
 */
@Component
@Slf4j
public class SailInterceptor extends InterfaceURLInterceptor {

    @Autowired
    private RiverServerFeign riverServerFeign;


    @Override
    public Boolean
    verifyUserValid(String token) {
        try {
            CommonResult<Boolean> booleanCommonResult = riverServerFeign.verifyUserValid(token);
            return booleanCommonResult.getData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void logLoginInfo(HttpServletRequest request) {

        riverServerFeign.logLoginInfo(request);
    }


}