package com.coatardbul.sail.controller;

import com.coatardbul.sail.common.api.CommonResult;
import com.coatardbul.sail.model.dto.StockCronRefreshDTO;
import com.coatardbul.sail.model.dto.StockStrategyQueryDTO;
import com.coatardbul.sail.service.stockData.DongFangService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.script.ScriptException;
import java.io.FileNotFoundException;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/25
 *
 * @author Su Xiaolei
 */

@Slf4j
@RestController
@Api(tags = "股票查询")
@RequestMapping("/test")
public class TestController {

    @Resource
    DongFangService dongFangService;
    /**
     * 同花顺新版问财功能
     *
     * @param dto 基础查询对象，支持id和问句查询
     * @return
     */
    @RequestMapping(path = "/test", method = RequestMethod.POST)
    public CommonResult strategy(@Validated @RequestBody StockCronRefreshDTO dto) throws NoSuchMethodException, ScriptException, FileNotFoundException {
        dongFangService.getAndRefreshStockInfo(dto.getCodeArr().get(0));

        return CommonResult.success(null);
    }

}
