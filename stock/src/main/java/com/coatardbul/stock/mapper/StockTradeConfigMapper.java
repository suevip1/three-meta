package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockTradeConfig;
import org.apache.ibatis.annotations.Param;

public interface StockTradeConfigMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTradeConfig record);

    int insertSelective(StockTradeConfig record);

    StockTradeConfig selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTradeConfig record);

    int updateByPrimaryKey(StockTradeConfig record);

    StockTradeConfig selectAllBySign(@Param("sign")String sign);


    int updateSimulateTypeBySign(@Param("updatedSimulateType")String updatedSimulateType,@Param("sign")String sign);


}