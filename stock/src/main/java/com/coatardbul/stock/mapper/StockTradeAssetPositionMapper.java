package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockTradeAssetPosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockTradeAssetPositionMapper {
    int deleteByPrimaryKey(String id);

    int deleteByCode(@Param("code")String code);


    int insert(StockTradeAssetPosition record);

    int insertSelective(StockTradeAssetPosition record);

    StockTradeAssetPosition selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTradeAssetPosition record);

    int updateByPrimaryKey(StockTradeAssetPosition record);

    List<StockTradeAssetPosition> selectByAll(StockTradeAssetPosition stockTradeAssetPosition);

    StockTradeAssetPosition selectAllByCode(@Param("code") String code);
}