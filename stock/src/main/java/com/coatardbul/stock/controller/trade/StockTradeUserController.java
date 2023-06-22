package com.coatardbul.stock.controller.trade;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.common.annotation.WebLog;
import com.coatardbul.stock.model.dto.StockTradeLoginDTO;
import com.coatardbul.stock.model.dto.StockUserCookieDTO;
import com.coatardbul.stock.service.statistic.trade.StockTradeUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/6/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "x")
@RequestMapping("/tradeUser")
public class StockTradeUserController {

    @Autowired
    StockTradeUserService stockTradeUserService;


    /**
     * stockTradeUserService.autoLogin();
     */
    @WebLog(value = "updateCookie")
    @RequestMapping(path = "/updateCookie", method = RequestMethod.POST)
    public CommonResult updateCookie(@Validated @RequestBody StockUserCookieDTO dto) {
        stockTradeUserService.updateCookie(dto);
        return CommonResult.success(dto);
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public CommonResult login(@Validated @RequestBody StockTradeLoginDTO dto) {
        try {
            stockTradeUserService.login(dto);
            return CommonResult.success("登陆成功");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }

    }

    @RequestMapping(path = "/autoLogin", method = RequestMethod.POST)
    public CommonResult autoLogin() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

            stockTradeUserService.autoLogin(request);
            return CommonResult.success("登陆成功");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());

        }
    }

    @RequestMapping(path = "/baseInfo", method = RequestMethod.POST)
    public CommonResult baseInfo(@Validated @RequestBody StockTradeLoginDTO dto) {
        stockTradeUserService.baseInfo(dto.getId());
        return CommonResult.success(null);
    }


}
