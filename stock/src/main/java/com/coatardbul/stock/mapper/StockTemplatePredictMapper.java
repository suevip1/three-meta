package com.coatardbul.stock.mapper;

import com.coatardbul.baseService.entity.bo.StockTemplatePredict;import org.apache.ibatis.annotations.Param;import java.util.List;

public interface StockTemplatePredictMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTemplatePredict record);

    int insertSelective(StockTemplatePredict record);

    StockTemplatePredict selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTemplatePredict record);

    int updateByPrimaryKey(StockTemplatePredict record);

    List<StockTemplatePredict> selectAllByDateAndTemplatedIdAndCode(@Param("date") String date, @Param("templatedId") String templatedId, @Param("code") String code);

    List<StockTemplatePredict> selectAllByCodeAndTemplatedSignAndDate(@Param("code") String code, @Param("templatedSign") String templatedSign, @Param("date") String date);

    List<StockTemplatePredict> selectAllByDateAndTemplatedIdAndCodeAndBuyTimeGreaterThan(@Param("date") String date, @Param("templatedId") String templatedId, @Param("code") String code, @Param("minBuyTime") String minBuyTime);

    int deleteByDateAndTemplatedIdAndCode(@Param("date") String date, @Param("templatedId") String templatedId, @Param("code") String code);

    List<StockTemplatePredict> selectAllByDateBetweenEqualAndTemplatedIdAndHoldDay(@Param("minDate") String minDate, @Param("maxDate") String maxDate, @Param("templatedId") String templatedId, @Param("templatedSign") String templatedSign, @Param("holdDay") Integer holdDay);

    List<StockTemplatePredict> selectAllByDateBetweenEqualAndTemplatedSign(@Param("minDate")String minDate,@Param("maxDate")String maxDate,@Param("templatedSign")String templatedSign);


    int deleteByDateAndTempatedId(@Param("date") String date, @Param("tempatedId") String tempatedId);

    int deleteByTemplatedIdAndHoldDayAndDateBetweenEqual(@Param("templatedId") String templatedId, @Param("holdDay") Integer holdDay, @Param("minBuyTime") String minBuyTime, @Param("maxBuyTime") String maxBuyTime);
}