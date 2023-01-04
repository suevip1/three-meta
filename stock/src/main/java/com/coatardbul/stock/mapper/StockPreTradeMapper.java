package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockPreTrade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockPreTradeMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockPreTrade record);

    int insertSelective(StockPreTrade record);

    StockPreTrade selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockPreTrade record);

    int updateByPrimaryKey(StockPreTrade record);

    List<StockPreTrade> selectAllByCodeAndTradeDate(@Param("code")String code,@Param("tradeDate")String tradeDate);


}