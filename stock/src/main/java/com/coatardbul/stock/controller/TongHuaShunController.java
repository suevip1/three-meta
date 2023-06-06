package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.dto.TongHuaShunPlateDTO;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import com.coatardbul.stock.service.statistic.TongHuaShunPlateService;
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
@RequestMapping("/tongHuaShunPlate")
public class TongHuaShunController {

    @Resource
    TongHuaShunPlateService tongHuaShunPlateService;

    @Resource
    StockCronRefreshService stockCronRefreshService;

    @ApiOperation("获取所有板块信息")
    @RequestMapping(path = "/getAllPlate", method = RequestMethod.POST)
    public CommonResult getAllPlate() throws ScriptException, FileNotFoundException, NoSuchMethodException {
        return CommonResult.success(tongHuaShunPlateService.getAllPlate());
    }

    @ApiOperation("获取板块股票信息")
    @RequestMapping(path = "/getPlateStock", method = RequestMethod.POST)
    public CommonResult getPlateStock(@RequestBody TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        return CommonResult.success(tongHuaShunPlateService.getPlateStock(dto));
    }
    @ApiOperation("清空板块股票信息")
    @RequestMapping(path = "/clearPlateStock", method = RequestMethod.POST)
    public CommonResult clearPlateStock(@RequestBody TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        return CommonResult.success(tongHuaShunPlateService.clearPlateStock(dto));
    }


    @ApiOperation("将股票信息添加到板块中")
    @RequestMapping(path = "/addPlateInfo", method = RequestMethod.POST)
    public CommonResult addPlateInfo(@RequestBody TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        tongHuaShunPlateService.addPlateInfo(dto);
        return CommonResult.success(null);
    }






//    @ApiOperation("将股票信息添加到板块中")
//    @RequestMapping(path = "/sysnPlateInfo", method = RequestMethod.POST)
//    public CommonResult sysnPlateInfo(@RequestBody TongHuaShunPlateDTO dto) {
//        tongHuaShunPlateService.sysnPlateInfo(dto);
//        return CommonResult.success(null);
//    }

}
