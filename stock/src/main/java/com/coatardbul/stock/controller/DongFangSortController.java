package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.stock.service.statistic.DongFangSortService;
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
 * Note:涨速排序
 * <p>
 * Date: 2022/12/10
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "基础相关相关")
@RequestMapping("/dongFangSort")
public class DongFangSortController {

    @Resource
    DongFangSortService dongFangSortService;

    @Resource
    StockCronRefreshService stockCronRefreshService;

    @ApiOperation("获取涨速排序信息")
    @RequestMapping(path = "/getSpeed", method = RequestMethod.POST)
    public CommonResult getSpeed() {
        return CommonResult.success(dongFangSortService.getSpeed());
    }
    @ApiOperation("获取涨幅排序信息")
    @RequestMapping(path = "/getIncrease", method = RequestMethod.POST)
    public CommonResult getIncrease() {
        return CommonResult.success(dongFangSortService.getIncrease());
    }


    @ApiOperation("获取转债信息")
    @RequestMapping(path = "/getConvertBond", method = RequestMethod.POST)
    public CommonResult getConvertBond(@RequestBody StockStrategyQueryDTO dto) {
        return CommonResult.success(dongFangSortService.getConvertBondLimit(dto));
    }




}
