package com.coatardbul.stock.controller.trade;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.model.entity.StockWatch;
import com.coatardbul.stock.service.statistic.trade.StockWatchService;
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
@Api(tags = "观察")
@RequestMapping("/watch")
public class StockWatchController {


    @Autowired
    StockWatchService stockWatchService;


    /**
     * 保存所有信息
     */
    @WebLog(value = "保存所有信息")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public CommonResult add(@Validated @RequestBody StockWatch dto)  {
        stockWatchService.add(dto);
        return CommonResult.success(null);
    }

    /**
     * 保存所有信息
     */
    @WebLog(value = "保存所有信息")
    @RequestMapping(path = "/modify", method = RequestMethod.POST)
    public CommonResult modify(@Validated @RequestBody StockWatch dto)  {
        stockWatchService.modify(dto);
        return CommonResult.success(null);
    }
    /**
     * 保存所有信息
     */
    @WebLog(value = "保存所有信息")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult delete(@Validated @RequestBody StockWatch dto)  {
        stockWatchService.delete(dto);
        return CommonResult.success(null);
    }


    /**
     * 全部策略
     * @param
     * @return
     */
    @RequestMapping(path = "/getAll", method = RequestMethod.POST)
    public CommonResult getAll() {
        return CommonResult.success(stockWatchService.getAll());
    }
}
