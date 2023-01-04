package com.coatardbul.stock.controller.trade;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.model.entity.StockPreTrade;
import com.coatardbul.stock.service.statistic.trade.StockPreTradeService;
import io.swagger.annotations.Api;
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
 * Date: 2022/12/27
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "预交易")
@RequestMapping("/preTrade")
public class StockPreTradeController {
    
    @Autowired
    StockPreTradeService stockPreTradeService;

    /**
     * 保存所有信息
     */
    @WebLog(value = "保存所有信息")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public CommonResult add(@Validated @RequestBody StockPreTrade dto)  {
        stockPreTradeService.add(dto);
        return CommonResult.success(null);
    }

    /**
     * 保存所有信息
     */
    @WebLog(value = "保存所有信息")
    @RequestMapping(path = "/modify", method = RequestMethod.POST)
    public CommonResult modify(@Validated @RequestBody StockPreTrade dto)  {
        stockPreTradeService.modify(dto);
        return CommonResult.success(null);
    }
    /**
     * 保存所有信息
     */
    @WebLog(value = "保存所有信息")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult delete(@Validated @RequestBody StockPreTrade dto)  {
        stockPreTradeService.delete(dto);
        return CommonResult.success(null);
    }


    /**
     * 全部策略
     * @param
     * @return
     */
    @RequestMapping(path = "/getAll", method = RequestMethod.POST)
    public CommonResult getAll(@Validated @RequestBody StockPreTrade dto) {
        return CommonResult.success(stockPreTradeService.getAll(dto));
    }
}
