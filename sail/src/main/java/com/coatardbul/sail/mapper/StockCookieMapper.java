package com.coatardbul.sail.mapper;

import com.coatardbul.sail.model.entity.StockCookie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockCookieMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockCookie record);

    int insertSelective(StockCookie record);

    StockCookie selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockCookie record);

    int updateByPrimaryKey(StockCookie record);


    List<StockCookie> selectAll();


    int updateCookieValue(@Param("updatedCookieValue")String updatedCookieValue);




}