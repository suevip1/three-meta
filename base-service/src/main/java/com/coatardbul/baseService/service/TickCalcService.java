package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.TickInfo;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:tick数据计算服务
 * <p>
 * Date: 2022/11/23
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class TickCalcService {


    @Autowired
    RedisTemplate redisTemplate;


    /**
     * @param code
     * @return
     */
    public String getCalcTickInfo(String code) {
        String nowStockTickInfo = RedisKeyUtils.getNowStockTickInfo(code);
        String tickInfoArr = (String) redisTemplate.opsForValue().get(nowStockTickInfo);
        if (StringUtils.isNotBlank(tickInfoArr)) {
            List<TickInfo> maps = JsonUtil.readToValue(tickInfoArr, new TypeReference<List<TickInfo>>() {
            });
            List<TickInfo> newSortArr = maps.stream().sorted(Comparator.comparing(TickInfo::getTime)).collect(Collectors.toList());

            //前一分钟判断
            List<TickInfo> tickInfos = newSortArr.subList(0, 20);

        }


        return null;
    }

}
