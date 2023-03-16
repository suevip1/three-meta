package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.service.statistic.DongFangAbnormalMovementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/3/15
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "移动")
@RequestMapping("/abnormalMovement")
public class DongFangAbnormalMovementController {


    @Autowired
    DongFangAbnormalMovementService dongFangAbnormalMovementService;


    @ApiOperation("获取所有异动信息")
    @RequestMapping(path = "/getAllAmInfo", method = RequestMethod.POST)
    public CommonResult getAllPlate(@RequestBody Map map) {
        return CommonResult.success(dongFangAbnormalMovementService.getStockAbnormalMovement(
                map.get("code").toString(),
                map.get("name").toString(),
                map.get("dateStr").toString()));
    }


}
