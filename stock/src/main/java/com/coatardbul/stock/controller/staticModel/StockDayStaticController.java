package com.coatardbul.stock.controller.staticModel;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.common.util.StockStaticModuleUtil;
import com.coatardbul.stock.mapper.StockBaseMapper;
import com.coatardbul.baseCommon.model.bo.trade.StockBaseDetail;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.model.dto.StockEmotionDayRangeDTO;
import com.coatardbul.stock.model.dto.StockEmotionQueryDTO;
import com.coatardbul.stock.model.dto.StockEmotionRangeDayDTO;
import com.coatardbul.baseCommon.model.entity.StockBase;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.statistic.dayStatic.StockDayStaticService;
import com.coatardbul.stock.service.statistic.tradeQuartz.TradeBaseService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

/**
 * <p>
 * Note:日统计数据 喇叭口，涨跌数量
 * @see StockStaticModuleUtil
 * ，包括刷新取数，查询等。
 * <p>
 * Date: 2022/1/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "股票查询")
@RequestMapping("/stockQuery")
public class StockDayStaticController {



    @Autowired
    StockStrategyService stockStrategyService;

    @Autowired
    StockBaseMapper stockBaseMapper;

    @Autowired
    StockDayStaticService stockDayStaticService;


    @Autowired
    TradeBaseService tradeBaseService;

    /**
     * 同花顺新版问财功能
     *
     * @param dto 基础查询对象，支持id和问句查询
     * @return
     */
    @WebLog(value = "同花顺新版问财功能")
    @RequestMapping(path = "/strategy", method = RequestMethod.POST)
    public CommonResult strategy(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, IOException, IllegalAccessException {

        StockStrategyQueryDTO stockStrategyQueryDTO = stockStrategyService.preComprehensiveStrategy(dto);
        StrategyBO strategyBO = stockStrategyService.comprehensiveStrategy(dto);
        StrategyBO result = stockStrategyService.afterComprehensiveStrategy(stockStrategyQueryDTO, strategyBO);
        return CommonResult.success(result);
    }

    @WebLog(value = "同花顺新版问财功能第一页")
    @RequestMapping(path = "/strategyFirstPage", method = RequestMethod.POST)
    public CommonResult strategyFirstPage(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, IOException {
        return CommonResult.success(stockStrategyService.strategyFirstProcess(dto));
    }

    @WebLog(value = "同花顺新版问财功能第一页")
    @RequestMapping(path = "/optimizeStrategyFirstPage", method = RequestMethod.POST)
    public CommonResult optimizeStrategyFirstPage(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, IOException, IllegalAccessException {
        return CommonResult.success(stockStrategyService.optimizeStrategyFirstPage(dto));
    }

    /**
     * 问财实时总数
     * @param dto
     * @return
     * @throws NoSuchMethodException
     * @throws ScriptException
     * @throws FileNotFoundException
     */
    @WebLog(value = "问财实时总数")
    @RequestMapping(path = "/strategyCount", method = RequestMethod.POST)
    public CommonResult strategyCount(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, FileNotFoundException {
        return CommonResult.success(stockStrategyService.strategyCount(dto));
    }

    @WebLog(value = "优化实时总数")
    @RequestMapping(path = "/optimizeStrategyCount", method = RequestMethod.POST)
    public CommonResult optimizeStrategyCount(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, IOException, IllegalAccessException {
        return CommonResult.success(stockStrategyService.optimizeStrategyCount(dto));
    }
    
    @WebLog(value = "获取实时交易信息")
    @RequestMapping(path = "/getImmediateStockBaseInfo", method = RequestMethod.POST)
    public CommonResult getImmediateStockBaseInfo(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, FileNotFoundException {

        StockBaseDetail upLimitPrice = tradeBaseService.getImmediateStockBaseInfo(dto.getStockCode(), dto.getDateStr(),false);

        if (upLimitPrice.getUpLimitPrice() == null) {
            return CommonResult.failed("");
        }else {
            StockBase stockBase = stockBaseMapper.selectByPrimaryKey(upLimitPrice.getCode());
            if(stockBase!=null){
                upLimitPrice.setTheme(stockBase.getTheme());
                upLimitPrice.setIndustry(stockBase.getIndustry());
            }
            return CommonResult.success(upLimitPrice);

        }
    }


    /**
     * 刷新某天所有的数据，
     * @see StockStaticModuleUtil

     *
     */
    @WebLog(value = "")
    @RequestMapping(path = "/refreshDay", method = RequestMethod.POST)
    public CommonResult refreshDay(@Validated @RequestBody StockEmotionDayDTO dto) throws IllegalAccessException, ParseException {
        stockDayStaticService.refreshDay(dto);
        return CommonResult.success(null);
    }
    /**
     * 删除某天所有的数据，
     */
    @WebLog(value = "")
    @RequestMapping(path = "/deleteDay", method = RequestMethod.POST)
    public CommonResult deleteDay(@Validated @RequestBody StockEmotionDayDTO dto) throws IllegalAccessException, ParseException {
        stockDayStaticService.deleteDay(dto);
        return CommonResult.success(null);
    }


    /**
     * 根据日期间隔刷新所有的数据，间隔超过5天，只工作查询最近的5日数据，并且和当天比较
     * 如果日期为当天以前，判断是交易日，返回最近的交易日
     * 如果是当天的，启动定时任务，并且刷新已经有的数据 ，实时刷新数据
     */
    @WebLog(value = "")
    @RequestMapping(path = "/refreshDayRange", method = RequestMethod.POST)
    public CommonResult refreshDayRange(@Validated @RequestBody StockEmotionDayRangeDTO dto) {
        stockDayStaticService.refreshDayRange(dto);
        return CommonResult.success(null);
    }


    /**
     * 强制刷新日期区间
     * @param dto
     * @return
     */
    @WebLog(value = "")
    @RequestMapping(path = "/forceRefreshDayRange", method = RequestMethod.POST)
    public CommonResult forceRefreshDayRange(@Validated @RequestBody StockEmotionDayRangeDTO dto) {
        stockDayStaticService.forceRefreshDayRange(dto);
        return CommonResult.success(null);
    }

    /**
     *获取日期区间的数据
     */
    @WebLog(value = "")
    @RequestMapping(path = "/getRangeStatic", method = RequestMethod.POST)
    public CommonResult getRangeStatic(@Validated @RequestBody StockEmotionRangeDayDTO dto) {
        return CommonResult.success(stockDayStaticService.getRangeStatic(dto));
    }

    /**
     * 获取对应日期，对应标识的统计数据
     */
    @WebLog(value = "")
    @RequestMapping(path = "/getDayStatic", method = RequestMethod.POST)
    public CommonResult getDayStatic(@Validated @RequestBody StockEmotionQueryDTO dto) {
        return CommonResult.success(stockDayStaticService.getDayStatic(dto));
    }


}
