package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockWatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockWatchMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockWatch record);

    List<StockWatch> selectAllByCode(@Param("code")String code);



    int insertSelective(StockWatch record);

    StockWatch selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockWatch record);

    int updateByPrimaryKey(StockWatch record);

    List<StockWatch> selectByAll();


}