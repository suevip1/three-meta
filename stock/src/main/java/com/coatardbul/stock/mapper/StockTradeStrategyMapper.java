package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.StockTradeStrategy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockTradeStrategyMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTradeStrategy record);

    int insertSelective(StockTradeStrategy record);

    StockTradeStrategy selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTradeStrategy record);

    int updateByPrimaryKey(StockTradeStrategy record);

    List<StockTradeStrategy> selectAllByTypeAndNameLikeAndExpressExampleLike(@Param("type")String type,@Param("likeName")String likeName,@Param("likeExpressExample")String likeExpressExample);



    StockTradeStrategy selectAllBySign(@Param("sign")String sign);


}