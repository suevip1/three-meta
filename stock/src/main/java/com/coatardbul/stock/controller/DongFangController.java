package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.dto.DongFangPlateDTO;
import com.coatardbul.stock.service.statistic.DongFangPlateService;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
@RequestMapping("/dongFangPlate")
public class DongFangController {

    @Resource
    DongFangPlateService dongFangPlateService;

    @Resource
    StockCronRefreshService stockCronRefreshService;

    @ApiOperation("获取所有板块信息")
    @RequestMapping(path = "/getAllPlate", method = RequestMethod.POST)
    public CommonResult getAllPlate() {
        return CommonResult.success(dongFangPlateService.getAllPlate());
    }

    @ApiOperation("获取板块股票信息")
    @RequestMapping(path = "/getPlateStock", method = RequestMethod.POST)
    public CommonResult getPlateStock(@RequestBody DongFangPlateDTO dto) {
        return CommonResult.success(dongFangPlateService.getPlateStock(dto));
    }
    @ApiOperation("清空板块股票信息")
    @RequestMapping(path = "/clearPlateStock", method = RequestMethod.POST)
    public CommonResult clearPlateStock(@RequestBody DongFangPlateDTO dto) {
        return CommonResult.success(dongFangPlateService.clearPlateStock(dto));
    }


    @ApiOperation("将股票信息添加到板块中")
    @RequestMapping(path = "/addPlateInfo", method = RequestMethod.POST)
    public CommonResult addPlateInfo(@RequestBody DongFangPlateDTO dto) {
        dongFangPlateService.addPlateInfo(dto);
        return CommonResult.success(null);
    }

    @ApiOperation("将涨停信息添加到板块中")
    @RequestMapping(path = "/addUpLimitPlateInfo", method = RequestMethod.POST)
    public CommonResult addUpLimitPlateInfo(@RequestBody DongFangPlateDTO dto) {
        stockCronRefreshService.dayAddUpLimitStockJob(dto.getDateStr());
        return CommonResult.success(null);
    }

    @ApiOperation("将低开上影线添加到板块中")
    @RequestMapping(path = "/addDksyxPlateInfo", method = RequestMethod.POST)
    public CommonResult addDksyxPlateInfo(@RequestBody DongFangPlateDTO dto) {
        stockCronRefreshService.addDksyxPlateInfo(dto.getDateStr());
        return CommonResult.success(null);
    }

    @ApiOperation("将历史两板以上添加到板块中")
    @RequestMapping(path = "/addHisTwoUpLimitAbovePlateInfo", method = RequestMethod.POST)
    public CommonResult addHisTwoUpLimitAbovePlateInfo(@RequestBody DongFangPlateDTO dto) {
        stockCronRefreshService.addHisTwoUpLimitAbovePlateInfo(dto.getDateStr());
        return CommonResult.success(null);
    }

    @ApiOperation("将历史5日埋伏添加到板块中")
    @RequestMapping(path = "/addMultiDayAmbushPlateInfo", method = RequestMethod.POST)
    public CommonResult addMultiDayAmbushPlateInfo(@RequestBody DongFangPlateDTO dto) {
        stockCronRefreshService.addMultiDayAmbushPlateInfo(dto.getDateStr());
        return CommonResult.success(null);
    }


    @ApiOperation("将股票信息添加到板块中")
    @RequestMapping(path = "/sysnPlateInfo", method = RequestMethod.POST)
    public CommonResult sysnPlateInfo(@RequestBody DongFangPlateDTO dto) {
        dongFangPlateService.sysnPlateInfo(dto);
        return CommonResult.success(null);
    }

}
