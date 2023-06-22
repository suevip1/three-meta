package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockTradeSellTask;import java.util.List;

public interface StockTradeSellTaskMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTradeSellTask record);

    int insertSelective(StockTradeSellTask record);

    StockTradeSellTask selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTradeSellTask record);

    int updateByPrimaryKey(StockTradeSellTask record);

    List<StockTradeSellTask> selectByAll(StockTradeSellTask stockTradeSellTask);
}