package com.coatardbul.stock.controller.api;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.service.base.CurlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ConnectTimeoutException;
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
 * Date: 2023/12/4
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "基础相关相关")
@RequestMapping("/request/api")
public class RequestApiConfigController {



    @Autowired
    CurlService curlService;
    @ApiOperation("发送请求")
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public CommonResult<Map>  send(@RequestBody Map map) throws ConnectTimeoutException {
        Map s = curlService.parseCurl(map.get("curl").toString());
        return CommonResult.success(s);
    }

}
