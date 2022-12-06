package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.constants.DataSourceEnum;
import com.coatardbul.baseCommon.constants.IsNotEnum;
import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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


    private final static String IS_PROXY = "isProxy";

    private final static String IS_OPEN_CRON_REFRESH = "isOpenCronRefresh";

    private final static String CODE_EXIST_HOUR = "codeExistHour";


    private final static String DATA_SOURCE = "dataSource";


    private final static String SOCKET_TIMEOUT = "sockTimeout";


    private final static String BATCH_NUM = "batchNum";

    private final static String ALI_APP_CODE = "aliAppCode";

    private final static Integer CONFIG_EXIST_DAY = 30;

    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    private void setXlcjCronRefreshConfigBo() {
        cronRefreshConfigBo = new CronRefreshConfigBo();
        //代理
        cronRefreshConfigBo.setIsProxy(getProxyFlag());
        //code保存持续时间
        cronRefreshConfigBo.setCodeExistHour(getCodeExistHour());
        //定时刷新标识
        cronRefreshConfigBo.setIsOpenCronRefreshFlag(getIsOpenCronRefreshFlag());
        //数据来源
        cronRefreshConfigBo.setDataSource(getDataSource());
        //socket超时时间
        cronRefreshConfigBo.setSockTimeout(getSockTimeout());
        //批次数量
        cronRefreshConfigBo.setBatchNum(getBatchNum());
        //阿里appcode
        cronRefreshConfigBo.setAliAppCode(getAliAppCode());
    }


    public CronRefreshConfigBo getCronRefreshConfigBo() {
        CronRefreshConfigBo configBo = new CronRefreshConfigBo();
        configBo.setIsProxy(getProxyFlag());
        configBo.setCodeExistHour(getCodeExistHour());
        configBo.setIsOpenCronRefreshFlag(getIsOpenCronRefreshFlag());
        configBo.setDataSource(getDataSource());
        configBo.setSockTimeout(getSockTimeout());
        configBo.setBatchNum(getBatchNum());
        configBo.setAliAppCode(getAliAppCode());
        return configBo;
    }


    public void setCronRefreshConfigBo(CronRefreshConfigBo tempObj) {
        setProxyFlag(tempObj.getIsProxy());
        setIsOpenCronRefreshFlag(tempObj.getIsOpenCronRefreshFlag());
        setCodeExistHour(tempObj.getCodeExistHour());
        setDataSource(tempObj.getDataSource());
        setSockTimeout(tempObj.getSockTimeout());
        setBatchNum(tempObj.getBatchNum());
        setAliAppCode(tempObj.getAliAppCode());
    }

    public boolean getProxyFlag() {
        Boolean hasKey = redisTemplate.hasKey(IS_PROXY);
        if (hasKey) {
            String isProxyStr = (String) redisTemplate.opsForValue().get(IS_PROXY);
            return Objects.equals(Integer.valueOf(isProxyStr), IsNotEnum.YES.getType());
        } else {
            //默认走代理
            return true;
        }
    }

    private void setProxyFlag(Boolean proxyFlag) {
        if (proxyFlag) {
            redisTemplate.opsForValue().set(IS_PROXY, IsNotEnum.YES.getType().toString(), CONFIG_EXIST_DAY, TimeUnit.DAYS);
        } else {
            //默认走代理
            redisTemplate.opsForValue().set(IS_PROXY, IsNotEnum.NO.getType().toString(), CONFIG_EXIST_DAY, TimeUnit.DAYS);
        }
    }

    private void setCodeExistHour(Integer codeExistHour) {
        redisTemplate.opsForValue().set(CODE_EXIST_HOUR, codeExistHour.toString(), CONFIG_EXIST_DAY, TimeUnit.DAYS);
    }

    private Integer getCodeExistHour() {
        Boolean hasKey = redisTemplate.hasKey(CODE_EXIST_HOUR);
        if (hasKey) {
            String codeExistHourStr = (String) redisTemplate.opsForValue().get(CODE_EXIST_HOUR);
            return Integer.valueOf(codeExistHourStr);
        } else {
            //默认
            return 3;
        }
    }


    private void setIsOpenCronRefreshFlag(Boolean isOpenCronRefreshFlag) {
        if (isOpenCronRefreshFlag) {
            redisTemplate.opsForValue().set(IS_OPEN_CRON_REFRESH, IsNotEnum.YES.getType().toString(), CONFIG_EXIST_DAY, TimeUnit.DAYS);
        } else {
            //默认走代理
            redisTemplate.opsForValue().set(IS_OPEN_CRON_REFRESH, IsNotEnum.NO.getType().toString(), CONFIG_EXIST_DAY, TimeUnit.DAYS);
        }
    }

    public boolean getIsOpenCronRefreshFlag() {
        Boolean hasCronKey = redisTemplate.hasKey(IS_OPEN_CRON_REFRESH);
        if (hasCronKey) {
            String isCronRefreshStr = (String) redisTemplate.opsForValue().get(IS_OPEN_CRON_REFRESH);
            return Objects.equals(Integer.valueOf(isCronRefreshStr), IsNotEnum.YES.getType());
        } else {
            //默认刷新
            return true;
        }
    }

    private void setDataSource(String dataSource) {
        redisTemplate.opsForValue().set(DATA_SOURCE, dataSource, CONFIG_EXIST_DAY, TimeUnit.DAYS);

    }

    public String getDataSource() {
        Boolean hasCronKey = redisTemplate.hasKey(DATA_SOURCE);
        if (hasCronKey) {
            return (String) redisTemplate.opsForValue().get(DATA_SOURCE);
        } else {
            //默认
            return DataSourceEnum.XIN_LANG.getSign();
        }
    }


    private void setSockTimeout(Integer socketTimeout) {
        redisTemplate.opsForValue().set(SOCKET_TIMEOUT, socketTimeout.toString(), CONFIG_EXIST_DAY, TimeUnit.DAYS);
    }

    public Integer getSockTimeout() {
        Boolean hasKey = redisTemplate.hasKey(SOCKET_TIMEOUT);
        if (hasKey) {
            String sockTimeoutStr = (String) redisTemplate.opsForValue().get(SOCKET_TIMEOUT);
            return Integer.valueOf(sockTimeoutStr);
        } else {
            //默认
            return 5000;
        }
    }

    private void setBatchNum(Integer batchNum) {
        redisTemplate.opsForValue().set(BATCH_NUM, batchNum.toString(), CONFIG_EXIST_DAY, TimeUnit.DAYS);
    }

    public Integer getBatchNum() {
        Boolean hasKey = redisTemplate.hasKey(BATCH_NUM);
        if (hasKey) {
            String sockTimeoutStr = (String) redisTemplate.opsForValue().get(BATCH_NUM);
            return Integer.valueOf(sockTimeoutStr);
        } else {
            //默认
            return 5;
        }
    }

    private void setAliAppCode(String aliAppCode) {
        redisTemplate.opsForValue().set(ALI_APP_CODE, aliAppCode, CONFIG_EXIST_DAY, TimeUnit.DAYS);
    }

    public String getAliAppCode() {
        Boolean hasKey = redisTemplate.hasKey(ALI_APP_CODE);
        if (hasKey) {
            return (String) redisTemplate.opsForValue().get(ALI_APP_CODE);
        } else {
            //默认
            return "46bb5bf04acf450eb90260f292f647a9";
        }
    }


}
