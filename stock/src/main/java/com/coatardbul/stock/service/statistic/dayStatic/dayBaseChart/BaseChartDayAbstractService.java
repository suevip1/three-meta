package com.coatardbul.stock.service.statistic.dayStatic.dayBaseChart;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.common.constants.Constant;
import com.coatardbul.stock.mapper.StockDayEmotionMapper;
import com.coatardbul.stock.mapper.StockMinuterEmotionMapper;
import com.coatardbul.stock.mapper.StockStaticTemplateMapper;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.model.dto.StockEmotionDayRangeDTO;
import com.coatardbul.stock.model.dto.StockEmotionQueryDTO;
import com.coatardbul.stock.model.dto.StockEmotionRangeDayDTO;
import com.coatardbul.stock.model.entity.StockDayEmotion;
import com.coatardbul.stock.model.entity.StockStaticTemplate;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.dayStatic.DayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/13
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public abstract class BaseChartDayAbstractService implements DayService {
    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    StockStaticTemplateMapper stockStaticTemplateMapper;
    @Autowired
    SnowFlakeService snowFlakeService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockStrategyService stockStrategyService;
    @Autowired
    RiverServerFeign riverServerFeign;
    @Autowired
    StockMinuterEmotionMapper stockMinuterEmotionMapper;
    @Autowired
    StockDayEmotionMapper stockDayEmotionMapper;

    /**
     * 刷新当日数据，全量刷新
     *
     * @param dto
     * @throws IllegalAccessException
     */
    @Override
    public void refreshDay(StockEmotionDayDTO dto) throws IllegalAccessException, ParseException {
        if (stockVerifyService.isIllegalDate(dto.getDateStr())) {
            return;
        }
        List<StockStaticTemplate> stockStaticTemplates = stockStaticTemplateMapper.selectAllByObjectSign(dto.getObjectEnumSign());
        if (stockStaticTemplates == null || stockStaticTemplates.size() == 0) {
            throw new BusinessException("对象标识异常");
        }
        //模型策略数据
        StockStaticTemplate stockStaticTemplate = stockStaticTemplates.get(0);

        refreshDayProcess(dto, stockStaticTemplate);
    }

    /**
     * 子类实现
     *
     * @param dto
     * @param stockStaticTemplate
     * @throws IllegalAccessException
     * @throws ParseException
     */
    public void refreshDayProcess(StockEmotionDayDTO dto, StockStaticTemplate stockStaticTemplate) throws IllegalAccessException, ParseException {

    }


    /**
     * 表中有数据，不刷新，无数据，增量刷新
     *
     * @param dto
     */
    @Override
    public void refreshDayRange(StockEmotionDayRangeDTO dto) {
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDate(), dto.getEndDate());
        for (String dateStr : dateIntervalList) {
            Constant.emotionByDateRangeThreadPool.execute(() -> {
                //表中有数据，直接返回，没有再查询
                List<StockDayEmotion> stockDayEmotions = stockDayEmotionMapper.selectAllByDateAndObjectSign(dateStr, dto.getObjectEnumSign());
                if (stockDayEmotions != null && stockDayEmotions.size() > 0) {
                    return;
                }
                StockEmotionDayDTO stockEmotionDayDTO = new StockEmotionDayDTO();
                stockEmotionDayDTO.setDateStr(dateStr);
                stockEmotionDayDTO.setObjectEnumSign(dto.getObjectEnumSign());
                stockEmotionDayDTO.setTimeInterval(dto.getTimeInterval());
                try {
                    refreshDay(stockEmotionDayDTO);
                } catch (IllegalAccessException | ParseException e) {
                    log.error(e.getMessage(), e);
                }
            });


        }
    }

    @Override
    public List<StockDayEmotion> getDayStatic(StockEmotionQueryDTO dto) {
        List<StockDayEmotion> stockDayEmotions = stockDayEmotionMapper.selectAllByDateAndObjectSign(dto.getDateStr(), dto.getObjectEnumSign());
        if (stockDayEmotions != null && stockDayEmotions.size() > 0) {
            return stockDayEmotions.stream().sorted(Comparator.comparing(StockDayEmotion::getDate)).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Override
    public List<StockDayEmotion> getRangeStatic(StockEmotionRangeDayDTO dto) {
        List<StockDayEmotion> stockDayEmotions = stockDayEmotionMapper.selectAllByDateBetweenEqualAndObjectSign(dto.getDateBeginStr(), dto.getDateEndStr(), dto.getObjectEnumSign());
        return stockDayEmotions;
    }

    @Override
    public void forceRefreshDayRange(StockEmotionDayRangeDTO dto) {
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDate(), dto.getEndDate());
        for (String dateStr : dateIntervalList) {
            Constant.emotionByDateRangeThreadPool.execute(() -> {
                StockEmotionDayDTO stockEmotionDayDTO = new StockEmotionDayDTO();
                stockEmotionDayDTO.setDateStr(dateStr);
                stockEmotionDayDTO.setObjectEnumSign(dto.getObjectEnumSign());
                stockEmotionDayDTO.setTimeInterval(dto.getTimeInterval());
                try {
                    refreshDay(stockEmotionDayDTO);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void deleteDay(StockEmotionDayDTO dto) {
        stockDayEmotionMapper.deleteByDateAndObjectSign(dto.getDateStr(), dto.getObjectEnumSign());

    }
}
