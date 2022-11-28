package com.coatardbul.river.service;

import com.coatardbul.river.common.util.DateTimeUtil;
import com.coatardbul.river.feign.BaseServerFeign;
import com.coatardbul.river.mapper.StockTimeIntervalMapper;
import com.coatardbul.river.model.bo.IntervalStaticBo;
import com.coatardbul.river.model.entity.StockTimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/10
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockTimeIntervalService {

    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    StockTimeIntervalMapper stockTimeIntervalMapper;
    public List<String> getList(StockTimeInterval dto) {
        List<StockTimeInterval> stockTimeIntervals = stockTimeIntervalMapper.selectAllByIntervalType(dto.getIntervalType());
        if(stockTimeIntervals!=null &&stockTimeIntervals.size()>0){
            return stockTimeIntervals.stream().map(StockTimeInterval::getTimeStr).collect(Collectors.toList());
        }
        return null;
    }

    public void refresh(StockTimeInterval dto) {
        stockTimeIntervalMapper.deleteByIntervalType(dto.getIntervalType());
        List<String> result = DateTimeUtil.getRangeMinute("09:30", "11:30", dto.getIntervalType());
        List<String> rangeMinute = DateTimeUtil.getRangeMinute("13:00", "15:00", dto.getIntervalType());
        result.addAll(rangeMinute);
        result.forEach(v->{
            StockTimeInterval addInfo=new StockTimeInterval();
            addInfo.setId(baseServerFeign.getSnowflakeId());
            addInfo.setTimeStr(v);
            addInfo.setIntervalType(dto.getIntervalType());
            stockTimeIntervalMapper.insert(addInfo);
        });
    }

    public List<IntervalStaticBo> getIntervalList() {
       return stockTimeIntervalMapper.selectIntervalStatic();
    }
}
