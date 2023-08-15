package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.entity.feign.CalendarDateDTO;
import com.coatardbul.stock.service.statistic.SeleniumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/7/12
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "移动")
@RequestMapping("/selenium")
public class SeleniumController {
    @Autowired
    SeleniumService seleniumService;
    @ApiOperation("新增股票信息")
    @RequestMapping(path = "/getIncreaseGreate5Range", method = RequestMethod.POST)
    public CommonResult getIncreaseGreate5Range(@RequestBody CalendarDateDTO dto) {
        try {
            seleniumService.getIncreaseGreate5Range(dto);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return CommonResult.success(null);
    }
}
