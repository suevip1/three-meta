package com.coatardbul.sail.mapper;

import com.coatardbul.sail.model.entity.ProxyIp;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ProxyIpMapper {
    int deleteByPrimaryKey(String id);

    int insert(ProxyIp record);

    int insertSelective(ProxyIp record);

    ProxyIp selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ProxyIp record);

    int updateByPrimaryKey(ProxyIp record);

    List<ProxyIp> selectAllByCreateTimeGreaterThanEqualAndUseTimeLessThanEqual(@Param("minCreateTime")Date minCreateTime,@Param("maxUseTime")Integer maxUseTime);


    int deleteByCreateTimeLessThanEqual(@Param("maxCreateTime")Date maxCreateTime);


    int deleteByIp(@Param("ip")String ip);


    List<ProxyIp> selectAll();


}