package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockFilterMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockFilter record);

    int insertSelective(StockFilter record);

    StockFilter selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockFilter record);

    int updateByPrimaryKey(StockFilter record);

    int deleteByDateAndTemplateSignAndStockCode(@Param("date")String date,@Param("templateSign")String templateSign,@Param("stockCode")String stockCode);

    int deleteByDateAndTemplateSign(@Param("date")String date,@Param("templateSign")String templateSign);



    StockFilter selectAllByDateAndTemplateSignAndStockCode(@Param("date")String date,@Param("templateSign")String templateSign,@Param("stockCode")String stockCode);


    List<StockFilter> selectAllByDateAndTemplateSign(@Param("date")String date,@Param("templateSign")String templateSign);




}