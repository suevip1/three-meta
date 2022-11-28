package com.coatardbul.river.mapper;
import org.apache.ibatis.annotations.Param;

import com.coatardbul.river.model.entity.AuthUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(AuthUser record);

    int insertSelective(AuthUser record);

    AuthUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AuthUser record);

    int updateByPrimaryKey(AuthUser record);

    List<AuthUser> selectAllByUsernameAndPassword(@Param("username")String username,@Param("password")String password);



}