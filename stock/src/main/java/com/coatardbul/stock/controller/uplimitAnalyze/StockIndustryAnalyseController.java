package com.coatardbul.stock.controller.uplimitAnalyze;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.model.dto.StockEmotionDayRangeDTO;
import com.coatardbul.stock.model.dto.StockIndustryAnalyseDTO;
import com.coatardbul.stock.service.statistic.StockIndustryAnalyseService;
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
 * Date: 2022/1/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "行业分析")
@RequestMapping("/industryAnalyse")
public class StockIndustryAnalyseController {

    @Autowired
    StockIndustryAnalyseService stockIndustryAnalyseService;

    /**
     * 查询所有的题材
     */
    @WebLog(value = "查询所有的题材")
    @RequestMapping(path = "/findAll", method = RequestMethod.POST)
    public CommonResult findAll(@Validated @RequestBody StockIndustryAnalyseDTO dto) {


        return CommonResult.success(stockIndustryAnalyseService.getAll());
    }
    @WebLog(value = "查询所有的题材")
    @RequestMapping(path = "/findDayRange", method = RequestMethod.POST)
    public CommonResult findDayRange(@Validated @RequestBody StockEmotionDayRangeDTO dto) {


        return CommonResult.success(stockIndustryAnalyseService.findDayRange(dto));
    }


    /**
     *添加
     */
    @WebLog(value = "")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public CommonResult add(@Validated @RequestBody StockIndustryAnalyseDTO dto)  {
        stockIndustryAnalyseService.add(dto);
        return CommonResult.success(null);
    }
    @WebLog(value = "")
    @RequestMapping(path = "/get", method = RequestMethod.POST)
    public CommonResult get(@Validated @RequestBody StockIndustryAnalyseDTO dto)  {

        return CommonResult.success(stockIndustryAnalyseService.get(dto));
    }

    @WebLog(value = "")
    @RequestMapping(path = "/modify", method = RequestMethod.POST)
    public CommonResult modify(@Validated @RequestBody StockIndustryAnalyseDTO dto)  {
        stockIndustryAnalyseService.modify(dto);
        return CommonResult.success(null);
    }

    @WebLog(value = "")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult delete(@Validated @RequestBody StockIndustryAnalyseDTO dto)  {
        stockIndustryAnalyseService.delete(dto);
        return CommonResult.success(null);
    }





}
