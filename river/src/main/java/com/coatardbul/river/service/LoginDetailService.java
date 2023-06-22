package com.coatardbul.river.service;

import com.coatardbul.baseCommon.util.IPUtils;
import com.coatardbul.baseService.feign.BaseServerFeign;
import com.coatardbul.river.mapper.LoginDetailMapper;
import com.coatardbul.river.model.entity.LoginDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/22
 *
 * @author Su Xiaolei
 */
@Service
public class LoginDetailService {


    @Autowired
    private LoginDetailMapper loginDetailMapper;
    @Autowired
    BaseServerFeign baseServerFeign;


    public void logLoginInfo(HttpServletRequest request) {
        String userGent = request.getHeader("user-agent");
        //客户端ip
        String clientIp = IPUtils.getIpAddress(request);
        //客户端port
        int clientPort = request.getRemotePort();
        //客户端请求URI
        String requestURI = request.getRequestURI();

        LoginDetail record=new LoginDetail();
        record.setId(baseServerFeign.getSnowflakeId());
        record.setIp(clientIp);
        record.setUserAgent(userGent);
        record.setClientPort(String.valueOf(clientPort));
        record.setRequestUri(requestURI);
        record.setGmtCreate(new Date());
        loginDetailMapper.insert(record);
    }

}
