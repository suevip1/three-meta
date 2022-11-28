package com.coatardbul.sail.service;

import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseCommon.constants.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/16
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class ThreadPoolConfigService {

    @Resource
    HttpPoolService httpPoolService;

    public Map getDataThreadPoolConfig() {
        Map<String,Object>map = new HashMap();
        map.put("poolSize", Constant.dataThreadPool.getPoolSize());
        map.put("corePoolSize", Constant.dataThreadPool.getCorePoolSize());
        map.put("maximumPoolSize", Constant.dataThreadPool.getMaximumPoolSize());
        map.put("largestPoolSize", Constant.dataThreadPool.getLargestPoolSize());
        map.put("activeCount", Constant.dataThreadPool.getActiveCount());
        map.put("taskCount", Constant.dataThreadPool.getTaskCount());
        map.put("completedTaskCount", Constant.dataThreadPool.getCompletedTaskCount());
        map.put("queueSize", Constant.dataThreadPool.getQueue().size());
        map.put("keepAliveTimeMinutes", Constant.dataThreadPool.getKeepAliveTime(TimeUnit.MINUTES));
        return map;
    }


    public void closeAndRestart(){
        httpPoolService.closeConnection();
        httpPoolService.reInit();
    }
}
