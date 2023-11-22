package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.EsTemplateConfigEnum;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.model.bo.LimitStrongWeakBO;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.EsTemplateConfigDTO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.model.entity.EsTemplateConfig;
import com.coatardbul.baseCommon.model.entity.StockBase;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.es.EsTemplateDataBo;
import com.coatardbul.baseService.service.EsTemplateDataService;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.StockParseAndConvertService;
import com.coatardbul.baseService.service.UpLimitStrongWeakService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.common.constants.Constant;
import com.coatardbul.stock.model.bo.LimitBaseInfoBO;
import com.coatardbul.stock.model.bo.StockUpLimitInfoBO;
import com.coatardbul.stock.model.bo.StockUpLimitNameBO;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.service.base.StockStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/4/9
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockSpecialStrategyService {

    @Autowired
    StockStrategyService stockStrategyService;

    @Autowired
    UpLimitStrongWeakService upLimitStrongWeakService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    SnowFlakeService snowFlakeService;

    @Autowired
    StockParseAndConvertService stockParseAndConvertService;


    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    EsTemplateDataService esTemplateDataService;

    /**
     * 两板及两板以上数据，最高到8板
     *
     * @param dto
     * @return
     */
    public List<StockUpLimitNameBO> getTwoAboveUpLimitInfo(StockEmotionDayDTO dto) {

        List<StockUpLimitNameBO> result = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(7);
        for (int i = 2; i < 9; i++) {
            final int num = i;
            Constant.countDownThreadPool.execute(() -> {

                try {
                    //涨停脚本语句
                    String upLimitNumScript = getUpLimitNumScript(num);
                    List<StockBase> upLimitNames = getlimitInfo(upLimitNumScript, dto.getDateStr());
                    //未连板脚本语句
                    String noUpLimitNumScript = getNoUpLimitNumScript(num);
                    List<StockBase> noUpLimitNames = getlimitInfo(noUpLimitNumScript, dto.getDateStr());
                    StockUpLimitNameBO stockUpLimitNameBO = new StockUpLimitNameBO();
                    stockUpLimitNameBO.setUpLimitNum(num + "板");
                    stockUpLimitNameBO.setUplimitArr(upLimitNames);
                    stockUpLimitNameBO.setNoUplimitArr(noUpLimitNames);
                    if (stockUpLimitNameBO.getUplimitArr().size() > 0) {
                        result.add(stockUpLimitNameBO);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    countDownLatch.countDown();
                }

            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return result.stream().sorted(Comparator.comparing(StockUpLimitNameBO::getUpLimitNum).reversed()).collect(Collectors.toList());
    }

    private List<StockBase> getlimitInfo(String upLimitNumScript, String dateStr) throws ScriptException, IOException, NoSuchMethodException, IllegalAccessException {
        StockStrategyQueryDTO stockStrategyQueryDTO = new StockStrategyQueryDTO();
        stockStrategyQueryDTO.setDateStr(dateStr);
        stockStrategyQueryDTO.setStockTemplateScript(upLimitNumScript);
        //策略查询
        StrategyBO strategy = stockStrategyService.comprehensiveStrategy(stockStrategyQueryDTO);
        JSONArray data = strategy.getData();
        List<StockBase> nameList = new ArrayList<>();
        for (int j = 0; j < data.size(); j++) {
            StockBase stockBase = new StockBase();
            stockBase.setCode(stockParseAndConvertService.getStockCode(data.getJSONObject(j)));
            stockBase.setName(stockParseAndConvertService.getStockName(data.getJSONObject(j)));
            stockBase.setIndustry(stockParseAndConvertService.getStockIndustry(data.getJSONObject(j)));
            stockBase.setTheme(stockParseAndConvertService.getStockTheme(data.getJSONObject(j)));
            nameList.add(stockBase);
        }
        return nameList;

    }


    /**
     * 查询欧
     *
     * @param dto
     * @return
     */
    public List<StockUpLimitNameBO> optimizeTwoAboveUpLimitInfo(StockEmotionDayDTO dto) throws IOException {
        List<StockUpLimitNameBO> result = new ArrayList<>();
        Map<String, List<EsTemplateDataBo>> uplimitMap = new HashMap<String, List<EsTemplateDataBo>>();

        Map<String, List<EsTemplateDataBo>> noUplimitMap = new HashMap<String, List<EsTemplateDataBo>>();

        EsTemplateConfig firstUpLimitTemplateConfig = getEsTemplateConfigByTemplateId(StockTemplateEnum.FIRST_UP_LIMIT.getId());
        EsTemplateConfig upLimitTemplateConfig = getEsTemplateConfigByTemplateId(StockTemplateEnum.UP_LIMIT.getId());


        //可能两板以上的票
        List<EsTemplateDataBo> twoAboveList =null;
        //首次涨停
        List<EsTemplateDataBo> todayFirstUplimit = getEsTemplateConfigByDate(dto.getDateStr(), firstUpLimitTemplateConfig);
        //涨停
        List<EsTemplateDataBo> todayUplimit = getEsTemplateConfigByDate(dto.getDateStr(), upLimitTemplateConfig);
        //未涨停
        List<EsTemplateDataBo> noUplimit =null;
        if (todayUplimit.size() > 0 && todayFirstUplimit.size() > 0) {
            //可能是两板以上的票过滤==今日涨停-今日首次涨停
            twoAboveList = subtractEsTemplateConfig(todayUplimit, todayFirstUplimit);
        }else {
            twoAboveList= new ArrayList<>();
        }
        if (twoAboveList.size() > 0) {
            String beginDateStr = riverRemoteService.getSpecialDay(dto.getDateStr(), -8);
            List<String> dateIntervalList = riverRemoteService.getDateIntervalList(beginDateStr, dto.getDateStr());
            //从昨日开始
            for (int i = dateIntervalList.size() - 2; i >= 0; i--) {
                int lbCount = dateIntervalList.size() - i;
                //首次涨停
                List<EsTemplateDataBo> first = getEsTemplateConfigByDate(dateIntervalList.get(i), firstUpLimitTemplateConfig);
                //涨停
                List<EsTemplateDataBo> all = getEsTemplateConfigByDate(dateIntervalList.get(i), upLimitTemplateConfig);

                //当日可能两板以上的，与昨日首次涨停的相等
                List<EsTemplateDataBo> esTemplateDataBos = equalEsTemplateConfig(twoAboveList, first);
                if (esTemplateDataBos.size() > 0) {
                    //n板的涨停
                    uplimitMap.put(lbCount + "板", esTemplateDataBos);
                    if (lbCount == 2) {
                        List<EsTemplateDataBo> noUplimitArr = subtractEsTemplateConfig(first, todayUplimit);
                        if (noUplimitArr.size() > 0) {
                            noUplimitMap.put(lbCount + "板", noUplimitArr);
                        }
                        //最后一日未涨停，+当前涨停=连板未涨停
                        List<EsTemplateDataBo> todayNoUplimitArr = subtractEsTemplateConfig(all, todayUplimit);
                        noUplimit= equalEsTemplateConfig(todayNoUplimitArr,all);
                    } else {
                        List<EsTemplateDataBo> noUplimitArr = equalEsTemplateConfig(first, noUplimit);
                        if (noUplimitArr.size() > 0) {
                            noUplimitMap.put(lbCount + "板", noUplimitArr);
                        }
                        noUplimit= equalEsTemplateConfig(noUplimit,all);

                    }
                }else {
                    List<EsTemplateDataBo> noUplimitArr = equalEsTemplateConfig(first, noUplimit);
                    if (noUplimitArr.size() > 0) {
                        noUplimitMap.put(lbCount + "板", noUplimitArr);
                    }
                    noUplimit= equalEsTemplateConfig(noUplimit,all);

                }
                List<EsTemplateDataBo> sub = subtractEsTemplateConfig(all, first);
                twoAboveList = equalEsTemplateConfig(sub, twoAboveList);
            }
            //根据涨停，将涨停和未涨停的添加进来
            for (Map.Entry<String, List<EsTemplateDataBo>> tempMap : uplimitMap.entrySet()) {
                StockUpLimitNameBO stockUpLimitNameBO = new StockUpLimitNameBO();
                stockUpLimitNameBO.setUpLimitNum(tempMap.getKey());
                List<StockBase> collect = tempMap.getValue().stream().map(this::convert).collect(Collectors.toList());
                stockUpLimitNameBO.setUplimitArr(collect);
                if (noUplimitMap.containsKey(tempMap.getKey())) {
                    List<StockBase> collect1 = noUplimitMap.get(tempMap.getKey()).stream().map(this::convert).collect(Collectors.toList());
                    stockUpLimitNameBO.setNoUplimitArr(collect1);
                }
                result.add(stockUpLimitNameBO);
            }
            //根据未涨停的
            for (Map.Entry<String, List<EsTemplateDataBo>> tempMap : noUplimitMap.entrySet()) {
                if (!uplimitMap.containsKey(tempMap.getKey())) {
                    StockUpLimitNameBO stockUpLimitNameBO = new StockUpLimitNameBO();
                    stockUpLimitNameBO.setUpLimitNum(tempMap.getKey());
                    List<StockBase> collect1 = tempMap.getValue().stream().map(this::convert).collect(Collectors.toList());
                    stockUpLimitNameBO.setNoUplimitArr(collect1);
                    result.add(stockUpLimitNameBO);
                }
            }
            return result.stream().sorted(Comparator.comparing(StockUpLimitNameBO::getUpLimitNum).reversed()).collect(Collectors.toList());
        } else {
            return getTwoAboveUpLimitInfo(dto);
        }
    }

    private StockBase convert(EsTemplateDataBo dto) {
        StockBase stockBase = new StockBase();
        stockBase.setCode(dto.getStockCode());
        stockBase.setName(dto.getStockName());
        stockBase.setIndustry(dto.getIndustry());
        stockBase.setTheme(dto.getTheme());
        return stockBase;

    }

    private List<EsTemplateDataBo> getTwoAboveList(String dateStr, EsTemplateConfig firstUpLimitTemplateConfig, EsTemplateConfig upLimitTemplateConfig) throws IOException {
        List<EsTemplateDataBo> esTemplateConfigByDate = getEsTemplateConfigByDate(dateStr, upLimitTemplateConfig);
        List<EsTemplateDataBo> esTemplateConfigByDate1 = getEsTemplateConfigByDate(dateStr, firstUpLimitTemplateConfig);
        if (esTemplateConfigByDate.size() > 0 && esTemplateConfigByDate1.size() > 0) {
            //可能是两板以上的票过滤==今日涨停-今日首次涨停
            List<EsTemplateDataBo> esTemplateDataBos = subtractEsTemplateConfig(esTemplateConfigByDate, esTemplateConfigByDate1);
            return esTemplateDataBos;
        }
        return new ArrayList<>();
    }

    private List<EsTemplateDataBo> equalEsTemplateConfig(List<EsTemplateDataBo> first, List<EsTemplateDataBo> second) {
        Map<String, EsTemplateDataBo> result = new HashMap<>();
        Map<String, EsTemplateDataBo> map1 = first.stream().collect(Collectors.toMap(EsTemplateDataBo::getStockCode, Function.identity()));
        Map<String, EsTemplateDataBo> map2 = second.stream().collect(Collectors.toMap(EsTemplateDataBo::getStockCode, Function.identity()));
        for (Map.Entry<String, EsTemplateDataBo> map : map2.entrySet()) {
            if (map1.containsKey(map.getKey())) {
                result.put(map.getKey(), map.getValue());
            }
        }
        return result.entrySet().stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
    }

    private List<EsTemplateDataBo> subtractEsTemplateConfig(List<EsTemplateDataBo> first, List<EsTemplateDataBo> second) {
        Map<String, EsTemplateDataBo> map1 = first.stream().collect(Collectors.toMap(EsTemplateDataBo::getStockCode, Function.identity()));
        Map<String, EsTemplateDataBo> map2 = second.stream().collect(Collectors.toMap(EsTemplateDataBo::getStockCode, Function.identity()));
        for (Map.Entry<String, EsTemplateDataBo> map : map2.entrySet()) {
            if (map1.containsKey(map.getKey())) {
                map1.remove(map.getKey());
            }
        }
        return map1.entrySet().stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
    }

    private List<EsTemplateDataBo> getEsTemplateConfigByDate(String dateStr, EsTemplateConfig esTemplateConfig) throws IOException {
        EsTemplateConfigDTO esTemplateConfigDTO = new EsTemplateConfigDTO();
        BeanUtils.copyProperties(esTemplateConfig, esTemplateConfigDTO);
        esTemplateConfigDTO.setDateStr(dateStr);
        esTemplateConfigDTO.setRiverStockTemplateId(esTemplateConfig.getTemplateId());
        return esTemplateDataService.getEsTemplateDataList(esTemplateConfigDTO);

    }

    private EsTemplateConfig getEsTemplateConfigByTemplateId(String templateId) {
        String esTemplateConfig = RedisKeyUtils.getEsTemplateConfig(templateId, EsTemplateConfigEnum.TYPE_DAY.getSign());

        String jsonStr = (String) redisTemplate.opsForValue().get(esTemplateConfig);
        EsTemplateConfig esTemplateConfig1 = JsonUtil.readToValue(jsonStr, EsTemplateConfig.class);


        return esTemplateConfig1;
    }


    public Object getUpLimitSign(StockEmotionDayDTO dto) {
        dto.setDateStr(riverRemoteService.getSpecialDay(dto.getDateStr(), -1));
        List<StockUpLimitNameBO> result = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(7);
        for (int i = 2; i < 9; i++) {
            final int num = i;
            Constant.countDownThreadPool.execute(() -> {
                try {
                    //涨停脚本语句
                    String upLimitNumScript = getUpLimitNumScript(num);
                    List<StockBase> stockBases = getlimitInfo(upLimitNumScript, dto.getDateStr());
                    String noUpLimitNumScript = getNoUpLimitNumScript(num);
                    List<StockBase> stockBases1 = getlimitInfo(noUpLimitNumScript, dto.getDateStr());
                    StockUpLimitNameBO stockUpLimitNameBO = new StockUpLimitNameBO();
                    stockUpLimitNameBO.setUpLimitNum((num + 1) + "板");
                    stockUpLimitNameBO.setUplimitArr(stockBases);
                    stockUpLimitNameBO.setNoUplimitArr(stockBases1);
                    if (stockUpLimitNameBO.getNoUplimitArr().size() > 0) {
                        result.add(stockUpLimitNameBO);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    countDownLatch.countDown();
                }

            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return result.stream().sorted(Comparator.comparing(StockUpLimitNameBO::getUpLimitNum).reversed()).collect(Collectors.toList());
    }


    /**
     * 非创业板，非st板块，{{lastDay3}}未涨停，{{lastDay2}}涨停，{{lastDay1}}涨停，{{today}}涨停，
     * 获取连续涨停的脚本
     *
     * @param num
     * @return
     */
    private String getUpLimitNumScript(int num) {
        StringBuffer sb = new StringBuffer();
        sb.append(" 非st板块，");
        sb.append("{{lastDay" + (num) + "}}未涨停，");
        for (int i = num - 1; i > 0; i--) {
            sb.append("{{lastDay" + i + "}}涨停，");
        }
        sb.append("{{today}}涨停，");

        return sb.toString();
    }

    private String getNoUpLimitNumScript(int num) {
        StringBuffer sb = new StringBuffer();
        sb.append(" 非st板块，");
        sb.append("{{lastDay" + (num) + "}}未涨停，");
        for (int i = num - 1; i > 0; i--) {
            sb.append("{{lastDay" + i + "}}涨停，");
        }
        sb.append("{{today}}未涨停，");

        return sb.toString();
    }


    /**
     * 涨停题材
     *
     * @param dto
     * @return
     * @throws NoSuchMethodException
     * @throws ScriptException
     * @throws FileNotFoundException
     */
    public List<StockUpLimitInfoBO> getUpLimitTheme(StockStrategyQueryDTO dto) throws NoSuchMethodException, ScriptException, IOException, IllegalAccessException {
        StrategyBO strategy = stockStrategyService.comprehensiveStrategy(dto);
        if (strategy.getTotalNum() > 0) {
            JSONArray data = strategy.getData();
            return rebuildThemeInfo(data);
        }
        return null;
    }

    /**
     * 题材相关
     *
     * @param data
     * @return
     */
    public List<StockUpLimitInfoBO> rebuildThemeInfo(JSONArray data) {
        //key 为题材名称 value为股票名称
        Map<String, List<String>> themeMap = new HashMap<>();

        //key 为股票名称 value为股票名称
        Map<String, LimitBaseInfoBO> nameMap = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject jo = data.getJSONObject(i);
            //取里面的数组信息
            Set<String> keys = jo.keySet();
            String stockName = stockParseAndConvertService.getStockName(jo);
            LimitStrongWeakBO upLimitStrongWeak = upLimitStrongWeakService.getLimitStrongWeak(jo, "涨停明细数据");
            LimitBaseInfoBO upLimitBaseInfoBO = new LimitBaseInfoBO();
            BeanUtils.copyProperties(upLimitStrongWeak, upLimitBaseInfoBO);
            upLimitBaseInfoBO.setName(stockName);
            upLimitBaseInfoBO.setCode(jo.getString("code"));

            nameMap.put(stockName, upLimitBaseInfoBO);

            for (String key : keys) {
                if (key.contains("涨停原因类别")) {
                    String themeStr = (String) jo.get(key);
                    if (themeStr.contains("+")) {
                        for (String str : themeStr.split("\\+")) {
                            if (themeMap.containsKey(str)) {
                                themeMap.get(str).add(stockName);
                            } else {
                                List<String> name = new ArrayList<>();
                                name.add(stockName);
                                themeMap.put(str, name);
                            }
                        }
                    } else {
                        if (themeMap.containsKey(themeStr)) {
                            themeMap.get(themeStr).add(stockName);
                        } else {
                            List<String> name = new ArrayList<>();
                            name.add(stockName);
                            themeMap.put(themeStr, name);
                        }
                    }
                }
            }
        }
        List<StockUpLimitInfoBO> result = themeMap.entrySet().stream().map(o1 -> convert(o1, nameMap)).collect(Collectors.toList());
        result = result.stream().sorted(Comparator.comparing(StockUpLimitInfoBO::getNum)).collect(Collectors.toList());
        return result;
    }

    private StockUpLimitInfoBO convert(Map.Entry<String, List<String>> map, Map<String, LimitBaseInfoBO> nameMap) {
        StockUpLimitInfoBO stockUpLimitInfoBO = new StockUpLimitInfoBO();
        stockUpLimitInfoBO.setThemeName(map.getKey());
        List<LimitBaseInfoBO> nameList = new ArrayList<>();
        for (String name : map.getValue()) {
            nameList.add(nameMap.get(name));
        }
        stockUpLimitInfoBO.setNameList(nameList);
        stockUpLimitInfoBO.setNum(map.getValue().size());
        return stockUpLimitInfoBO;
    }


    /**
     * 根据策略code数据
     *
     * @param dateStr
     * @param riverStockTemplateId
     * @return
     */
    private List<String> getCodeListByStrategy(String dateStr, String riverStockTemplateId) {
        List<String> codeList = new ArrayList<>();
        StockStrategyQueryDTO stockStrategyQueryDTO = new StockStrategyQueryDTO();
        stockStrategyQueryDTO.setDateStr(dateStr);
        stockStrategyQueryDTO.setRiverStockTemplateId(riverStockTemplateId);
        StrategyBO strategy = null;
        try {
            strategy = stockStrategyService.comprehensiveStrategy(stockStrategyQueryDTO);
            JSONArray data = strategy.getData();
            for (Object jo : data) {
                codeList.add(((String) ((JSONObject) jo).get("code")));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return codeList;
    }


}
