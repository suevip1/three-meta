package com.coatardbul.river.local;


import com.coatardbul.river.common.api.CommonResult;
import com.coatardbul.river.model.dto.CalendarDTO;
import com.coatardbul.river.model.dto.CalendarDateDTO;
import com.coatardbul.river.model.dto.CalendarSpecialDTO;
import com.coatardbul.river.model.entity.AuthCalendar;
import com.coatardbul.river.service.CalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Su Xiaolei
 */
@Api("日期相关接口")
@RestController
@RequestMapping(value = "/api/calendar")
public class CalendarController {


    @Autowired
    private CalendarService calendarService;

    /**
     * 爬取日历数据，开始时间 yyyy-mm，结束时间
     * @param calendarDTO
     * @return
     */
    @ApiOperation("爬取日历数据，开始时间 yyyy-mm，结束时间")
    @PostMapping(value = "/widerCalendar", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> widerCalendar(@RequestBody @Valid CalendarDTO calendarDTO) {
        calendarService.widerCalendar(calendarDTO);
        return CommonResult.success(null);
    }


    /**
     * 获取前后特定几天
     * @param calendarDTO
     * @return  前4的日期
     */
    @PostMapping(value = "/getSpecialDay", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<String> getSpecialDay(@RequestBody @Valid CalendarSpecialDTO calendarDTO) {
        return CommonResult.success( calendarService.getSpecialDay(calendarDTO));
    }



    /**
     * @param calendarDateDTO
     * @return
     */
    @ApiOperation(value = " 获取从哪到哪的日期", notes = "")
    @PostMapping(value = "/getDate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<List<String>> getDate(@RequestBody @Valid CalendarDateDTO calendarDateDTO) {
        return CommonResult.success(calendarService.getDate(calendarDateDTO));
    }


    /**
     * 获取日历信息
     * @param calendarDTO
     * @return
     */
    @PostMapping(value = "/getCalendarList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<List<AuthCalendar>> getCalendarList(@RequestBody @Valid CalendarDateDTO calendarDTO) {
        return CommonResult.success( calendarService.getCalendarList(calendarDTO));
    }

    /**
     * 获取相差天数：
     * 股票，只算交易日,
     */
    @PostMapping(value = "/getSubtractDay", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult<Integer> getSubtractDay(@RequestBody @Valid CalendarDateDTO dto) {

        return CommonResult.success(calendarService.getSubtractDay(dto));
    }


}
