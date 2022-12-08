package com.coatardbul.sail.feign;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.dto.StockTemplateQueryDTO;
import com.coatardbul.baseService.config.FeignLogConfig;
import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/8
 *
 * @author Su Xiaolei
 */
@FeignClient(name = "stock",
        url="124.222.217.230:9006",
        configuration = FeignLogConfig.class, fallbackFactory = String.class)
public interface StockServerFeign {

    @RequestMapping(value = "stock/buyTask/add", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<String> add(StockTradeBuyTask dto);


}
