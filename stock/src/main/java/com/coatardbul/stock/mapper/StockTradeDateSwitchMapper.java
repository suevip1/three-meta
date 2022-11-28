package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockTradeDateSwitch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockTradeDateSwitchMapper {
    int deleteByPrimaryKey(String date);

    int insert(StockTradeDateSwitch record);

    int insertSelective(StockTradeDateSwitch record);

    StockTradeDateSwitch selectByPrimaryKey(String date);

    int updateByPrimaryKeySelective(StockTradeDateSwitch record);

    int updateByPrimaryKey(StockTradeDateSwitch record);

    List<StockTradeDateSwitch> selectByAll(StockTradeDateSwitch stockTradeDateSwitch);

    int updateDate(@Param("updatedDate")String updatedDate);



}