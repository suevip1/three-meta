package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import com.coatardbul.stock.service.statistic.TongHuaShunIndustryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/10
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "基础相关相关")
@RequestMapping("/tongHuaShunIndustry")
public class TongHuaShunIndustryController {

    @Resource
    TongHuaShunIndustryService  tongHuaShunIndustryService;

    @Resource
    StockCronRefreshService stockCronRefreshService;

    @ApiOperation("获取所有板块信息")
    @RequestMapping(path = "/getIncreaseRate", method = RequestMethod.POST)
    public CommonResult getAllPlate(@RequestBody Map map) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        return CommonResult.success(tongHuaShunIndustryService.getIncreaseRate(map.get("bkCode").toString(),map.get("dateStr").toString()));
    }
    @ApiOperation("获取所有板块年涨幅信息")
    @RequestMapping(path = "/getYearIncreaseRate", method = RequestMethod.POST)
    public CommonResult getYearIncreaseRate(@RequestBody Map map) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        return CommonResult.success(tongHuaShunIndustryService.getYearIncreaseRate(map.get("bkCode").toString(),map.get("yearStr").toString()));
    }

    @ApiOperation("获取所有板块信息")
    @RequestMapping(path = "/getAllPlate1", method = RequestMethod.POST)
    public CommonResult getAllPlate1(@RequestBody Map map) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        return CommonResult.success(tongHuaShunIndustryService.getBaseInfo(map.get("bkCode").toString()));
    }

    @ApiOperation("获取所有板块信息")
    @RequestMapping(path = "/getAllPlate2", method = RequestMethod.POST)
    public CommonResult getAllPlate2(@RequestBody Map map) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        tongHuaShunIndustryService.getAllInfo();
        return CommonResult.success(null);
    }







}
