package com.coatardbul.stock.mapper;

import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import java.util.List;

public interface StockTradeBuyTaskMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTradeBuyTask record);

    int insertSelective(StockTradeBuyTask record);

    StockTradeBuyTask selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTradeBuyTask record);

    int updateByPrimaryKey(StockTradeBuyTask record);

    List<StockTradeBuyTask> selectByAll(StockTradeBuyTask stockTradeBuyTask);
}