package com.coatardbul.stock.interceptor;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.interceptor.InterfaceURLInterceptor;
import com.coatardbul.baseService.feign.RiverServerFeign;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 拦截器
 */
@Component
@Slf4j
public class StockInterceptor extends InterfaceURLInterceptor {

    @Autowired
    private RiverServerFeign riverServerFeign;


    @Override
    public Boolean
    verifyUserValid(String token) {
        try {
            int retryNum = 5;
            while (retryNum > 0) {
                try {
                    CommonResult<Boolean> booleanCommonResult = riverServerFeign.verifyUserValid(token);
                    return booleanCommonResult.getData();
                } catch (RetryableException e) {
                    retryNum--;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

    @Override
    public void logLoginInfo(HttpServletRequest request) {

        riverServerFeign.logLoginInfo(request);
    }


}