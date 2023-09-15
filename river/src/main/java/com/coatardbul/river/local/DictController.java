package com.coatardbul.river.local;


import com.coatardbul.river.common.annotation.WebLog;
import com.coatardbul.river.common.api.CommonResult;
import com.coatardbul.river.mapper.DictInfoMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(value = "用户信息")
@WebLog("1111111")
@Slf4j
@RestController
@RequestMapping(value = "/dict")
public class DictController {
    @Autowired
    DictInfoMapper dictInfoMapper;


    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @RequestMapping(value = "/getInfoByType", method = RequestMethod.POST)
    public CommonResult insertBankAcc(@RequestBody Map map) {

        return CommonResult.success(dictInfoMapper.selectAllByBusiType(map.get("busiType").toString()));

    }


}