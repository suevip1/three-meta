package com.coatardbul.stock.controller.strategy;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.model.bo.StockUpLimitNameBO;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.service.statistic.StockSpecialStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/4/9
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "特殊策略")
@RequestMapping("/specialStrategy")
public class StockSpecialStrategyController {
@Autowired
StockSpecialStrategyService stockSpecialStrategyService;


    @ApiOperation("获取2板以上涨停数据")
    @RequestMapping(path = "/getUpLimitInfo", method = RequestMethod.POST)
    public CommonResult getUpLimitInfo(@Validated @RequestBody StockEmotionDayDTO dto)  {
        try {
            List<StockUpLimitNameBO> stockUpLimitNameBOS = stockSpecialStrategyService.optimizeTwoAboveUpLimitInfo(dto);
            return CommonResult.success( stockUpLimitNameBOS);

        } catch (IOException e) {
            return CommonResult.failed("");
        }
    }


    @ApiOperation("获取2板以上编辑")
    @RequestMapping(path = "/getUpLimitSign", method = RequestMethod.POST)
    public CommonResult getUpLimitSign(@Validated @RequestBody StockEmotionDayDTO dto)  {
        return CommonResult.success( stockSpecialStrategyService.getUpLimitSign(dto));
    }


    /**
     *获取涨停题材
     */
    @WebLog(value = "")
    @RequestMapping(path = "/getUpLimitTheme", method = RequestMethod.POST)
    public CommonResult getUpLimitTheme(@Validated @RequestBody StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, IOException, IllegalAccessException {
        return CommonResult.success( stockSpecialStrategyService.getUpLimitTheme(dto));
    }



}
