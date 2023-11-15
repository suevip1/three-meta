package com.coatardbul.stock.controller.es;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.entity.EsTemplateConfig;
import com.coatardbul.stock.service.es.EsTemplateConfigService;
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

/**
 * <p>

 * <p>修改es模板配置，并且同步redis
 * Date: 2022/3/14
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "模板配置")
@RequestMapping("/es/templateConfig")
public class EsTemplateConfigController {
    @Autowired
    EsTemplateConfigService esTemplateConfigService;


    @ApiOperation("查询list")
    @RequestMapping(path = "/getList", method = RequestMethod.POST)
    public CommonResult getList(@Validated @RequestBody EsTemplateConfig dto) {
        try {
            List<EsTemplateConfig> list = esTemplateConfigService.getList(dto);
            return CommonResult.success(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("查询list")
    @RequestMapping(path = "/getQuery", method = RequestMethod.POST)
    public CommonResult getQuery(@Validated @RequestBody EsTemplateConfig dto) {
        try {
            EsTemplateConfig query = esTemplateConfigService.getQuery(dto);
            return CommonResult.success(query);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("新增")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public CommonResult add(@Validated @RequestBody EsTemplateConfig dto) {
        try {
            esTemplateConfigService.add(dto);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("修改")
    @RequestMapping(path = "/modify", method = RequestMethod.POST)
    public CommonResult modify(@Validated @RequestBody EsTemplateConfig dto) {
        try {
            esTemplateConfigService.modify(dto);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("删除es上数据")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult delete(@Validated @RequestBody EsTemplateConfig dto) {
        try {
            esTemplateConfigService.delete(dto);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
