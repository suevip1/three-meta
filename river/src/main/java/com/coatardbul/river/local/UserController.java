package com.coatardbul.river.local;


import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.river.common.annotation.WebLog;
import com.coatardbul.river.common.api.CommonResult;
import com.coatardbul.river.model.dto.UserDto;
import com.coatardbul.river.model.entity.AuthUser;
import com.coatardbul.river.service.impl.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(value = "用户信息")
@WebLog("1111111")
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserInfoService userInfoService;
    @Resource
    private SnowFlakeService snowFlakeService;
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public CommonResult insertBankAcc(@RequestBody @Valid UserDto userDto, BindingResult bindResult) {



        // dto对象的非空，长度检查
        if (bindResult.hasErrors()) {
            log.error(bindResult.getFieldError().getDefaultMessage());
            return CommonResult.failed(bindResult.getFieldError().getDefaultMessage());
        }
        try {
            AuthUser userInfo = userInfoService.selectByPrimaryKey("123123");
            return CommonResult.success(userInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "登陆", notes = "获取用户信息")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult login(@RequestBody @Valid UserDto userDto) {
        List<AuthUser> login = userInfoService.login(userDto);
        if (login!=null &&login.size()>0) {
            return CommonResult.success(System.currentTimeMillis());
        } else {
            return CommonResult.failed("用户名称或密码错误");
        }

    }
}