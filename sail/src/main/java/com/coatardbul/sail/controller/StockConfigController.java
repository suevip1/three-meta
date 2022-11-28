package com.coatardbul.sail.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.sail.service.ThreadPoolConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * Note:redis上数据定时刷新，操作管理
 * <p>
 * Date: 2022/4/9
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "定时刷新")
@RequestMapping("/config")
public class StockConfigController {
    @Autowired
    ThreadPoolConfigService threadPoolConfigService;





    /**
     * 获取固定股票盘口信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("获取线程池的配置信息")
    @RequestMapping(path = "getThreadPoolConfig", method = RequestMethod.POST)
    public CommonResult getThreadPoolConfig() {
        Map dataThreadPoolConfig = threadPoolConfigService.getDataThreadPoolConfig();
        return CommonResult.success(dataThreadPoolConfig);
    }

    /**
     *关闭连接并重启
     *
     * @param dto
     * @return
     */
    @ApiOperation("关闭连接并重启")
    @RequestMapping(path = "closeAndRestartHttpClient", method = RequestMethod.POST)
    public CommonResult closeAndRestart() {
         threadPoolConfigService.closeAndRestart();
        return CommonResult.success(null);
    }


}
