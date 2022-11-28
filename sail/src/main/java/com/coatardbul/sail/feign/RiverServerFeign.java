
package com.coatardbul.sail.feign;


import com.coatardbul.baseService.config.FeignLogConfig;
import com.coatardbul.sail.model.feign.StockTemplateQueryDTO;
import com.coatardbul.sail.common.api.CommonResult;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "river", url="124.222.217.230:9002", configuration = FeignLogConfig.class, fallbackFactory = String.class)
public interface RiverServerFeign {

    /**
     * 获取根据模板id，日期，时间 查询的模板字符串
     * @return
     */
    @RequestMapping(value = "river/api/stockTemplate/getQuery", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<String> getQuery(StockTemplateQueryDTO dto);



}
