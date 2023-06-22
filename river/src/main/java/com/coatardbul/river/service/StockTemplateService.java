package com.coatardbul.river.service;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.feign.BaseServerFeign;
import com.coatardbul.river.common.constants.DateTypeEnum;
import com.coatardbul.river.mapper.AuthCalendarMapper;
import com.coatardbul.river.mapper.StockQueryTemplateMapper;
import com.coatardbul.river.model.bo.DateEnumBo;
import com.coatardbul.river.model.dto.StockTemplateDTO;
import com.coatardbul.river.model.dto.StockTemplateListQueryDTO;
import com.coatardbul.river.model.dto.StockTemplateQueryDTO;
import com.coatardbul.river.model.entity.AuthCalendar;
import com.coatardbul.river.model.entity.StockQueryTemplate;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/1/1
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockTemplateService {

    /**
     * 当前交易日  YYYY年MM月DD日
     */
    private static final String TODAY_STR = "{{today}}";

    private static final String TODAY_STR_REGEX = "\\{\\{today}}";

    /**
     * 当前交易时间  HH点mm分
     */
    private static final String TIME_STR = "{{time}}";

    private static final String TIME_STR_REGEX = "\\{\\{time}}";


    /**
     * 股票代码  包含001001
     */
    private static final String STOCK_CODE_STR = "包含{{stockCode}}";

    private static final String STOCK_CODE_REGEX = "\\{\\{stockCode}}";

    /**
     * 题材
     */
    private static final String THEME_STR = "{{theme}}";
    private static final String THEME_STR_REGEX = "\\{\\{theme}}";

    /**
     * 上一个交易日
     */
    private static final String LAST_DAY = "{{lastDay}}";

    private static final String LAST_DAY_STRING = "lastDay";

    private static final String STOCK_CODE = "[包]{1}[含]{1}[0-9]{6}";


    /**
     * 后一个交易日
     */
    private static final String AFTER_DAY = "{{afterDay}}";

    private static final String AFTER_DAY_STRING = "afterDay";


    private static final String LAST_DAY_REGEX = "\\{\\{lastDay}}";


    private static final String DATE_STR = "yyyy年MM月dd日";

    /**
     * 匹配YYYY年M月DD日
     * u4E00-u9FA5 汉字通用匹配符号
     */
//    private static final String YEAR_MONTH_DAY = "[0-9]{4}[\u4E00-\u9FA5]{1}[0-9]{2}[\u4E00-\u9FA5]{1}[0-9]{2}[\u4E00-\u9FA5]{1}";
    private static final String YEAR_MONTH_DAY = "[0-9]{4}[年]{1}[0-9]{2}[月]{1}[0-9]{2}[日]{1}";


    //中文汉字
    private static final String CHINESE_WORD = "[\u4E00-\u9FA5]";


    private static final String HOUR_MINUTE_STR = "HH点mm分";
    private static final String HOUR_MINUTE = "[0-9]{2}[点]{1}[0-9]{2}[分]";


    private static final String TEMPLATE_QUERY_SPLIT = "，";

    private static final String ID_SPLIT = ",";


    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    StockQueryTemplateMapper stockQueryTemplateMapper;
    @Autowired
    AuthCalendarMapper authCalendarMapper;

    /**
     * @param dto
     */
    public void add(StockTemplateDTO dto) throws BusinessException, ParseException {

        String templateScript = getScriptByExampleStr(dto);

        List<StockQueryTemplate> nameList = stockQueryTemplateMapper.selectAllByNameAndScriptStr(dto.getName(), null);
        if (nameList != null && nameList.size() > 0) {
            throw new BusinessException("当前名称重复");
        }
        List<StockQueryTemplate> templateScriptList = stockQueryTemplateMapper.selectAllByNameAndScriptStr(null, templateScript);
        if (templateScriptList != null && templateScriptList.size() > 0) {
            throw new BusinessException("当前模板重复");
        }
        StockQueryTemplate addStockQueryTemplate = convert(dto, templateScript);
        addStockQueryTemplate.setId(baseServerFeign.getSnowflakeId());
        addStockQueryTemplate.setHotValue(1);
        stockQueryTemplateMapper.insert(addStockQueryTemplate);

    }


    /**
     * 根据例子获取模板脚本，
     * 支持
     * 1.年月日，2022年12月22日，今天，昨天，前天
     * 2.09点30分
     * 3.股票代码  包含001001
     *
     * @param dto
     * @return
     */
    private String getScriptByExampleStr(StockTemplateDTO dto) throws ParseException {
        String templateScript = getMatchDateScript(dto.getExampleStr(), dto.getTodayStr());
        templateScript = getMatchTimeScript(templateScript);
        templateScript = getMatchStockCodeScript(templateScript);
        templateScript = getMatchThemeScript(templateScript, dto.getThemeStr());
        return templateScript;
    }


    /**
     * 取代字符串中的时间 HH点mm分
     *
     * @param exampleStr
     * @return
     */
    private String getMatchStockCodeScript(String exampleStr) {
        //包含001001 共6位
        int strLength = 8;
        String stockCodeMatchStr = "";
        for (int i = 0; i <= exampleStr.length() - strLength; i++) {
            String substring = exampleStr.substring(i, i + strLength);
            if (substring.matches(STOCK_CODE)) {
                stockCodeMatchStr = substring;
                break;
            }
        }
        if (StringUtils.isNotBlank(stockCodeMatchStr)) {
            return exampleStr.replace(stockCodeMatchStr, STOCK_CODE_STR);
        } else {
            return exampleStr;
        }
    }


    /**
     * 获取指定题材脚本
     *
     * @param exampleStr
     * @return
     */
    private String getMatchThemeScript(String exampleStr, String themeStr) {
        if (StringUtils.isNotBlank(themeStr)) {
            return exampleStr.replace(themeStr, THEME_STR);
        } else {
            return exampleStr;
        }

    }


    /**
     * 取代字符串中的时间 HH点mm分
     *
     * @param exampleStr
     * @return
     */
    private String getMatchTimeScript(String exampleStr) {
        //09点30分共6位
        int strLength = 6;
        String timeMatchStr = "";
        for (int i = 0; i < exampleStr.length() - strLength; i++) {
            String substring = exampleStr.substring(i, i + strLength);
            if (substring.matches(HOUR_MINUTE)) {
                timeMatchStr = substring;
                break;
            }
        }
        if (StringUtils.isNotBlank(timeMatchStr)) {
            return exampleStr.replace(timeMatchStr, TIME_STR);
        } else {
            return exampleStr;
        }
    }


    /**
     * 匹配YYYY年MM月DD日
     *
     * @param exampleStr
     * @return
     */
    private String getMatchDateScript(String exampleStr, String todayStr) throws ParseException {
        String templateScript = exampleStr;
        //2022年11月11日 数组
        List<String> dateChStrArr = new ArrayList<>();
        //2022年11月11日共11位
        int strLength = 11;
        for (int i = 0; i < exampleStr.length() - strLength; i++) {
            String substring = exampleStr.substring(i, i + strLength);
            if (substring.matches(YEAR_MONTH_DAY)) {
                //中文
                dateChStrArr.add(substring);
            }
        }
        //YYYY-MM-DD
        List<String> dateFormatStrArr = dateChStrArr.stream().map(o1 -> {
            try {
                return DateTimeUtil.parseDateStr(o1, DateTimeUtil.YYYY_MM_DD_WORD, DateTimeUtil.YYYY_MM_DD);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
                return "";
            }
        }).collect(Collectors.toList());
        //查询
        if (dateFormatStrArr.size() > 0) {
            //升序
            dateFormatStrArr = dateFormatStrArr.stream().distinct().sorted().collect(Collectors.toList());
            //查询中间所有的工作日
            List<AuthCalendar> authCalendars = authCalendarMapper.selectAllByDateBetweenEqualAndDateProp(dateFormatStrArr.get(0), dateFormatStrArr.get(dateFormatStrArr.size() - 1), DateTypeEnum.WORK_DAY.getCode());
            if (authCalendars.size() == 0) {
                return null;
            }
            //降序排列,yyyy-MM-DD
            List<String> dayStrList = authCalendars.stream().map(AuthCalendar::getDate).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            //当日转换为yyyy-MM-DD
            todayStr = StringUtils.isNotBlank(todayStr) ? DateTimeUtil.parseDateStr(todayStr, DateTimeUtil.YYYY_MM_DD_WORD, DateTimeUtil.YYYY_MM_DD) : dayStrList.get(0);
            List<DateEnumBo> scriptDateArr = getScriptDateArr(dayStrList, todayStr);
            for (int i = 0; i < dayStrList.size(); i++) {
                int finalI = i;
                List<DateEnumBo> filterDateInfo = scriptDateArr.stream().filter(o1 -> o1.getDateFormatStr().equals(dayStrList.get(finalI))).collect(Collectors.toList());
                templateScript = templateScript.replace(filterDateInfo.get(0).getDateChName(), filterDateInfo.get(0).getDateScript());
            }
        }
        return templateScript;
    }


    /**
     * 获取日期表达式，可以多天
     *
     * @param dayList  为降序的YYYY-MM-DD 集合
     * @param todayStr 当日的   YYYY-MM-DD
     * @return
     */
    private List<String> getScriptDateStr(List<String> dayList, String todayStr) {
        List<String> result = new ArrayList<>();
        List<String> lastDayAndToday = new ArrayList<>();
        if (StringUtils.isNotBlank(todayStr)) {
            List<String> afterDayList = dayList.stream().filter(o1 -> o1.compareTo(todayStr) > 0).collect(Collectors.toList());
            for (int i = afterDayList.size(); i > 0; i--) {
                result.add(AFTER_DAY.replace(AFTER_DAY_STRING, AFTER_DAY_STRING + i));
            }
            lastDayAndToday = dayList.stream().filter(o1 -> o1.compareTo(todayStr) <= 0).collect(Collectors.toList());
        } else {
            lastDayAndToday = dayList;
        }
        result.add(TODAY_STR);
        for (int i = 1; i < lastDayAndToday.size(); i++) {
            result.add(LAST_DAY.replace(LAST_DAY_STRING, LAST_DAY_STRING + i));
        }
        return result;
    }


    /**
     * @param dayList  包含今天日期，这样才能取到最后一个值
     * @param todayStr YYYY-MM-DD
     * @return
     * @throws ParseException
     */
    private List<DateEnumBo> getScriptDateArr(List<String> dayList, String todayStr) throws ParseException {
        List<DateEnumBo> result = new ArrayList<>();
        List<String> lastDayAndToday = new ArrayList<>();
        if (StringUtils.isNotBlank(todayStr)) {
            List<String> afterDayList = dayList.stream().filter(o1 -> o1.compareTo(todayStr) >= 0).sorted().collect(Collectors.toList());
            for (int i = afterDayList.size() - 1; i > 0; i--) {
                DateEnumBo dateEnumBo = new DateEnumBo();
                dateEnumBo.setDateChName(DateTimeUtil.parseDateStr(afterDayList.get(i), DateTimeUtil.YYYY_MM_DD, DateTimeUtil.YYYY_MM_DD_WORD));
                dateEnumBo.setDateFormatStr(afterDayList.get(i));
                dateEnumBo.setDateScript(AFTER_DAY.replace(AFTER_DAY_STRING, AFTER_DAY_STRING + i));
//                todayDateEnumInfo.setDateReplaceScript();
                result.add(dateEnumBo);
            }
            lastDayAndToday = dayList.stream().filter(o1 -> o1.compareTo(todayStr) <= 0).collect(Collectors.toList());
        } else {
            lastDayAndToday = dayList;
        }
        DateEnumBo todayDateEnumInfo = new DateEnumBo();
        todayDateEnumInfo.setDateChName(DateTimeUtil.parseDateStr(todayStr, DateTimeUtil.YYYY_MM_DD, DateTimeUtil.YYYY_MM_DD_WORD));
        todayDateEnumInfo.setDateFormatStr(todayStr);
        todayDateEnumInfo.setDateScript(TODAY_STR);
        result.add(todayDateEnumInfo);
        for (int i = 1; i < lastDayAndToday.size(); i++) {
            DateEnumBo dateEnumBo = new DateEnumBo();
            dateEnumBo.setDateChName(DateTimeUtil.parseDateStr(lastDayAndToday.get(i), DateTimeUtil.YYYY_MM_DD, DateTimeUtil.YYYY_MM_DD_WORD));
            dateEnumBo.setDateFormatStr(lastDayAndToday.get(i));
            dateEnumBo.setDateScript(LAST_DAY.replace(LAST_DAY_STRING, LAST_DAY_STRING + i));
            result.add(dateEnumBo);
        }
        return result;
    }


    public List<StockQueryTemplate> getList(StockTemplateListQueryDTO dto) {
        List<StockQueryTemplate> result = stockQueryTemplateMapper.selectByIdAndNameLikeAndExampleStrLike(dto.getId(), dto.getName(), dto.getExampleStr());
        return result;
    }

    public void modify(StockTemplateDTO dto) throws ParseException {
        StockQueryTemplate update = convert(dto, null);
        update.setId(dto.getId());
        stockQueryTemplateMapper.updateByPrimaryKeySelective(update);
    }


    private StockQueryTemplate convert(StockTemplateDTO dto, String templateScript) throws ParseException {
        StockQueryTemplate addStockQueryTemplate = new StockQueryTemplate();
        if (StringUtils.isNotBlank(templateScript)) {
            addStockQueryTemplate.setScriptStr(templateScript);
        } else {
            addStockQueryTemplate.setScriptStr(getScriptByExampleStr(dto));
        }
        addStockQueryTemplate.setName(dto.getName());
        addStockQueryTemplate.setExampleStr(dto.getExampleStr());
        addStockQueryTemplate.setRemark(dto.getRemark());
        addStockQueryTemplate.setTodayStr(dto.getTodayStr());
        addStockQueryTemplate.setThemeStr(dto.getThemeStr());
        addStockQueryTemplate.setTemplateSign(dto.getTemplateSign());
        return addStockQueryTemplate;
    }


    public void addHot(StockTemplateDTO dto) {
        StockQueryTemplate stockQueryTemplate = stockQueryTemplateMapper.selectByPrimaryKey(dto.getId());

        StockQueryTemplate updateInfo = new StockQueryTemplate();
        updateInfo.setId(dto.getId());
        if (stockQueryTemplate.getHotValue() == null) {
            updateInfo.setHotValue(1);
        } else {
            updateInfo.setHotValue(stockQueryTemplate.getHotValue().intValue() + 1);
        }
        stockQueryTemplateMapper.updateByPrimaryKeySelective(updateInfo);
    }


    public void delete(StockTemplateDTO dto) throws BusinessException {
        Assert.notNull(dto.getId(), "id不能为空");
        StockQueryTemplate stockQueryTemplate = stockQueryTemplateMapper.selectByPrimaryKey(dto.getId());
        if (stockQueryTemplate.getHotValue() > 20) {
            throw new BusinessException("常用问句禁止删除");
        }
        if (StringUtils.isNotBlank(stockQueryTemplate.getTemplateSign())) {
            throw new BusinessException("已有标识，禁止删除");
        }
        stockQueryTemplateMapper.deleteByPrimaryKey(dto.getId());
    }


    /**
     * 根据日期，时间，获取最终的查询语句，
     *
     * @param dto
     * @return
     * @throws ParseException
     */
    public String getQuery(StockTemplateQueryDTO dto) throws ParseException {
        if (StringUtils.isNotBlank(dto.getId())) {
            String[] idArray = getIdArray(dto.getId());
            String matchDateInfo = getFinalMatchDate(dto, idArray);
            matchDateInfo = getMatchTimeInfo(dto.getTimeStr(), matchDateInfo);
            matchDateInfo = getMatchStockCodeInfo(dto.getStockCode(), matchDateInfo);
            matchDateInfo = getMatchThemeInfo(dto.getThemeStr(), matchDateInfo);
            matchDateInfo = getFilterTemplateQuery(matchDateInfo);
            return matchDateInfo;
        } else if (StringUtils.isNotBlank(dto.getStockScript())) {
            String matchDateInfo = getMatchDateInfo(dto.getStockScript(), dto.getDateStr());
            matchDateInfo = getMatchTimeInfo(dto.getTimeStr(), matchDateInfo);
            matchDateInfo = getMatchStockCodeInfo(dto.getStockCode(), matchDateInfo);
            matchDateInfo = getMatchThemeInfo(dto.getThemeStr(), matchDateInfo);
            matchDateInfo = getFilterTemplateQuery(matchDateInfo);
            return matchDateInfo;
        } else if (StringUtils.isNotBlank(dto.getObjectSign())) {
            StockQueryTemplate stockQueryTemplate = stockQueryTemplateMapper.selectByTemplateSign(dto.getObjectSign());
            String matchDateInfo = getMatchDateInfo(stockQueryTemplate.getScriptStr(), dto.getDateStr());
            matchDateInfo = getMatchTimeInfo(dto.getTimeStr(), matchDateInfo);
            matchDateInfo = getMatchStockCodeInfo(dto.getStockCode(), matchDateInfo);
            matchDateInfo = getMatchThemeInfo(dto.getThemeStr(), matchDateInfo);
            matchDateInfo = getFilterTemplateQuery(matchDateInfo);
            return matchDateInfo;
        } else {
            return null;
        }


    }


    /**
     * 过滤问句，将合并后的问句去重返回
     *
     * @param matchDateInfo
     * @return
     */
    private String getFilterTemplateQuery(String matchDateInfo) {

        Map<String, String> map = new HashMap<>();
        StringBuilder result = new StringBuilder();
        String[] split = matchDateInfo.split(TEMPLATE_QUERY_SPLIT);
        for (String splitStr : split) {
            if (StringUtils.isNotBlank(splitStr.trim()) && !map.containsKey(splitStr.trim())) {
                map.put(splitStr.trim(), "1");
                result.append(splitStr).append(TEMPLATE_QUERY_SPLIT);
            }
        }
        return result.toString();
    }

    /**
     * 获取最终的日期匹配结果
     *
     * @param dto
     * @param idArray
     * @return
     * @throws ParseException
     */
    private String getFinalMatchDate(StockTemplateQueryDTO dto, String[] idArray) throws ParseException {
        String matchDateInfoResult = "";
        for (String id : idArray) {
            dto.setId(id);
            String matchDateInfo1 = getMatchDateInfo(dto);
            if (matchDateInfo1.endsWith(TEMPLATE_QUERY_SPLIT)) {
                matchDateInfoResult = matchDateInfoResult + matchDateInfo1;
            } else {
                matchDateInfoResult = matchDateInfoResult + matchDateInfo1 + TEMPLATE_QUERY_SPLIT;
            }
        }
        return matchDateInfoResult;
    }

    /**
     * 传入的id是多个，用,号隔开
     *
     * @param id
     * @return
     */
    private String[] getIdArray(String id) {
        return id.split(ID_SPLIT);
    }

    /**
     * 替换脚本中的股票代码
     *
     * @param stockCode
     * @param matchDateInfo
     * @return
     */
    private String getMatchStockCodeInfo(String stockCode, String matchDateInfo) {
        if (StringUtils.isNotBlank(stockCode)) {
            return matchDateInfo.replaceAll(STOCK_CODE_REGEX, stockCode);
        } else {
            return matchDateInfo;
        }
    }

    /**
     * 替换脚本中的股票代码
     *
     * @param themeInfo
     * @param matchDateInfo
     * @return
     */
    private String getMatchThemeInfo(String themeInfo, String matchDateInfo) {
        if (StringUtils.isNotBlank(themeInfo)) {
            return matchDateInfo.replaceAll(THEME_STR_REGEX, themeInfo);
        } else {
            return matchDateInfo.replaceAll(THEME_STR_REGEX, "");
        }
    }


    private String getMatchTimeInfo(String timeStr, String matchDateInfo) {
        if (StringUtils.isNotBlank(timeStr)) {
            String timeDesc = getTimeDesc(timeStr);
            return matchDateInfo.replaceAll(TIME_STR_REGEX, timeDesc);
        } else {
            //HH：mm 为空，为空
            return matchDateInfo.replaceAll(TIME_STR_REGEX, "");
        }
    }

    /**
     * 日期匹配
     *
     * @param dto
     * @return
     * @throws ParseException
     */
    private String getMatchDateInfo(StockTemplateQueryDTO dto) throws ParseException {
        StockQueryTemplate stockQueryTemplate = stockQueryTemplateMapper.selectByPrimaryKey(dto.getId());

        //脚本
        String scriptStr = stockQueryTemplate.getScriptStr();
        //脚本中替换日期
        return getMatchDateInfo(scriptStr, dto.getDateStr());
    }

    /**
     * 根据脚本，指定日期返回将日期替换的字符串
     *
     * @param scriptStr
     * @param dateStr
     * @return
     * @throws ParseException
     */
    private String getMatchDateInfo(String scriptStr, String dateStr) throws ParseException {
        Date date = DateTimeUtil.parseDateStr(dateStr, DateTimeUtil.YYYY_MM_DD);
        //yyyy年MM月dd日
        String dateFormatWord = DateTimeUtil.getDateFormat(date, DATE_STR);
        //将表达式中的日期参数提取出来
        List<DateEnumBo> dateEnumArr = getDateEnumArr(scriptStr);

        //包含上一天
        if (scriptStr.contains(LAST_DAY_STRING)) {
            //过滤取最大值
            Integer maxNum = dateEnumArr.stream().filter(o1 -> o1.getDateScript().contains(LAST_DAY_STRING)).map(DateEnumBo::getNum).max(Comparator.comparing(Integer::intValue)).get();
            PageHelper.startPage(0, maxNum + 1);
            //排序获取最近几天数据
            List<String> dateList = authCalendarMapper.selectDateListByDateLessThanAndDateProp(dateStr, DateTypeEnum.WORK_DAY.getCode());
            List<DateEnumBo> scriptDateArr = getScriptDateArr(dateList, dateStr);
            scriptStr = setReplaceDateTime(dateEnumArr, scriptDateArr, scriptStr);
        }
        if (scriptStr.contains(AFTER_DAY_STRING)) {
            //过滤取最大值
            Integer maxNum = dateEnumArr.stream().filter(o1 -> o1.getDateScript().contains(AFTER_DAY_STRING)).map(DateEnumBo::getNum).max(Comparator.comparing(Integer::intValue)).get();
            PageHelper.startPage(0, maxNum + 1);
            //排序获取最近几天数据
            List<String> dateList = authCalendarMapper.selectDateListByDateGreaterThanAndDateProp(dateStr, DateTypeEnum.WORK_DAY.getCode());
            List<DateEnumBo> scriptDateArr = getScriptDateArr(dateList, dateStr);
            scriptStr = setReplaceDateTime(dateEnumArr, scriptDateArr, scriptStr);
        }
        if (scriptStr.contains(TODAY_STR)) {
            scriptStr = scriptStr.replaceAll(TODAY_STR_REGEX, dateFormatWord);
        }
        return scriptStr;

    }

    private List<DateEnumBo> getDateEnumArr(String scriptStr) {

        List<DateEnumBo> list = new ArrayList();
        String reg = "\\{\\{[a-zA-Z]+[0-9]*}}";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(scriptStr);
        while (m.find()) {
            DateEnumBo dateEnumBo = new DateEnumBo();
//            dateEnumBo.setDateChName();
//            dateEnumBo.setDateFormatStr();
            dateEnumBo.setDateScript(m.group());
            dateEnumBo.setNum(getSpecialNum(m.group(), "[0-9]+"));
            list.add(dateEnumBo);
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    private Integer getSpecialNum(String str, String regex) {
        String specialStr = getSpecialStr(str, regex);
        if (StringUtils.isNotBlank(specialStr)) {
            return Integer.valueOf(specialStr);
        } else {
            return 0;
        }
    }

    /**
     * 根据正则表达，获取特定的正则信息
     *
     * @param str
     * @param regex
     * @return
     */
    private String getSpecialStr(String str, String regex) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return "";
        }
    }

    /**
     * @param timeStr HH:MM
     * @return HH点MM分
     */
    private String getTimeDesc(String timeStr) {
        return timeStr.replace(":", "点") + "分";
    }


    /**
     * 转换成对应的字符串
     *
     * @param dateList yyyy-MM-dd 类型
     * @return yyyy年MM月dd日 类型
     * @throws ParseException
     */
    private List<String> convertDateFormat(List<String> dateList) throws ParseException {
        List<String> result = new ArrayList<>();
        for (String str : dateList) {
            Date lastDate = DateTimeUtil.parseDateStr(str, DateTimeUtil.YYYY_MM_DD);
            String lastDateFormat = DateTimeUtil.getDateFormat(lastDate, DATE_STR);
            result.add(lastDateFormat);
        }
        return result;
    }


    /**
     * 将字符串中的 {{lastday1}}取代成对应的日期
     *
     * @param dateEnumArr   表达式中的日期参数提取出来
     * @param scriptDateArr 数据库中查询出来的语句
     * @param scriptStr     yyyy年MM月dd日 结合
     * @return 替换后的结果
     */
    private String setReplaceDateTime(List<DateEnumBo> dateEnumArr, List<DateEnumBo> scriptDateArr, String scriptStr) {
        String result = null;
        for (int i = 0; i < dateEnumArr.size(); i++) {
            int finalI = i;
            List<DateEnumBo> findInfo = scriptDateArr.stream().filter(o1 -> o1.getDateScript().equals(dateEnumArr.get(finalI).getDateScript())).collect(Collectors.toList());
            if (findInfo.size() > 0) {
                if (StringUtils.isNotBlank(result)) {
                    //替换对应的i和对应的字符串
                    result = result.replaceAll(getReplaceRegexStr(findInfo.get(0).getDateScript()), findInfo.get(0).getDateChName());
                } else {
                    result = scriptStr.replaceAll(getReplaceRegexStr(findInfo.get(0).getDateScript()), findInfo.get(0).getDateChName());
                }
            }

        }
        return result;
    }


    private String getReplaceRegexStr(String str) {
        return str.replace("{", "\\{");
    }

    public StockQueryTemplate findOne(StockTemplateDTO dto) {
        return stockQueryTemplateMapper.selectByPrimaryKey(dto.getId());
    }


    public String exportEnum() {

        StringBuffer sb = new StringBuffer();
        List<StockQueryTemplate> list = stockQueryTemplateMapper.selectByAll();
        for (int i = 0; i < list.size(); i++) {
            // STOCK_DETAIL("STOCK_DETAIL", "股票详情", "1515522893696598016"),
            StockQueryTemplate stockQueryTemplate = list.get(i);

            String templateSign = "";
            if (StringUtils.isNotBlank(stockQueryTemplate.getTemplateSign())) {
                templateSign=stockQueryTemplate.getTemplateSign();
            }else {
                templateSign="a"+i;
            }
            sb.append(templateSign + "(\"" + templateSign + "\",  \"" + stockQueryTemplate.getName() + "\", \"" + stockQueryTemplate.getId() + "\"),\n");
        }
        log.info("11111111111");

        log.info(sb.toString());
        return sb.toString();
    }
}



