package com.coatardbul.river.local;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.river.common.annotation.WebLog;
import com.coatardbul.river.common.api.CommonResult;
import com.coatardbul.river.common.constants.RequestUrlConstant;
import com.coatardbul.river.model.entity.StockTimeInterval;
import com.coatardbul.river.service.StockTimeIntervalService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/9
 *
 * @author Su Xiaolei
 */
@Api(value = "股票间隔时间")
@Slf4j
@WebLog
@RestController
@ControllerAdvice
@RequestMapping(value = RequestUrlConstant.TIME_INTERVAL)
public class StockTimeIntervalCtroller {

    @Autowired
    StockTimeIntervalService stockTimeIntervalService;

    /**
     * 获取所有时间间隔
     * @param
     * @return
     */
    @PostMapping(value = "/getIntervalList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult getIntervalList() throws BusinessException {
        return CommonResult.success( stockTimeIntervalService.getIntervalList());
    }

    /**
     * 获取所有间隔数据
     * @param
     * @return
     */
    @PostMapping(value = "/getTimeList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult getList(@RequestBody @Valid StockTimeInterval dto) throws BusinessException {
        return CommonResult.success( stockTimeIntervalService.getList(dto));
    }

    /**
     * 根据间隔时间刷新，从9.30开始，11.30结束，13:00开始，15:00结束
     * @param dto
     * @return
     * @throws BusinessException
     */
    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public CommonResult refresh(@RequestBody @Valid StockTimeInterval dto) throws BusinessException {
        stockTimeIntervalService.refresh(dto);
        return CommonResult.success(null );
    }

}
