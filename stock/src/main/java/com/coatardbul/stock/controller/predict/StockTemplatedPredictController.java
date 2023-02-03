package com.coatardbul.stock.controller.predict;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.model.dto.StockPredictDto;
import com.coatardbul.stock.service.statistic.StockPredictService;
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
 * Note:模板预测，回测模板
 * <p>
 * Date: 2022/4/4
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "")
@RequestMapping("/stockPredict")
public class StockTemplatedPredictController {
    @Autowired
    StockPredictService stockPredictService;

    @WebLog(value = "")
    @RequestMapping(path = "/execute", method = RequestMethod.POST)
    public CommonResult execute(@Validated @RequestBody StockPredictDto dto) {
        stockPredictService.execute(dto);
        return CommonResult.success(null);
    }


    @WebLog(value = "")
    @RequestMapping(path = "/getAll", method = RequestMethod.POST)
    public CommonResult getAll(@Validated @RequestBody StockPredictDto dto) {
        return CommonResult.success( stockPredictService.getAll(dto));
    }

    @WebLog(value = "")
    @RequestMapping(path = "/updateByQuery", method = RequestMethod.POST)
    public CommonResult updateByQuery(@Validated @RequestBody StockPredictDto dto) {
        stockPredictService.updateByQuery(dto);
        return CommonResult.success(null);
    }
    @WebLog(value = "")
    @RequestMapping(path = "/deleteById", method = RequestMethod.POST)
    public CommonResult deleteById(@Validated @RequestBody StockPredictDto dto) {
        stockPredictService.deleteById(dto);
        return CommonResult.success(null);
    }
    @WebLog(value = "")
    @RequestMapping(path = "/updateById", method = RequestMethod.POST)
    public CommonResult updateById(@Validated @RequestBody StockPredictDto dto) {
        stockPredictService.updateById(dto);
        return CommonResult.success(null);
    }
    @WebLog(value = "")
    @RequestMapping(path = "/updateStatus", method = RequestMethod.POST)
    public CommonResult updateStatus(@Validated @RequestBody StockPredictDto dto) {
        stockPredictService.updateStatus(dto);
        return CommonResult.success(null);
    }
    @WebLog(value = "")
    @RequestMapping(path = "/cancelStatus", method = RequestMethod.POST)
    public CommonResult cancelStatus(@Validated @RequestBody StockPredictDto dto) {
        stockPredictService.cancelStatus(dto);
        return CommonResult.success(null);
    }
    @WebLog(value = "")
    @RequestMapping(path = "/deleteByQuery", method = RequestMethod.POST)
    public CommonResult deleteByQuery(@Validated @RequestBody StockPredictDto dto) {
        stockPredictService.deleteByQuery(dto);
        return CommonResult.success(null);
    }
}
