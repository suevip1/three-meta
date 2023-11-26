package com.coatardbul.stock.controller.msg;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.bo.DingDingMsgBo;
import com.coatardbul.stock.service.msg.DingDingService;
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
 * Date: 2023/11/25
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "模板配置")
@RequestMapping("/msgAck")
public class MsgAckController {

    @Autowired
    DingDingService dingDingService;

    @ApiOperation("发送钉钉消息")
    @RequestMapping(path = "/dingDing/sendMsg", method = RequestMethod.POST)
    public CommonResult sendDingDingMsg(@Validated @RequestBody DingDingMsgBo dto) {
        try {
            String str = dingDingService.sendMsg(dto);
            return CommonResult.success(str);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
