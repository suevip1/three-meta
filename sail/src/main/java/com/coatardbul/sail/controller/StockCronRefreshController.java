package com.coatardbul.sail.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.sail.model.dto.StockCronRefreshDTO;
import com.coatardbul.sail.service.StockCronRefreshService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/cronRefresh")
public class StockCronRefreshController {
    @Autowired
    StockCronRefreshService stockCronRefreshService;




    /**
     * 获取固定股票盘口信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("获取固定股票即时信息")
    @RequestMapping(path = "/refreshStockInfo", method = RequestMethod.POST)
    public CommonResult refreshStockInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.refreshStockInfo(dto.getCodeArr());
        return CommonResult.success(null);
    }

    @ApiOperation("获取固定股票即时信息")
    @RequestMapping(path = "/refreshHisStockInfo", method = RequestMethod.POST)
    public CommonResult refreshHisStockInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.refreshHisStockInfo(dto);
        return CommonResult.success(null);
    }

    /**
     * 获取固定股票分钟信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("获取股票分钟信息")
    @RequestMapping(path = "/refreshStockMinuterInfo", method = RequestMethod.POST)
    public CommonResult refreshStockMinuterInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.refreshStockMinuterInfo(dto);
        return CommonResult.success(null);
    }


    /**
     * 获取固定股票tick信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("获取固定股票即时信息")
    @RequestMapping(path = "/refreshStockTickInfo", method = RequestMethod.POST)
    public CommonResult refreshStockTickInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.refreshStockTickInfo(dto.getCodeArr());
        return CommonResult.success(null);
    }





}
