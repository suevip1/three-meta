package com.coatardbul.stock.controller.es;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.dto.EsDayKlineDto;
import com.coatardbul.stock.service.es.EsDayKLineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/13
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "模板配置")
@RequestMapping("/es/dayKLine")
public class EsDayKLineController {


    @Autowired
    EsDayKLineService esDayKLineService;

    @ApiOperation("获取es总数")
    @RequestMapping(path = "/getCount", method = RequestMethod.POST)
    public CommonResult<Long> getCount(@Validated @RequestBody EsDayKlineDto dto) throws IOException {
        long count= esDayKLineService.getCount(dto);
        return CommonResult.success(count);
    }

    @ApiOperation("同步到es上")
    @RequestMapping(path = "/syncData", method = RequestMethod.POST)
    public CommonResult syncData(@Validated @RequestBody EsDayKlineDto dto) {
        try {
            esDayKLineService.syncData(dto);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }
    @ApiOperation("同步当日全部到es上")
    @RequestMapping(path = "/syncTodayData", method = RequestMethod.POST)
    public CommonResult syncTodayData( @RequestBody Object dto) {
        try {
            esDayKLineService.syncTodayData();
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("删除es上数据")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult<Integer> delete(@Validated @RequestBody EsDayKlineDto dto) {
        esDayKLineService.deleteEsSync(dto);
        return CommonResult.success(null);
    }


    @ApiOperation("获取es数据")
    @RequestMapping(path = "/getList", method = RequestMethod.POST)
    public CommonResult getList(@Validated @RequestBody EsDayKlineDto dto) throws IOException {
        List list = esDayKLineService.getDateList(dto);
        return CommonResult.success(list);
    }

    @ApiOperation("获取es范围数据")
    @RequestMapping(path = "/getRangeList", method = RequestMethod.POST)
    public CommonResult getRangeList(@Validated @RequestBody EsDayKlineDto dto) throws IOException {
        List list = esDayKLineService.getRangeList(dto);
        return CommonResult.success(list);
    }

}
