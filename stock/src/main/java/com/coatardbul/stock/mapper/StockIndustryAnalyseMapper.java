package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockIndustryAnalyse;import org.apache.ibatis.annotations.Param;import java.util.List;

public interface StockIndustryAnalyseMapper {
    int deleteByPrimaryKey(String date);

    int insert(StockIndustryAnalyse record);

    int insertSelective(StockIndustryAnalyse record);

    StockIndustryAnalyse selectByPrimaryKey(String date);

    int updateByPrimaryKeySelective(StockIndustryAnalyse record);

    int updateByPrimaryKey(StockIndustryAnalyse record);

    List<StockIndustryAnalyse> selectByAll();

    List<StockIndustryAnalyse> selectAllByDateBetweenEqual(@Param("minDate") String minDate, @Param("maxDate") String maxDate);
}