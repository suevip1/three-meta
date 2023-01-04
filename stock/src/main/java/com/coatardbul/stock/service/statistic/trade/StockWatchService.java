package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.stock.mapper.StockWatchMapper;
import com.coatardbul.stock.model.entity.StockWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/27
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockWatchService {

    @Autowired
    StockWatchMapper stockWatchMapper;
    @Autowired
    SnowFlakeService snowFlakeService;

    public void add(StockWatch dto) {
        List<StockWatch> stockWatches = stockWatchMapper.selectAllByCode(dto.getCode());
        if (stockWatches.size() > 0) {
            return;
        }
        dto.setId(snowFlakeService.getSnowId());
        stockWatchMapper.insertSelective(dto);
    }

    public void modify(StockWatch dto) {
        stockWatchMapper.updateByPrimaryKeySelective(dto);
    }

    public void delete(StockWatch dto) {
        stockWatchMapper.deleteByPrimaryKey(dto.getId());

    }

    public List<StockWatch> getAll() {
        return stockWatchMapper.selectByAll();
    }
}
