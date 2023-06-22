package com.coatardbul.river.mapper;

import com.coatardbul.baseCommon.model.entity.AuthUser;import org.apache.ibatis.annotations.Param;import java.util.List;

public interface AuthUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(AuthUser record);

    int insertSelective(AuthUser record);

    AuthUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AuthUser record);

    int updateByPrimaryKey(AuthUser record);

    List<AuthUser> selectAllByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}