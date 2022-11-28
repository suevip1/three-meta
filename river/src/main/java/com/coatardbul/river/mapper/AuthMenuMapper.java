package com.coatardbul.river.mapper;
import org.apache.ibatis.annotations.Param;

import com.coatardbul.river.model.entity.AuthMenu;
import org.apache.ibatis.annotations.Mapper;import java.util.List;

@Mapper
public interface AuthMenuMapper {
    int deleteByPrimaryKey(String id);

    int deleteByParentMenuId(@Param("parentMenuId")String parentMenuId);



    int insert(AuthMenu record);

    int insertSelective(AuthMenu record);

    AuthMenu selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AuthMenu record);

    int updateByPrimaryKey(AuthMenu record);



    List<AuthMenu> selectAllByParentMenuIdAndMenuNameLikeAndRouterUrl(@Param("parentMenuId")String parentMenuId,@Param("likeMenuName")String likeMenuName,@Param("routerUrl")String routerUrl);



    Integer selectMaxSequent();
}