package com.coatardbul.sail.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.sail.service.base.StockStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/1/15
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "同花顺cookie")
@RequestMapping("/cookie")
public class StockCookieController {


    @Autowired
    StockStrategyService stockStrategyService;

    @ApiOperation("cookie刷新")
    @RequestMapping(path = "/refreshCookie", method = RequestMethod.POST)
    public CommonResult refreshCookie() {
        stockStrategyService.refreshCookie();
        return CommonResult.success(null);
    }



}
