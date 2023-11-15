package com.coatardbul.stock.controller.es;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.dto.EsTemplateConfigDTO;
import com.coatardbul.baseService.service.EsTemplateDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Note:es上同步模板数据,日期+模板id+code,模板id，日期，code ,name,概念，行业前缀，行业，参数（保留字段），数据json
 * <p>
 * 1.按照单个时间同步，按照多个时间同步
 * <p>
 * Date: 2022/3/14
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "ip相关")
@RequestMapping("/es/templateData")
public class EsTemplateDataController {
    @Autowired
    EsTemplateDataService esTemplateDataService;


    @ApiOperation("获取es总数")
    @RequestMapping(path = "/getCount", method = RequestMethod.POST)
    public CommonResult<Long> getCount(@Validated @RequestBody EsTemplateConfigDTO dto) {
       long count= esTemplateDataService.getCount(dto);
        return CommonResult.success(count);
    }

    @ApiOperation("同步到es上")
    @RequestMapping(path = "/syncData", method = RequestMethod.POST)
    public CommonResult syncData(@Validated @RequestBody EsTemplateConfigDTO dto) {
        try {
            esTemplateDataService.syncData(dto);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("同步范围数据到es上")
    @RequestMapping(path = "/syncRangeData", method = RequestMethod.POST)
    public CommonResult syncRangeData(@Validated @RequestBody Map map) {
        try {
            List dateArrInfo = (List) map.get("dateArrInfo");
            List filterEsTemplateConfigList = (List) map.get("filterEsTemplateConfigList");

            esTemplateDataService.syncRangeData(dateArrInfo, filterEsTemplateConfigList);

            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("删除es上数据")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult<Integer> delete(@Validated @RequestBody EsTemplateConfigDTO dto) {
        esTemplateDataService.deleteEsSync(dto);
        return CommonResult.success(null);
    }


}
