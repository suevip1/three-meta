package com.coatardbul.stock.controller;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.dto.StockCookieDTO;
import com.coatardbul.stock.service.statistic.StockCookieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/1/15
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "同花顺cookie")
@RequestMapping("/cookie")
public class StockCookieController {

    @Autowired
    StockCookieService stockCookieService;



    /**
     * 修改同时更新缓存
     * @param dto
     * @return
     */
    @ApiOperation(value = "cookie修改，更新缓存中的cookie信息")
    @RequestMapping(path = "/simpleModify", method = RequestMethod.POST)
    public CommonResult simpleModify(@Validated @RequestBody StockCookieDTO dto) {
        stockCookieService.simpleModify(dto);
        return CommonResult.success(null);
    }




}
