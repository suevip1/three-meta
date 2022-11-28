package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockTradeDetail;

public interface StockTradeDetailMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTradeDetail record);

    int insertSelective(StockTradeDetail record);

    StockTradeDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTradeDetail record);

    int updateByPrimaryKey(StockTradeDetail record);
}