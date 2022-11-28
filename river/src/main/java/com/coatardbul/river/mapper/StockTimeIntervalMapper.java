package com.coatardbul.river.mapper;
import com.coatardbul.river.model.bo.IntervalStaticBo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.coatardbul.river.model.entity.StockTimeInterval;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockTimeIntervalMapper {
    int deleteByPrimaryKey(String id);

    int insert(StockTimeInterval record);

    int insertSelective(StockTimeInterval record);

    StockTimeInterval selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockTimeInterval record);

    int updateByPrimaryKey(StockTimeInterval record);

    List<StockTimeInterval> selectAllByIntervalType(@Param("intervalType")Integer intervalType);

int deleteByIntervalType(@Param("intervalType")Integer intervalType);


List<IntervalStaticBo> selectIntervalStatic();


}