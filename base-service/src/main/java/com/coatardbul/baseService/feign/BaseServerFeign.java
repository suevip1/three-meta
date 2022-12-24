
package com.coatardbul.baseService.feign;


import com.coatardbul.baseService.config.FeignLogConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "baseServer", configuration = FeignLogConfig.class, fallbackFactory = String.class)
public interface BaseServerFeign {

    /**
     * @return
     */
    @RequestMapping(value = "baseServer/snowflakeId", method = RequestMethod.GET)
    @Headers("Content-Type: application/json")
    public String getSnowflakeId();


}
