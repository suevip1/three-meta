package com.coatardbul.stock.mapper;

import com.coatardbul.stock.model.entity.AccountBase;import org.apache.ibatis.annotations.Param;

public interface AccountBaseMapper {
    int deleteByPrimaryKey(String id);

    int insert(AccountBase record);

    int insertSelective(AccountBase record);

    AccountBase selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AccountBase record);

    int updateByPrimaryKey(AccountBase record);

    int updateByUserIdAndTradeTypeSelective(@Param("updated")AccountBase updated);



    AccountBase selectByUserIdAndTradeType(@Param("userId") String userId, @Param("tradeType") String tradeType);
}