package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.service.ProxyIpService;
import com.coatardbul.baseCommon.model.entity.ProxyIp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/3/14
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "ip相关")
@RequestMapping("/ip")
public class ProxyIpController {
    @Autowired
    ProxyIpService proxyIpService;


    @ApiOperation("获取所有ip信息")
    @RequestMapping(path = "/getAllIps", method = RequestMethod.POST)
    public CommonResult<List<ProxyIp>> getAllIps() {
        return CommonResult.success(proxyIpService.getAllIps());
    }


}
