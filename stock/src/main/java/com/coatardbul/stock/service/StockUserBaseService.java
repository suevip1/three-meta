package com.coatardbul.stock.service;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.UserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/22
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockUserBaseService {

    @Autowired
    private UserBaseService userBaseService;

    @Autowired
    RiverServerFeign riverServerFeign;

    /**
     *
     */

    public String getCurrUserName(HttpServletRequest request) {

        try {
            String username = userBaseService.getCurrAccountByToken(request.getHeader("token"));
            if (StringUtils.isNotBlank(username)) {
                return username;
            }else {
                return getCurrUserNameByRiver();
            }
        } catch (Exception e) {
            //通过redis获取异常，则通过接口获取
         return getCurrUserNameByRiver();
        }
    }
    private String getCurrUserNameByRiver(){
        CommonResult currUserName = riverServerFeign.getCurrUserName();
        Object data = currUserName.getData();
        if (data !=null ) {
            return  data.toString();
        } else {
            return null;
        }
    }

    public String getDefaultTradeUser(){
        return "sxl14459048";
    }
}


