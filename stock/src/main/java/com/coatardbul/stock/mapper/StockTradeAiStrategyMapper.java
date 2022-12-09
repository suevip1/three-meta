package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockTradeAiStrategy;import java.util.List;

public interface StockTradeAiStrategyMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTradeAiStrategy record);

    int insertSelective(StockTradeAiStrategy record);

    StockTradeAiStrategy selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTradeAiStrategy record);

    int updateByPrimaryKey(StockTradeAiStrategy record);

    List<StockTradeAiStrategy> selectAll();
}