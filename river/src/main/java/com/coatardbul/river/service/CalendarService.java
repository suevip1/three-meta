package com.coatardbul.river.service;


import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.constants.CommonStatusEnum;
import com.coatardbul.river.common.constants.DateTypeEnum;
import com.coatardbul.river.feign.BaseServerFeign;
import com.coatardbul.river.mapper.AuthCalendarMapper;
import com.coatardbul.river.model.dto.CalendarDTO;
import com.coatardbul.river.model.dto.CalendarDateDTO;
import com.coatardbul.river.model.dto.CalendarSpecialDTO;
import com.coatardbul.river.model.entity.AuthCalendar;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huixianwang
 * @version 2019/12/9 3:43 下午
 */
@Service
@Slf4j
public class CalendarService {


    @Autowired
    AuthCalendarMapper authCalendarMapper;
    @Autowired
    BaseServerFeign baseServerFeign;

    public void widerCalendar(CalendarDTO calendarDTO) {
        List<String> rangeMonth = DateTimeUtil.getRangeMonth(calendarDTO.getBeginMonth(), calendarDTO.getEndMonth());
        List<String> rangeUrl = getCalendarUrl(rangeMonth);
        rangeUrl.forEach(o1 -> {
            try {
                excuseUrlAndParse(o1);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });

    }


    private void excuseUrlAndParse(String url) throws IOException {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(url);
        // 响应模型
        CloseableHttpResponse response = null;

        // 由客户端执行(发送)Post请求
        response = httpClient.execute(httpGet);
        // 从响应模型中获取响应实体
        HttpEntity responseEntity = response.getEntity();
        String str = EntityUtils.toString(responseEntity);
        Document doc = Jsoup.parse(str);
        // 所有#id的标签
        Elements elements = doc.select("body");
        // 返回第一个
        Element body1 = doc.select("body").get(0);


        List<AuthCalendar> result1 = new ArrayList<>();

        //日期信息
        List<Node> nodes = body1.children().get(0).childNodes().get(0).childNodes();
        //2019年04月
        String yearMonth = nodes.get(0).childNodes().get(13).childNodes().get(0).toString();
        yearMonth = yearMonth.substring(0, 4) + "-" + yearMonth.substring(5, 7);
        int week = 1;
        for (int i = 14; i < nodes.size(); i++) {


            if ("wnrl_kongbai".equals(nodes.get(i).attr("class"))) {
                //空白,占位符，1号之前，月末填补空白
            }
            if ("wnrl_riqi".equals(nodes.get(i).attr("class"))) {
                AuthCalendar authCalendar = new AuthCalendar();
                authCalendar.setId(baseServerFeign.getSnowflakeId());
                authCalendar.setWeek(week % 7);
                authCalendar.setStatus(CommonStatusEnum.VALID.getCode().intValue());
                String solarDay = nodes.get(i).childNodes().get(0).childNodes().get(0).childNodes().get(0).toString();
                authCalendar.setDate(yearMonth + "-" + solarDay);
                String lunarDateName = nodes.get(i).childNodes().get(0).childNodes().get(1).childNodes().get(0).toString();
                authCalendar.setLunar(lunarDateName);
                //日期
                if (StringUtils.isBlank(nodes.get(i).childNodes().get(0).attr("class"))) {
                    //空白,正常工作日，不计算
                    authCalendar.setDateProp(DateTypeEnum.WORK_DAY.getCode());
                } else {
                    if ("wnrl_riqi_ban".equals(nodes.get(i).childNodes().get(0).attr("class"))) {
                        //周六周日加班
                        authCalendar.setDateProp(DateTypeEnum.WORK_WEEK_DAY.getCode());
                    } else {

                        if ("wnrl_riqi_mo".equals(nodes.get(i).childNodes().get(0).attr("class"))) {
                            //周末
                            authCalendar.setDateProp(DateTypeEnum.WEEK_DAY.getCode());
                        }
                        if ("wnrl_riqi_xiu".equals(nodes.get(i).childNodes().get(0).attr("class"))) {
                            //休息
                            authCalendar.setDateProp(DateTypeEnum.HOLIDAY.getCode());

                        }
                    }

                }
                result1.add(authCalendar);

            }
            week++;
        }
        List<AuthCalendar> result2 = new ArrayList<>();

        List<Node> nodes1 = body1.children().get(0).childNodes();
        for (int i = 1; i < nodes1.size(); i++) {
            AuthCalendar a = new AuthCalendar();
            String solarDay = nodes1.get(i).childNodes().get(1).childNodes().get(0).toString().trim();
            a.setDate(yearMonth + "-" + solarDay);
            String lunarDateName = nodes1.get(i).childNodes().get(2).childNodes().get(0).toString().trim();
            a.setLunar(lunarDateName);
            result2.add(a);
        }
        Map<String, AuthCalendar> map = result2.stream().collect(Collectors.toMap(AuthCalendar::getDate, Function.identity()));
        result1.forEach(o1 -> {
            if (map.containsKey(o1.getDate())) {
                if (!map.get(o1.getDate()).getLunar().contains(o1.getLunar())) {
                    o1.setHolidayName(o1.getLunar());
                }
                o1.setLunar(map.get(o1.getDate()).getLunar());
                try {
                    authCalendarMapper.insert(o1);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * @param str
     * @return
     */
    private List<String> getCalendarUrl(List<String> str) {
        if (str != null && str.size() > 0) {
            return str.stream().map(o1 -> "https://wannianrili.51240.com/ajax/?q=" + o1 + "&v=20031909").collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }





    public List<AuthCalendar> getCalendarList(CalendarDTO calendarDTO) {
        List<AuthCalendar> authCalendars = authCalendarMapper.selectAllByDateBetween(calendarDTO.getBeginMonth(), calendarDTO.getEndMonth(),calendarDTO.getDateProp());

        if (authCalendars != null && authCalendars.size() > 0) {
            return authCalendars.stream().map(this::convert).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private AuthCalendar convert(AuthCalendar authCalendar) {
        authCalendar.setId(null);
        authCalendar.setStatus(null);
        return authCalendar;
    }


    public List<String> getDate(CalendarDateDTO dto) {
        if(StringUtils.isNotBlank(dto.getBeginDate())&&StringUtils.isNotBlank(dto.getEndDate())){
            List<AuthCalendar> authCalendars = authCalendarMapper.selectAllByDateBetweenEqualAndDateProp(dto.getBeginDate(), dto.getEndDate(), dto.getDateProp());
            if(authCalendars!=null &&authCalendars.size()>0){
                return authCalendars.stream().map(AuthCalendar::getDate).collect(Collectors.toList());
            }else {
                return new ArrayList<>();
            }
        }

        if(dto.getAddDay()!=null){
            CalendarSpecialDTO calendarSpecialDTO=new CalendarSpecialDTO();
            String endDateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
            calendarSpecialDTO.setDateStr(endDateStr);
            calendarSpecialDTO.setDateProp(1);
            calendarSpecialDTO.setAddDay(dto.getAddDay());
            String beginDateStr = getSpecialDay(calendarSpecialDTO);
            dto.setBeginDate(beginDateStr);
            dto.setEndDate(endDateStr);
            return getDate(dto);
        }
        return new ArrayList<>();
    }


    public String getSpecialDay(CalendarSpecialDTO calendarDTO) {
        //数字大于0，后几天
        if(calendarDTO.getAddDay()>0){
            PageHelper.startPage(1,calendarDTO.getAddDay());
            List<AuthCalendar> authCalendars = authCalendarMapper.selectAllByDateGreaterThanAndDateProp(calendarDTO.getDateStr(), calendarDTO.getDateProp());
            return authCalendars.get(authCalendars.size()-1).getDate();
        }else {
            PageHelper.startPage(1,0-calendarDTO.getAddDay());
            List<AuthCalendar> authCalendars = authCalendarMapper.selectAllByDateLessThanAndDateProp(calendarDTO.getDateStr(), calendarDTO.getDateProp());
            return authCalendars.get(authCalendars.size()-1).getDate();
        }

    }

    public Integer getSubtractDay(CalendarDateDTO dto) {
        return  authCalendarMapper.selectCountByDateBetweenEqualAndDateProp(dto.getBeginDate(),dto.getEndDate(),dto.getDateProp());
    }
}
