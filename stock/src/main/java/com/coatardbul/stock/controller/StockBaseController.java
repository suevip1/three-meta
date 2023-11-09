package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.dto.StockBaseDTO;
import com.coatardbul.stock.service.statistic.StockBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/7/16
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "基础相关相关")
@RequestMapping("/base")
public class StockBaseController {
    @Autowired
    StockBaseService stockBaseService;


    @ApiOperation("新增股票信息")
    @RequestMapping(path = "/addProcess", method = RequestMethod.GET)
    public CommonResult addProcess() {
        stockBaseService.allAddProcess();
        return CommonResult.success(null);
    }
    @ApiOperation("新增转债信息")
    @RequestMapping(path = "/addConvertBondProcess", method = RequestMethod.GET)
    public CommonResult addConvertBondProcess() {
        stockBaseService.addConvertBondProcess();
        return CommonResult.success(null);
    }

    @ApiOperation("查询所有")
    @RequestMapping(path = "/findAll", method = RequestMethod.POST)
    public CommonResult findAll(@Validated @RequestBody StockBaseDTO dto) {
        return CommonResult.success(stockBaseService.findAll(dto));
    }




}
