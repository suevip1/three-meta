package com.coatardbul.stock.service.statistic.dayStatic.dayBaseChart;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.BigRoot;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.common.util.StockStaticModuleUtil;
import com.coatardbul.stock.mapper.StockDayEmotionMapper;
import com.coatardbul.stock.mapper.StockMinuterEmotionMapper;
import com.coatardbul.stock.mapper.StockStaticTemplateMapper;
import com.coatardbul.stock.model.bo.DayAxiosBaseBO;
import com.coatardbul.stock.model.bo.DayCallAuctionIncreaseBo;
import com.coatardbul.stock.model.bo.StockStaticBaseBO;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.model.entity.StockDayEmotion;
import com.coatardbul.stock.model.entity.StockStaticTemplate;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Note:策略处理
 * <p>
 * Date: 2022/1/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockDayCalcAuctionIncreaseService extends BaseChartDayAbstractService {

    @Autowired
    RiverRemoteService riverRemoteService;


    @Autowired
    StockStrategyService stockStrategyService;
    @Autowired
    SnowFlakeService snowFlakeService;
    @Autowired
    RiverServerFeign riverServerFeign;
    @Autowired
    StockStaticTemplateMapper stockStaticTemplateMapper;
    @Autowired
    StockMinuterEmotionMapper stockMinuterEmotionMapper;
    @Autowired
    StockDayEmotionMapper stockDayEmotionMapper;
    @Autowired
    StockVerifyService stockVerifyService;


    /**
     * 刷新当日数据，全量刷新
     *
     * @param dto
     * @throws IllegalAccessException
     */
    @Override
    public void refreshDayProcess(StockEmotionDayDTO dto, StockStaticTemplate stockStaticTemplate) throws IllegalAccessException, ParseException {
        String orderByStr = stockStaticTemplate.getOrderBy().replace("dateStr", dto.getDateStr().replaceAll("-", ""));

        //获取模型对象中的模板id集合,便于根据模板id查询对应的数据结果
        List<String> templateIdList = stockStrategyService.getTemplateIdList(stockStaticTemplate);

        if (templateIdList != null && templateIdList.size() > 0) {
            StockDayEmotion addStockDayEmotion = new StockDayEmotion();
            addStockDayEmotion.setId(snowFlakeService.getSnowId());
            addStockDayEmotion.setDate(dto.getDateStr());
            addStockDayEmotion.setObjectSign(dto.getObjectEnumSign());

            //获取数组里面的对象
            Map<String, StrategyBO> map = new HashMap<>();
            for (String templateId : templateIdList) {
                StockStrategyQueryDTO stockStrategyQueryDTO = new StockStrategyQueryDTO();
                stockStrategyQueryDTO.setRiverStockTemplateId(templateId);
                stockStrategyQueryDTO.setDateStr(dto.getDateStr());
                stockStrategyQueryDTO.setOrderStr(orderByStr);
                stockStrategyQueryDTO.setOrderBy("desc");
                StrategyBO strategy = null;
                try {
                    strategy = stockStrategyService.wenCaiStrategy(stockStrategyQueryDTO);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                map.put(templateId, strategy);
            }
            List<DayAxiosBaseBO> rebuild = rebuild( dto,stockStaticTemplate, map);
            addStockDayEmotion.setObjectStaticArray(JsonUtil.toJson(rebuild));
            stockDayEmotionMapper.deleteByDateAndObjectSign(dto.getDateStr(), dto.getObjectEnumSign());
            stockDayEmotionMapper.insertSelective(addStockDayEmotion);
        }

    }

    private List<DayAxiosBaseBO> rebuild(StockEmotionDayDTO dto,StockStaticTemplate stockStaticTemplate, Map<String, StrategyBO> map)  {
        //排序字段
        String orderByStr = stockStaticTemplate.getOrderBy().replace("dateStr", dto.getDateStr().replaceAll("-", ""));
        //解析
        Class classBySign = StockStaticModuleUtil.getClassBySign(stockStaticTemplate.getObjectSign());
        DayCallAuctionIncreaseBo o = (DayCallAuctionIncreaseBo) JsonUtil.readToValue(stockStaticTemplate.getObjectStr(), classBySign);
        List<DayAxiosBaseBO> result = new ArrayList<>();
//        DayAxiosBaseBO adjs = getAdjs(map.get(o.getRiseId()), map.get(o.getFailId()));
//        result.add(adjs);
        List<DayAxiosBaseBO> firstUpLimit = getStandardDeviationAndMedian(map.get(o.getCallAuctionIncreaseId()), orderByStr, "涨幅大于5");
        result.addAll(firstUpLimit);
        return result;
    }




    public List<DayAxiosBaseBO> getStandardDeviationAndMedian(StrategyBO limitUpStrategy, String orderByStr, String appendName) {
        List<DayAxiosBaseBO> result = new ArrayList<>();
        //基本统计信息
        StockStaticBaseBO staticBase = getStaticBase(limitUpStrategy, orderByStr);
        DayAxiosBaseBO standardDeviation = new DayAxiosBaseBO();
        standardDeviation.setName(appendName+"标准差");
        standardDeviation.setValue(staticBase.getStandardDeviation());
        result.add(standardDeviation);
        DayAxiosBaseBO median = new DayAxiosBaseBO();
        median.setName(appendName+"中位数");
        median.setValue(staticBase.getMedian());
        result.add(median);
        return result;
    }


    /**
     * 获取统计的基本信息
     *
     * @param limitUpStrategy 查询的策略对象
     * @param orderStr        排序字段
     * @return
     */
    private StockStaticBaseBO getStaticBase(StrategyBO limitUpStrategy, String orderStr) {
        StockStaticBaseBO result = new StockStaticBaseBO();
        int medianindex = limitUpStrategy.getTotalNum() / 2;
        int medianSub = limitUpStrategy.getTotalNum() % 2;
        if (medianindex != 0) {
            JSONObject medianStrategy = null;
            if (medianSub == 0) {
                medianStrategy = limitUpStrategy.getData().getJSONObject(medianindex - 1);
            } else {
                medianStrategy = limitUpStrategy.getData().getJSONObject(medianindex);
            }
            //标的数
            result.setRaiseLimitNum(limitUpStrategy.getData().size());
            //中位数
            BigDecimal medianNum = medianStrategy.getBigDecimal(orderStr);
            BigDecimal variance = BigDecimal.ZERO;
            for (int i = 0; i < limitUpStrategy.getData().size(); i++) {
                if (limitUpStrategy.getData().getJSONObject(i).getBigDecimal(orderStr) == null) {
                    continue;
                }
                BigDecimal b = limitUpStrategy.getData().getJSONObject(i).getBigDecimal(orderStr).subtract(medianNum);
                variance = variance.add(b.multiply(b));
            }
            //方差
            variance = variance.divide(new BigDecimal(limitUpStrategy.getTotalNum() - 1), 4, BigDecimal.ROUND_HALF_UP);
            result.setMedian(medianNum);
            //标准差
            result.setStandardDeviation(BigRoot.bigRoot(variance, 2, 4, BigDecimal.ROUND_HALF_UP));
        }
        return result;
    }


}
