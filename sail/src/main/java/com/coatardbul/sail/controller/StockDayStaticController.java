package com.coatardbul.sail.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.sail.service.base.StockStrategyService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptException;
import java.io.IOException;

/**
 * <p>
 * Note:日统计数据 喇叭口，涨跌数量
 * @see com.coatardbul.stock.common.util.StockStaticModuleUtil
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



    /**
     * 同花顺新版问财功能
     *
     * @param dto 基础查询对象，支持id和问句查询
     * @return
     */
    @RequestMapping(path = "/strategy", method = RequestMethod.POST)
    public CommonResult strategy(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, IOException, IllegalAccessException {
        return CommonResult.success(stockStrategyService.comprehensiveStrategy(dto));
    }



}
