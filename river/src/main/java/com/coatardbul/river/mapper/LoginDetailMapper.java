package com.coatardbul.river.mapper;

import com.coatardbul.river.model.entity.LoginDetail;

public interface LoginDetailMapper {
    int deleteByPrimaryKey(String id);

    int insert(LoginDetail record);

    int insertSelective(LoginDetail record);

    LoginDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(LoginDetail record);

    int updateByPrimaryKey(LoginDetail record);
}