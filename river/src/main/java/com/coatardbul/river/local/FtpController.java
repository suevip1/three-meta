package com.coatardbul.river.local;


import com.coatardbul.river.common.annotation.WebLog;
import com.coatardbul.river.common.constants.RequestUrlConstant;
import com.coatardbul.river.service.FtpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "人行联行号")
@Slf4j
@WebLog
@RestController
@ControllerAdvice
@RequestMapping(value = RequestUrlConstant.FTP)
public class FtpController {
    @Autowired
    FtpService ftpService;

    /**
     * 解析本地的zip文件，将文件中的数据插入到表中
     *
     */
    @ApiOperation(value = "解析本地的zip文件", notes = "")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void test() {
        ftpService.set();
    }



}
