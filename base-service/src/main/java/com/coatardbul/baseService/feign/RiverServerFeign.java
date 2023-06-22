
package com.coatardbul.baseService.feign;


import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.config.FeignLogConfig;

import com.coatardbul.baseService.entity.feign.CalendarDateDTO;
import com.coatardbul.baseService.entity.feign.CalendarSpecialDTO;
import com.coatardbul.baseService.entity.feign.StockTemplateDto;
import com.coatardbul.baseService.entity.feign.StockTemplateQueryDTO;
import com.coatardbul.baseService.entity.feign.StockTimeInterval;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@FeignClient(name = "river", url="124.222.217.230:9002", configuration = FeignLogConfig.class, fallbackFactory = String.class)
public interface RiverServerFeign {

    /**
     * 获取根据模板id，日期，时间 查询的模板字符串
     * @return
     */
    @RequestMapping(value = "river/api/stockTemplate/getQuery", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<String> getQuery(StockTemplateQueryDTO dto);

    /**
     * 间隔数据
     * @return
     */
    @RequestMapping(value = "river/timeInterval/getTimeList", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<List<String>> getTimeIntervalList(StockTimeInterval dto);

    /**
     * 获取两个时间段之间的日期
     * @param dto
     * @return
     */
    @RequestMapping(value = "river/api/calendar/getDate", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<List<String>> getDate(CalendarDateDTO dto);


    @RequestMapping(value = "river/api/stockTemplate/findOne", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<StockTemplateDto> findOne(StockTemplateDto dto);



    @RequestMapping(value = "river/api/calendar/getSpecialDay", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<String> getSpecialDay(CalendarSpecialDTO dto);


    /**
     * 验证token
     * @param token
     * @return
     */
    @RequestMapping(value = "river/user/verifyUserValid", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<Boolean> verifyUserValid(String token);


    /**
     * 记录登陆信息
     * @param
     * @return
     */
    @RequestMapping(value = "river/loginDetail/logLoginInfo", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult logLoginInfo(HttpServletRequest request);



    /**
     * 获取目前登陆用户
     * @param
     * @return
     */
    @RequestMapping(value = "river/user/getCurrUserName", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    public CommonResult<String> getCurrUserName();


}
