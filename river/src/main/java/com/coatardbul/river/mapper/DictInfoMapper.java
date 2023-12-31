package com.coatardbul.river.mapper;

import com.coatardbul.baseCommon.model.entity.DictInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(DictInfo record);

    int insertSelective(DictInfo record);

    DictInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DictInfo record);

    int updateByPrimaryKey(DictInfo record);

    List<DictInfo> selectAllByBusiType(@Param("busiType")String busiType);


    List<DictInfo> selectAllByBusiTypeAndSignKey(@Param("busiType")String busiType,@Param("signKey")String signKey);


}