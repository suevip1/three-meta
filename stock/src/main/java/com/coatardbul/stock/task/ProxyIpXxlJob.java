package com.coatardbul.stock.task;

import com.coatardbul.baseService.service.ProxyIpService;
import com.coatardbul.stock.common.util.DateTimeUtil;
import com.coatardbul.stock.common.util.JsonUtil;
import com.coatardbul.stock.mapper.ProxyIpMapper;
import com.coatardbul.baseService.entity.dto.ProxyIpQueryDTO;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/17
 *
 * @author Su Xiaolei
 */
@Slf4j
@Component
public class ProxyIpXxlJob {
    @Autowired
    ProxyIpService proxyIpService;
    @Autowired
    ProxyIpMapper proxyIpMapper;

    @XxlJob("proxyIpJobHandler")
    public void proxyIpJobHandler() throws Exception {
        String param = XxlJobHelper.getJobParam();
        log.info("代理ip定时任务开始,传递参数为：" + param);
        if (StringUtils.isNotBlank(param)) {
            ProxyIpQueryDTO proxyIpQueryDTO = JsonUtil.readToValue(param, ProxyIpQueryDTO.class);
            log.info(",传递参数为：" + proxyIpQueryDTO.toString());
            //删除两分钟之前的数据
            proxyIpMapper.deleteByCreateTimeLessThanEqual(DateTimeUtil.getBeforeDate(2, Calendar.MINUTE));
            proxyIpService.addIpProcess(proxyIpQueryDTO);
        }
        log.info("代理ip定时任务结束");

    }
}
