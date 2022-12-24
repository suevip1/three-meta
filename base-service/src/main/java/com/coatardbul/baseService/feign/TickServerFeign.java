
package com.coatardbul.baseService.feign;


import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.config.FeignLogConfig;
import com.coatardbul.baseService.entity.dto.StockCronRefreshDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "tick",
        url = "43.142.151.181:8000",
        configuration = FeignLogConfig.class, fallbackFactory = String.class)
public interface TickServerFeign {


    @RequestMapping(value = "tick/refreshTickInfo/", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult refreshTickInfo(StockCronRefreshDTO dto);


}
