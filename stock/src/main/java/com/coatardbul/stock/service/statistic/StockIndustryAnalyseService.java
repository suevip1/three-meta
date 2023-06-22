package com.coatardbul.stock.service.statistic;

import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.stock.mapper.StockIndustryAnalyseMapper;
import com.coatardbul.stock.model.dto.StockEmotionDayRangeDTO;
import com.coatardbul.stock.model.dto.StockIndustryAnalyseDTO;
import com.coatardbul.stock.model.entity.StockIndustryAnalyse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/14
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockIndustryAnalyseService {

    @Autowired
    StockIndustryAnalyseMapper stockIndustryAnalyseMapper;


    public void add(StockIndustryAnalyseDTO dto) {

        StockIndustryAnalyse convert = convert(dto);
        StockIndustryAnalyse stockIndustryAnalyse = stockIndustryAnalyseMapper.selectByPrimaryKey(dto.getDate());
        if (stockIndustryAnalyse == null) {
            stockIndustryAnalyseMapper.insertSelective(convert);
        }
    }

    public List<StockIndustryAnalyseDTO> getAll() {
        List<StockIndustryAnalyse> stockIndustryAnalyses = stockIndustryAnalyseMapper.selectByAll();
        List<StockIndustryAnalyseDTO> stockIndustryAnalyseDTOS = new ArrayList<>();
        for (StockIndustryAnalyse stockIndustryAnalyse : stockIndustryAnalyses) {
            StockIndustryAnalyseDTO stockIndustryAnalyseDTO = convert(stockIndustryAnalyse);
            stockIndustryAnalyseDTOS.add(stockIndustryAnalyseDTO);
        }
        return stockIndustryAnalyseDTOS;
    }


    public StockIndustryAnalyse convert(StockIndustryAnalyseDTO dto) {
        StockIndustryAnalyse stockIndustryAnalyse = new StockIndustryAnalyse();
        stockIndustryAnalyse.setDate(dto.getDate());
        stockIndustryAnalyse.setCallAuction(getStringJson(dto.getCallAuction()));
        stockIndustryAnalyse.setGreateThanLine(getStringJson(dto.getGreateThanLine()));
        stockIndustryAnalyse.setBigIncrease(getStringJson(dto.getBigIncrease()));
        stockIndustryAnalyse.setNextCallAuctionBuy(getStringJson(dto.getNextCallAuctionBuy()));
        stockIndustryAnalyse.setSmallIncrease(getStringJson(dto.getSmallIncrease()));
        stockIndustryAnalyse.setUplimitInfo(getStringJson(dto.getUplimitInfo()));
        stockIndustryAnalyse.setRemark(dto.getRemark());
        return stockIndustryAnalyse;
    }

    public StockIndustryAnalyseDTO convert(StockIndustryAnalyse stockIndustryAnalyse) {
        StockIndustryAnalyseDTO dto = new StockIndustryAnalyseDTO();
        dto.setDate(stockIndustryAnalyse.getDate());
        dto.setCallAuction(getArrByStr(stockIndustryAnalyse.getCallAuction()));
        dto.setGreateThanLine(getArrByStr(stockIndustryAnalyse.getGreateThanLine()));
        dto.setBigIncrease(getArrByStr(stockIndustryAnalyse.getBigIncrease()));
        dto.setNextCallAuctionBuy(getArrByStr(stockIndustryAnalyse.getNextCallAuctionBuy()));
        dto.setSmallIncrease(getArrByStr(stockIndustryAnalyse.getSmallIncrease()));
        dto.setUplimitInfo(getArrByStr(stockIndustryAnalyse.getUplimitInfo()));
        dto.setRemark(stockIndustryAnalyse.getRemark());
        return dto;
    }

    private List<String> getArrByStr(String str) {
        if (StringUtils.isNotBlank(str)) {
            List<String> arr = JsonUtil.readToValue(str, new TypeReference<List<String>>() {
            });
            return arr;
        } else {
            return new ArrayList<>();
        }
    }

    private String getStringJson(List<String> strArr) {
        if (strArr == null) {
            strArr = new ArrayList<>();
        }
        return JsonUtil.toJson(strArr);
    }

    public List<StockIndustryAnalyseDTO> findDayRange(StockEmotionDayRangeDTO dto) {
        List<StockIndustryAnalyse> stockIndustryAnalyses = stockIndustryAnalyseMapper.selectAllByDateBetweenEqual(dto.getBeginDate(), dto.getEndDate());
        List<StockIndustryAnalyseDTO> stockIndustryAnalyseDTOS = new ArrayList<>();
        for (StockIndustryAnalyse stockIndustryAnalyse : stockIndustryAnalyses) {
            StockIndustryAnalyseDTO stockIndustryAnalyseDTO = convert(stockIndustryAnalyse);
            stockIndustryAnalyseDTOS.add(stockIndustryAnalyseDTO);
        }
        return stockIndustryAnalyseDTOS;
    }

    public void modify(StockIndustryAnalyseDTO dto) {
        StockIndustryAnalyse convert = convert(dto);
        stockIndustryAnalyseMapper.updateByPrimaryKey(convert);

    }

    public void delete(StockIndustryAnalyseDTO dto) {
        stockIndustryAnalyseMapper.deleteByPrimaryKey(dto.getDate());
    }

    public StockIndustryAnalyseDTO get(StockIndustryAnalyseDTO dto) {
        StockIndustryAnalyse stockIndustryAnalyse = stockIndustryAnalyseMapper.selectByPrimaryKey(dto.getDate());
       return convert(stockIndustryAnalyse);
    }
}
