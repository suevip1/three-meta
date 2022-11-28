package com.coatardbul.base_server.controller;

import com.coatardbul.baseService.service.SnowFlakeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zrj
 * @date 2021/2/15
 * @since V1.0
 **/
@RestController
public class SnowFlakeController {

    @Resource
    private SnowFlakeService snowFlakeService;

    @GetMapping("/snowflakeId")
    public long getSnowflakeId(){
        return snowFlakeService.snowflakeId();
    }
}
