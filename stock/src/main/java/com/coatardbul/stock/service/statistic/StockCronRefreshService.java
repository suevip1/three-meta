package com.coatardbul.stock.service.statistic;

import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.common.constants.Constant;
import com.coatardbul.stock.common.constants.DataSourceEnum;
import com.coatardbul.stock.common.constants.IsNotEnum;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.common.api.CommonResult;
import com.coatardbul.stock.common.util.DateTimeUtil;
import com.coatardbul.stock.common.util.JsonUtil;
import com.coatardbul.stock.feign.SailServerFeign;
import com.coatardbul.stock.model.bo.CronRefreshConfigBo;
import com.coatardbul.stock.model.dto.StockCronRefreshDTO;
import com.coatardbul.stock.service.statistic.uplimitAnalyze.StockUpLimitAnalyzeService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
public class StockCronRefreshService {
    private CronRefreshConfigBo cronRefreshConfigBo;


    private final static String IS_PROXY = "isProxy";

    private final static String IS_OPEN_CRON_REFRESH = "isOpenCronRefresh";

    private final static String CODE_EXIST_HOUR = "codeExistHour";


    private final static String DATA_SOURCE = "dataSource";


    private final static String SOCKET_TIMEOUT = "sockTimeout";


    private final static String BATCH_NUM = "batchNum";

    private final static Integer CONFIG_EXIST_DAY = 30;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HttpPoolService httpService;

    @Autowired
    SailServerFeign sailServerFeign;
    @Autowired
    StockUpLimitAnalyzeService stockUpLimitAnalyzeService;
    @Autowired
    StockStrategyService stockStrategyService;


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
    }


    public CronRefreshConfigBo getCronRefreshConfigBo() {
        CronRefreshConfigBo configBo = new CronRefreshConfigBo();
        configBo.setIsProxy(getProxyFlag());
        configBo.setCodeExistHour(getCodeExistHour());
        configBo.setIsOpenCronRefreshFlag(getIsOpenCronRefreshFlag());
        configBo.setDataSource(getDataSource());
        configBo.setSockTimeout(getSockTimeout());
        configBo.setBatchNum(getBatchNum());
        return configBo;
    }


    public void setCronRefreshConfigBo(CronRefreshConfigBo tempObj) {
        setProxyFlag(tempObj.getIsProxy());
        setIsOpenCronRefreshFlag(tempObj.getIsOpenCronRefreshFlag());
        setCodeExistHour(tempObj.getCodeExistHour());
        setDataSource(tempObj.getDataSource());
        setSockTimeout(tempObj.getSockTimeout());
        setBatchNum(tempObj.getBatchNum());
    }

    private boolean getProxyFlag() {
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

    private String getDataSource() {
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

    private Integer getBatchNum() {
        Boolean hasKey = redisTemplate.hasKey(BATCH_NUM);
        if (hasKey) {
            String sockTimeoutStr = (String) redisTemplate.opsForValue().get(BATCH_NUM);
            return Integer.valueOf(sockTimeoutStr);
        } else {
            //默认
            return 5;
        }
    }


    /**
     * 查询redis上所有股票信息
     *
     * @return
     */
    public List getStockInfo() {
        List<Map<String, Object>> result = new ArrayList();
        //获取redis上所有当前时间的key
        Set keys = redisTemplate.keys(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD) + "*");
        if (keys.size() > 0) {
            for (Object codeKey : keys) {
                if (codeKey instanceof String) {
                    String key = codeKey.toString();
                    String stockDetailStr = (String) redisTemplate.opsForValue().get(key);
                    Map stockMap = JsonUtil.readToValue(stockDetailStr, Map.class);
                    result.add(stockMap);
                }
            }
        }
        return result;
    }

    /**
     * 获取刷新redis上股票信息
     *
     * @param codes
     * @return
     */
    public void refreshStockInfo(List<String> codes) {
//        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
//        List<Map<String, Object>> result = new ArrayList();
//        List<String> codeArr = new ArrayList<>();
//        for (String code : codes) {
//            String key = dateFormat + "_" + code;
//            Boolean hasKey = redisTemplate.hasKey(key);
//            if (hasKey) {
//                String stockDetailStr = (String) redisTemplate.opsForValue().get(key);
//                Map map = JsonUtil.readToValue(stockDetailStr, Map.class);
//                result.add(map);
//            } else {
//                Map<String, Object> map = new HashMap<>();
//                map.put("code", code);
//                result.add(map);
//            }
//        }
        stockRefreshprocess(codes);
//        return result;
    }

    public void refreshStockMinuterInfo(List<String> codeArr) {
        stockMinuterRefreshprocess(codeArr);
    }

    private void stockMinuterRefreshprocess(List<String> codes) {
        List<String> codeArr = new ArrayList<>();
        for (String code : codes) {
            codeArr.add(code);
            if (codeArr.size() == cronRefreshConfigBo.getBatchNum()) {
                List<String> finalCodeArr = codeArr;
                Constant.minuterThreadPool.submit(() -> {
                    StockCronRefreshDTO stockCronRefreshDTO = new StockCronRefreshDTO();
                    stockCronRefreshDTO.setCodeArr(finalCodeArr);
                    sailServerFeign.refreshStockMinuterInfo(stockCronRefreshDTO);
                });
                codeArr = new ArrayList<>();
            }
        }
        List<String> finalCodeArr = codeArr;
        if(finalCodeArr.size()>0){
            Constant.minuterThreadPool.submit(() -> {
                StockCronRefreshDTO stockCronRefreshDTO = new StockCronRefreshDTO();
                stockCronRefreshDTO.setCodeArr(finalCodeArr);
                sailServerFeign.refreshStockMinuterInfo(stockCronRefreshDTO);
            });
        }
    }

    private void stockRefreshprocess(List<String> codes) {
        List<String> codeArr = new ArrayList<>();
        for (String code : codes) {
            codeArr.add(code);
            if (codeArr.size() == cronRefreshConfigBo.getBatchNum()) {
                List<String> finalCodeArr = codeArr;
                Constant.immediateThreadPool.submit(() -> {
                    StockCronRefreshDTO stockCronRefreshDTO = new StockCronRefreshDTO();
                    stockCronRefreshDTO.setCodeArr(finalCodeArr);
                    sailServerFeign.refreshStockInfo(stockCronRefreshDTO);
                });
                codeArr = new ArrayList<>();
            }
        }
        List<String> finalCodeArr = codeArr;
        if(finalCodeArr.size()>0){
            Constant.immediateThreadPool.submit(() -> {
                StockCronRefreshDTO stockCronRefreshDTO = new StockCronRefreshDTO();
                stockCronRefreshDTO.setCodeArr(finalCodeArr);
                sailServerFeign.refreshStockInfo(stockCronRefreshDTO);
            });
        }
    }


    /**
     * 删除redis上股票信息
     *
     * @param codes
     * @return
     */
    public void deleteStockInfo(List<String> codes) {
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        for (String code : codes) {
            String key = dateFormat + "_" + code;
            Boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                redisTemplate.delete(key);
                redisTemplate.delete("tick_" + code);
            }
        }
    }


    /**
     * 定时刷新
     */
    public void cronRefresh() {
        //获取redis上所有当前时间的key
        Set keys = redisTemplate.keys(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD) + "*");

        if (keys.size() > 0) {
            List<String> codes = new ArrayList<String>();
            for (Object codeKey : keys) {
                if (codeKey instanceof String) {
                    String key = codeKey.toString();
                    String code = key.substring(11, 17);
                    codes.add(code);
                }
            }
            stockRefreshprocess(codes);
        }

    }


    public List getTickInfo(String code) {
        String key = "tick_" + code;
        String jsonStr = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(jsonStr)) {
            return JsonUtil.readToValue(jsonStr, new TypeReference<List<Map>>() {
            });
        }
        return new ArrayList();

    }

    public List getMinuterInfo(String code) {

        String key = RedisKeyUtils.getNowStockMinuterInfo(code);
        String jsonStr = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(jsonStr)) {
            return JsonUtil.readToValue(jsonStr, new TypeReference<List<Map>>() {
            });
        }
        return new ArrayList();

    }


    public Object getDataThreadPoolConfig() {
        CommonResult threadPoolConfig = sailServerFeign.getThreadPoolConfig();
        return threadPoolConfig.getData();
    }



}
