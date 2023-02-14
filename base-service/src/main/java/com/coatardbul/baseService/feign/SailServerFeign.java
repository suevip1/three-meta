
package com.coatardbul.baseService.feign;


import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.config.FeignLogConfig;
import com.coatardbul.baseService.entity.dto.StockCronRefreshDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "sail",
//        url="localhost:10001",
        configuration = FeignLogConfig.class, fallbackFactory = String.class)
public interface SailServerFeign {

    /**
     * 获取根据模板id，日期，时间 查询的模板字符串
     * @return
     */
    @RequestMapping(value = "sail/cronRefresh/refreshStockInfo", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult refreshStockInfo(StockCronRefreshDTO dto);

    @RequestMapping(value = "sail/cronRefresh/refreshHisStockInfo", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult refreshHisStockInfo(StockCronRefreshDTO dto);



    @RequestMapping(value = "sail/cronRefresh/refreshStockMinuterInfo", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult refreshStockMinuterInfo(StockCronRefreshDTO dto);




    @RequestMapping(value = "sail/config/getThreadPoolConfig", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult getThreadPoolConfig();


    @RequestMapping(value = "sail/cookie/refreshCookie", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult refreshCookie();

}
