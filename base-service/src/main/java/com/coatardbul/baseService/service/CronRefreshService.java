package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.constants.DataSourceEnum;
import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Note:  采用新浪财经数据，将股票信息同步到redis上，以redis为数据库，进行信息同步
 * <p>
 * Date: 2022/10/23
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class CronRefreshService {
    private CronRefreshConfigBo cronRefreshConfigBo;


    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    private void setXlcjCronRefreshConfigBo() {
        cronRefreshConfigBo = getRedisCronRefreshConfigBo();
    }


    /**
     * 获取默认配置，不走redis，每个参数有默认值
     * @return
     */
    public CronRefreshConfigBo defaultConfigBo() {
        CronRefreshConfigBo cronRefreshConfigBo = new CronRefreshConfigBo();
        //代理
        cronRefreshConfigBo.setIsProxy(true);
        //code保存持续时间
        cronRefreshConfigBo.setCodeExistHour(24);
        //定时刷新标识
        cronRefreshConfigBo.setIsOpenCronRefreshFlag(true);
        //数据来源
        cronRefreshConfigBo.setDataSource(DataSourceEnum.DONG_FANG.getSign());
        //socket超时时间
        cronRefreshConfigBo.setSockTimeout(5000);
        //批次数量
        cronRefreshConfigBo.setBatchNum(5);
        //阿里appcode
        cronRefreshConfigBo.setAliAppCode("46bb5bf04acf450eb90260f292f647a9");

        cronRefreshConfigBo.setCronAmBeginTime("09:30");
        cronRefreshConfigBo.setCronAmEndTime("11:30");
        cronRefreshConfigBo.setCronPmBeginTime("13:00");
        cronRefreshConfigBo.setCronPmEndTime("15:00");

        return cronRefreshConfigBo;
    }


    /**
     * 快捷获取配置数据
     * @return
     */
    public CronRefreshConfigBo getCronRefreshConfigBo() {
        if(cronRefreshConfigBo!=null){
            return cronRefreshConfigBo;
        }
        return getRedisCronRefreshConfigBo();
    }

    /**
     * 获取redis上的数据，没有走默认值
     * @return
     */
    public CronRefreshConfigBo getRedisCronRefreshConfigBo() {
        String cronSystemConfigKey = RedisKeyUtils.getCronSystemConfig();
        try {
            String cronSystemConfigValue = (String) redisTemplate.opsForValue().get(cronSystemConfigKey);
            if (StringUtils.isNotBlank(cronSystemConfigValue)) {
                CronRefreshConfigBo configBo = JsonUtil.readToValue(cronSystemConfigValue, CronRefreshConfigBo.class);
                return configBo;
            }else {
                return defaultConfigBo();
            }
            //由于超时，命令终端，其他引起的redis异常
        }catch (RedisSystemException e){
            log.error("获取CronRefreshConfigBo时redis异常"+e.getMessage());
            return defaultConfigBo();
        }
    }

    /**
     *
     * @param tempObj
     */
    public void modifyCronRefreshConfigBo(CronRefreshConfigBo tempObj) {
        if(tempObj!=null){
            redisTemplate.opsForValue().set(RedisKeyUtils.getCronSystemConfig(), JsonUtil.toJson(tempObj));
            cronRefreshConfigBo=tempObj;
        }
    }

    public boolean getProxyFlag() {
       return getCronRefreshConfigBo().getIsProxy();
    }

    public Integer getCodeExistHour() {
        return getCronRefreshConfigBo().getCodeExistHour();
    }

    public boolean getIsOpenCronRefreshFlag() {
        return getCronRefreshConfigBo().getIsOpenCronRefreshFlag();
    }


    public String getDataSource() {
        return getCronRefreshConfigBo().getDataSource();
    }


    public Integer getSockTimeout() {
        return getCronRefreshConfigBo().getSockTimeout();
    }



    public Integer getBatchNum() {
        return getCronRefreshConfigBo().getBatchNum();
    }



    public String getAliAppCode() {
        return getCronRefreshConfigBo().getAliAppCode();
    }

    public String getCronAmBeginTime() {
        return getCronRefreshConfigBo().getCronAmBeginTime();
    }



    public String getCronAmEndTime() {
        return getCronRefreshConfigBo().getCronAmEndTime();
    }



    public String getCronPmBeginTime() {
        return getCronRefreshConfigBo().getCronPmBeginTime();
    }



    public String getCronPmEndTime() {
        return getCronRefreshConfigBo().getCronPmEndTime();
    }
}
