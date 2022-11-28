package com.coatardbul.river.service.impl;

import com.coatardbul.river.common.config.FtpConfig;
import com.coatardbul.river.common.util.FtpUtil;
import com.coatardbul.river.service.FtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2021/12/20
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class FtpServiceImpl implements FtpService {
    @Autowired
    FtpConfig ftpConfig;

    @Override
    public void set() {

        String fileName = "10-8.jpg";
        boolean result = FtpUtil.ftpUpload(fileName, ftpConfig.getUrl(), ftpConfig.getPort(), ftpConfig.getUsername(),
                ftpConfig.getPassword(), ftpConfig.getLocalDir(), ftpConfig.getRemotePath());
        if (result) {
            log.info("=======上传文件" + fileName + "成功=======");
        } else {
            log.info("=======上传文件" + fileName + "失败=======");
        }
    }
}
