package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.entity.ProxyIp;
import com.coatardbul.stock.model.dto.DongFangPlateDTO;
import com.coatardbul.stock.model.dto.StockBaseDTO;
import com.coatardbul.stock.service.statistic.DongFangPlateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/10
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "基础相关相关")
@RequestMapping("/dongFangPlate")
public class DongFangController {

    @Resource
    DongFangPlateService dongFangPlateService;


    @ApiOperation("获取所有板块信息")
    @RequestMapping(path = "/getAllPlate", method = RequestMethod.POST)
    public CommonResult getAllIps() {
        return CommonResult.success(dongFangPlateService.getAllPlate());
    }
    @ApiOperation("将股票信息添加到板块中")

    @RequestMapping(path = "/addPlateInfo", method = RequestMethod.POST)
    public CommonResult addPlateInfo(@RequestBody DongFangPlateDTO dto) {
        dongFangPlateService.addPlateInfo(dto);
        return CommonResult.success(null);
    }


    @ApiOperation("将股票信息添加到板块中")
    @RequestMapping(path = "/sysnPlateInfo", method = RequestMethod.POST)
    public CommonResult sysnPlateInfo(@RequestBody DongFangPlateDTO dto) {
        dongFangPlateService.sysnPlateInfo(dto);
        return CommonResult.success(null);
    }

}
