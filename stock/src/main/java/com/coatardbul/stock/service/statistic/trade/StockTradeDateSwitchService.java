package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.stock.model.entity.StockTradeDateSwitch;
import com.coatardbul.stock.mapper.StockTradeDateSwitchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Note:  日切信息
 * <p>
 * Date: 2022/6/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockTradeDateSwitchService {


    @Autowired
    StockTradeDateSwitchMapper stockTradeDateSwitchMapper;


    /**
     * 获取当前日切时间
     * @return
     */
    public String getCurrentDateSwitch() {
        List<StockTradeDateSwitch> stockTradeDateSwitches = stockTradeDateSwitchMapper.selectByAll(null);
        if (stockTradeDateSwitches != null && stockTradeDateSwitches.size() > 0) {
            return stockTradeDateSwitches.get(0).getDate();
        } else {
            return "";
        }
    }

    public void switchDate(StockTradeDateSwitch dto){
        stockTradeDateSwitchMapper.updateDate(dto.getDate());
    }

    public StockTradeDateSwitch getDateSwitchInfo() {
        List<StockTradeDateSwitch> stockTradeDateSwitches = stockTradeDateSwitchMapper.selectByAll(null);
        if (stockTradeDateSwitches != null && stockTradeDateSwitches.size() > 0) {
            return stockTradeDateSwitches.get(0);
        } else {
            return null;
        }
    }



}
