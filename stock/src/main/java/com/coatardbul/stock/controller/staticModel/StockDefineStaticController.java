package com.coatardbul.stock.controller.staticModel;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.model.dto.StockDefineStaticDTO;
import com.coatardbul.stock.service.statistic.StockDefineStaticService;
import com.coatardbul.stock.model.entity.StockDefineStatic;
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
 * Note:自定义统计，主要用来统计二板的方差，量比之类的数据。
 * <p>
 * Date: 2022/7/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "自定义统计")
@RequestMapping("/defineStatic")
public class StockDefineStaticController {


    @Autowired
    private StockDefineStaticService stockDefineStaticService;


    /**
     * 保存所有信息
     */
    @WebLog(value = "保存所有信息")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public CommonResult add(@Validated @RequestBody StockDefineStatic dto)  {
        stockDefineStaticService.add(dto);
        return CommonResult.success(null);
    }


    /**
     * 获取所有
     */
    @WebLog(value = "获取所有")
    @RequestMapping(path = "/getAll", method = RequestMethod.POST)
    public CommonResult getAll(@Validated @RequestBody StockDefineStaticDTO dto)  {
        return CommonResult.success(stockDefineStaticService.getAll(dto));
    }




}
