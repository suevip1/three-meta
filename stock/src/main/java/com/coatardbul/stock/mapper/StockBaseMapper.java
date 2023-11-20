package com.coatardbul.stock.mapper;

import com.coatardbul.baseCommon.model.entity.StockBase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockBaseMapper {
    int deleteByPrimaryKey(String code);

    int insert(StockBase record);

    int insertSelective(StockBase record);

    StockBase selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(StockBase record);

    int updateByPrimaryKey(StockBase record);

    List<StockBase> selectByAll(StockBase stockBase);

    List<StockBase> selectByCodeLike(@Param("likeCode") String likeCode);
}