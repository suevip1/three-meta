package com.coatardbul.stock.controller.es;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.entity.bo.es.EsIndustryDataBo;
import com.coatardbul.stock.service.es.EsIndustryDataService;
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
@RequestMapping("/es/industryData")
public class EsIndustryDataController {


    @Autowired
    EsIndustryDataService esIndustryDataService;

    @ApiOperation("获取es总数")
    @RequestMapping(path = "/getCount", method = RequestMethod.POST)
    public CommonResult<Long> getCount(@Validated @RequestBody EsIndustryDataBo dto) throws IOException {
        long count= esIndustryDataService.getCount(dto);
        return CommonResult.success(count);
    }

    @ApiOperation("同步到es上")
    @RequestMapping(path = "/syncData", method = RequestMethod.POST)
    public CommonResult syncData(@Validated @RequestBody EsIndustryDataBo dto) {
        try {
            esIndustryDataService.syncData(dto);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("删除es上数据")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult<Integer> delete(@Validated @RequestBody EsIndustryDataBo dto) {
        esIndustryDataService.deleteEsSync(dto);
        return CommonResult.success(null);
    }


    @ApiOperation("获取es数据")
    @RequestMapping(path = "/getList", method = RequestMethod.POST)
    public CommonResult getList(@Validated @RequestBody EsIndustryDataBo dto) throws IOException {
        List list = esIndustryDataService.getList(dto);
        return CommonResult.success(list);
    }
}
