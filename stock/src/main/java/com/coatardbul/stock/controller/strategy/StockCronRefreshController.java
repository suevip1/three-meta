package com.coatardbul.stock.controller.strategy;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseService.service.CronRefreshService;
import com.coatardbul.stock.model.dto.StockCronRefreshDTO;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
     CronRefreshService cronRefreshService;
    @RequestMapping(path = "getThreadPoolConfig", method = RequestMethod.POST)
    public CommonResult getThreadPoolConfig() {
        Object dataThreadPoolConfig = stockCronRefreshService.getDataThreadPoolConfig();
        return CommonResult.success(dataThreadPoolConfig);
    }

    @ApiOperation("设置配置")
    @RequestMapping(path = "/setConfig", method = RequestMethod.POST)
    public CommonResult setConfig(@RequestBody CronRefreshConfigBo bo) {
        cronRefreshService.setCronRefreshConfigBo(bo);
        return CommonResult.success(null);
    }

    @ApiOperation("获取配置")
    @RequestMapping(path = "/getConfig", method = RequestMethod.POST)
    public CommonResult getConfig() {
        return CommonResult.success(cronRefreshService.getCronRefreshConfigBo());
    }


    /**
     * 获取所有的股票信息
     *
     * @param
     * @return
     */
    @ApiOperation("获取所有的股票信息")
    @RequestMapping(path = "/getStockInfo", method = RequestMethod.POST)
    public CommonResult getStockInfo(@RequestBody StockCronRefreshDTO dto) {
        return CommonResult.success(stockCronRefreshService.getStockInfo(dto));
    }


    /**
     * 获取固定股票即时信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("获取固定股票即时信息")
    @RequestMapping(path = "/refreshStockInfo", method = RequestMethod.POST)
    public CommonResult refreshStockInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.refreshStockInfo(dto);
        return CommonResult.success(null);
    }

    /**
     * 获取固定股票分钟信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("获取固定股票分钟信息")
    @RequestMapping(path = "/refreshStockMinuterInfo", method = RequestMethod.POST)
    public CommonResult refreshStockMinuterInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.refreshStockMinuterInfo(dto.getCodeArr());
        return CommonResult.success(null);
    }


    /**
     * 获取固定历史股票tick信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("获取固定股票即时信息")
    @RequestMapping(path = "/refreshHisStockTickInfo", method = RequestMethod.POST)
    public CommonResult refreshHisStockTickInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.refreshHisStockTickInfo(dto);
        return CommonResult.success(null);
    }


    /**
     * 删除固定股票即时信息
     *
     * @param dto
     * @return
     */
    @ApiOperation("删除固定股票即时信息")
    @RequestMapping(path = "/deleteStockInfo", method = RequestMethod.POST)
    public CommonResult deleteStockInfo(@RequestBody StockCronRefreshDTO dto) {
        stockCronRefreshService.deleteStockInfo(dto.getCodeArr(),dto.getDateStr());
        return CommonResult.success(null);
    }


    @ApiOperation("获取固定股票即时信息")
    @RequestMapping(path = "/getTickInfo", method = RequestMethod.POST)
    public CommonResult getTickInfo(@RequestBody StockCronRefreshDTO dto) {
        List tickInfo = stockCronRefreshService.getTickInfo(dto);
        return CommonResult.success(tickInfo);
    }

    @ApiOperation("获取固定股票即时信息")
    @RequestMapping(path = "/getMinuterInfo", method = RequestMethod.POST)
    public CommonResult getMinuterInfo(@RequestBody StockCronRefreshDTO dto) {
        List minuterInfo = stockCronRefreshService.getMinuterInfo(dto);
        return CommonResult.success(minuterInfo);
    }


}
