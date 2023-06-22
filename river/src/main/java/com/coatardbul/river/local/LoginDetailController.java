package com.coatardbul.river.local;

import com.coatardbul.river.common.annotation.WebLog;
import com.coatardbul.river.common.api.CommonResult;
import com.coatardbul.river.model.entity.LoginDetail;
import com.coatardbul.river.service.LoginDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/22
 *
 * @author Su Xiaolei
 */
@Api(value = "登陆信息")
@WebLog("1111111")
@Slf4j
@RestController
@RequestMapping(value = "/loginDetail")
public class LoginDetailController {
    @Autowired
    LoginDetailService loginDetailMapper;

    @ApiOperation(value = "add", notes = "add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult add(@RequestBody @Valid LoginDetail loginDetail) {
//        Boolean token = userInfoService.verifyUserValid(userDto);
        return CommonResult.success(null);
    }

    @ApiOperation(value = "记录信息", notes = "记录信息")
    @RequestMapping(value = "/logLoginInfo", method = RequestMethod.POST)
    public CommonResult logLoginInfo(@RequestBody @Valid HttpServletRequest request) {
        loginDetailMapper.logLoginInfo(request);
        return CommonResult.success(null);
    }
}
