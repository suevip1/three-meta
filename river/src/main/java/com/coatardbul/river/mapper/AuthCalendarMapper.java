package com.coatardbul.river.mapper;

import com.coatardbul.river.model.entity.AuthCalendar;
import org.apache.ibatis.annotations.Mapper;import org.apache.ibatis.annotations.Param;import java.util.List;

@Mapper
public interface AuthCalendarMapper {
    int deleteByPrimaryKey(String id);

    int insert(AuthCalendar record);

    int insertSelective(AuthCalendar record);

    AuthCalendar selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AuthCalendar record);

    int updateByPrimaryKey(AuthCalendar record);

    List<AuthCalendar> selectByAll(AuthCalendar authCalendar);

    String selectMaxDateByDateLessThanAndDateProp(@Param("maxDate")String maxDate, @Param("dateProp") Integer dateProp);

    List<String> selectDateListByDateLessThanAndDateProp(@Param("maxDate")String maxDate, @Param("dateProp") Integer dateProp);


    List<String> selectDateListByDateGreaterThanAndDateProp(@Param("minDate")String minDate, @Param("dateProp")Integer dateProp);

    List<AuthCalendar> selectAllByDateGreaterThanAndDateProp(@Param("minDate")String minDate, @Param("dateProp")Integer dateProp);



    List<AuthCalendar> selectAllByDateBetween(@Param("minDate") String minDate, @Param("maxDate") String maxDate, @Param("dateProp") Integer dateProp);


    /**
     *
     * @param minDate  不包含开始时间
     * @param maxDate 包含结束时间
     * @param dateProp
     * @return
     */
    Integer selectCountByDateBetweenEqualAndDateProp(@Param("minDate")String minDate,@Param("maxDate")String maxDate,@Param("dateProp")Integer dateProp);


    List<AuthCalendar> selectAllByDateBetweenEqualAndDateProp(@Param("minDate")String minDate,@Param("maxDate")String maxDate,@Param("dateProp")Integer dateProp);



    List<AuthCalendar> selectAllByDateLessThanAndDateProp(@Param("maxDate")String maxDate,@Param("dateProp")Integer dateProp);



}