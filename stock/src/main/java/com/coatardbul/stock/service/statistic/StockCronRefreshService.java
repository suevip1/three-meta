package com.coatardbul.stock.service.statistic;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.TickInfo;
import com.coatardbul.baseService.service.CronRefreshService;
import com.coatardbul.baseService.service.DataServiceBridge;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseService.service.StockUpLimitAnalyzeCommonService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.common.constants.Constant;
import com.coatardbul.stock.feign.SailServerFeign;
import com.coatardbul.stock.feign.TickServerFeign;
import com.coatardbul.stock.model.dto.StockCronRefreshDTO;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Resource
    DataFactory dataFactory;
    @Resource
    public CronRefreshService cronRefreshService;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HttpPoolService httpService;

    @Resource
    TickServerFeign tickServerFeign;
    @Autowired
    SailServerFeign sailServerFeign;
    @Autowired
    StockUpLimitAnalyzeCommonService stockUpLimitAnalyzeCommonService;
    @Autowired
    StockStrategyService stockStrategyService;


    /**
     * 查询redis上所有股票信息
     *
     * @return
     */
    public List getStockInfo(StockCronRefreshDTO dto) {
        Boolean isNow=false;
        List<Map<String, Object>> result = new ArrayList();
        DataServiceBridge dataServiceBridge = dataFactory.build();
        if(!StringUtils.isNotBlank(dto.getDateStr())){
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            isNow=true;
        }
        //获取redis上所有当前时间的key
        Set keys = redisTemplate.keys(RedisKeyUtils.getStockInfoPattern(dto.getDateStr()));
        if (keys.size() > 0) {
            for (Object codeKey : keys) {
                if (codeKey instanceof String) {
                    String stockDetailStr = (String) redisTemplate.opsForValue().get(codeKey.toString());
                    Map stockMap = JsonUtil.readToValue(stockDetailStr, Map.class);
                    //是否当前
                    if(!isNow){
                        //如果有时间，需要根据tick数据动态计算缺省数值
                        String key = RedisKeyUtils.getHisStockTickInfo(dto.getDateStr(), RedisKeyUtils.getCodeByStockInfoKey(codeKey.toString()));
                        String stockTickArrStr = (String) redisTemplate.opsForValue().get(key);
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(stockTickArrStr)) {
                            List<TickInfo> stockTickArr = JsonUtil.readToValue(stockTickArrStr, new TypeReference<List<TickInfo>>() {
                            });
                            //有过滤时间
                            if (stockTickArr.size() > 0 && StringUtils.isNotBlank(dto.getTimeStr())) {
                                stockTickArr= stockTickArr.stream().filter(item-> item.getTime().compareTo(dto.getTimeStr())<=0).collect(Collectors.toList());
                                try {
                                    dataServiceBridge.updateTickInfoToStockInfo(stockTickArr, stockMap);
                                }catch (Exception e){
                                    log.error(e.getMessage());
                                }
                            }
                        }
                    }

                    result.add(stockMap);
                }
            }
        }
        return result;
    }

    /**
     * 获取刷新redis上股票信息
     *
     * @param
     * @return
     */
    public void refreshStockInfo(StockCronRefreshDTO dto) {
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
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);

        if (dateFormat.equals(dto.getDateStr())||!StringUtils.isNotBlank(dto.getDateStr())) {
            stockRefreshprocess(dto);
        } else {
            Constant.onceUpLimitThreadPool.submit(() -> {
                tickServerFeign.refreshTickInfo(dto);
            });
            //历史
            stockRefreshHisProcess(dto);

        }

//        return result;
    }

    public void refreshStockMinuterInfo(List<String> codeArr) {
        stockMinuterRefreshprocess(codeArr);
    }

    private void stockMinuterRefreshprocess(List<String> codes) {
        List<String> codeArr = new ArrayList<>();
        for (String code : codes) {
            codeArr.add(code);
            if (codeArr.size() == cronRefreshService.getBatchNum()) {
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
        if (finalCodeArr.size() > 0) {
            Constant.minuterThreadPool.submit(() -> {
                StockCronRefreshDTO stockCronRefreshDTO = new StockCronRefreshDTO();
                stockCronRefreshDTO.setCodeArr(finalCodeArr);
                sailServerFeign.refreshStockMinuterInfo(stockCronRefreshDTO);
            });
        }
    }

    private void stockRefreshHisProcess(StockCronRefreshDTO dto) {
        List<String> codes = dto.getCodeArr();
        List<String> codeArr = new ArrayList<>();
        for (String code : codes) {
            codeArr.add(code);
            if (codeArr.size() == cronRefreshService.getBatchNum()) {
                List<String> finalCodeArr = codeArr;
                Constant.immediateThreadPool.submit(() -> {
                    StockCronRefreshDTO stockCronRefreshDTO = new StockCronRefreshDTO();
                    stockCronRefreshDTO.setCodeArr(finalCodeArr);
                    stockCronRefreshDTO.setDateStr(dto.getDateStr());
                    sailServerFeign.refreshHisStockInfo(stockCronRefreshDTO);
                });
                codeArr = new ArrayList<>();
            }
        }
        List<String> finalCodeArr = codeArr;
        if (finalCodeArr.size() > 0) {
            Constant.immediateThreadPool.submit(() -> {
                StockCronRefreshDTO stockCronRefreshDTO = new StockCronRefreshDTO();
                stockCronRefreshDTO.setCodeArr(finalCodeArr);
                stockCronRefreshDTO.setDateStr(dto.getDateStr());
                sailServerFeign.refreshHisStockInfo(stockCronRefreshDTO);
            });
        }
    }

    private void stockRefreshprocess(StockCronRefreshDTO dto) {
        List<String> codes = dto.getCodeArr();
        List<String> codeArr = new ArrayList<>();
        for (String code : codes) {
            codeArr.add(code);
            if (codeArr.size() == cronRefreshService.getBatchNum()) {
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
        if (finalCodeArr.size() > 0) {
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
     * @param dateStr
     * @return
     */
    public void deleteStockInfo(List<String> codes, String dateStr) {
        if(!StringUtils.isNotBlank(dateStr)){
            dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        }
        for (String code : codes) {
            String key = RedisKeyUtils.getHisStockInfo(dateStr,code);
            Boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                redisTemplate.delete(key);
                redisTemplate.delete(RedisKeyUtils.getHisStockTickInfo(dateStr,code));
            }
        }
    }


    /**
     * 定时刷新
     */
    public void cronRefresh() {
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        //获取redis上所有当前时间的key
        Set keys = redisTemplate.keys(RedisKeyUtils.getStockInfoPattern(dateFormat));

        if (keys.size() > 0) {
            List<String> codes = new ArrayList<String>();
            for (Object codeKey : keys) {
                if (codeKey instanceof String) {
                    String key = codeKey.toString();
                    String code = RedisKeyUtils.getCodeByStockInfoKey(key);
                    codes.add(code);
                }
            }
            StockCronRefreshDTO stockRefreshprocess = new StockCronRefreshDTO();
            stockRefreshprocess.setDateStr(dateFormat);
            stockRefreshprocess.setCodeArr(codes);
            stockRefreshprocess(stockRefreshprocess);
        }

    }


    public List getTickInfo(StockCronRefreshDTO dto) {
        String key = RedisKeyUtils.getHisStockTickInfo(dto.getDateStr(),dto.getCodeArr().get(0));
        String jsonStr = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(jsonStr)) {
            return JsonUtil.readToValue(jsonStr, new TypeReference<List<Map>>() {
            });
        }
        return new ArrayList();

    }

    public List getMinuterInfo(StockCronRefreshDTO dto) {

        String key = RedisKeyUtils.getHisStockMinuterInfo(dto.getDateStr(),dto.getCodeArr().get(0));
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


    public void refreshHisStockTickInfo(StockCronRefreshDTO dto) {
        tickServerFeign.refreshTickInfo(dto);
    }
}
