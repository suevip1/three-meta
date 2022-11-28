package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.BigRoot;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.constants.UpDwonEnum;
import com.coatardbul.baseService.entity.bo.BeginFiveTickScore;
import com.coatardbul.baseService.entity.bo.TickInfo;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/25
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public abstract class CommonService {
    @Resource
 public    CronRefreshService cronRefreshService;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StockUpLimitAnalyzeService stockUpLimitAnalyzeService;
    public StockStrategyCommonService stockStrategyCommonService;

    @Autowired
    public void setStockStrategyCommonService(StockStrategyCommonService stockStrategyCommonService) {
        this.stockStrategyCommonService = stockStrategyCommonService;
    }

    public void addCommonParam(Map stockDetailMap) {
        stockDetailMap.put("lastUpdateTime", DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
    }


    /**
     * 获取股票详情信息
     *
     * @param code
     * @param dateFormat
     * @return
     */
    public Map getStockDetailMap(String code, String dateFormat) {
        //金额判断,股票详情
        StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
        dto.setRiverStockTemplateSign(StockTemplateEnum.STOCK_DETAIL.getSign());
        dto.setDateStr(dateFormat);
        dto.setStockCode(code);
        StrategyBO strategy = null;
        try {
            strategy = stockStrategyCommonService.strategy(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (strategy == null || strategy.getTotalNum() == 0) {
            return null;
        }
        JSONObject jsonObject = strategy.getData().getJSONObject(0);
        Map convert = stockUpLimitAnalyzeService.convert(jsonObject, dateFormat);
        return convert;
    }

    public void calcMap(Map map) {
        //竞价涨幅=(竞价-昨日价格)/昨日价格
        map.put("auctionIncreaseRate",
                new BigDecimal(map.get("auctionPrice").toString()).subtract(new BigDecimal(map.get("lastClosePrice").toString())).multiply(new BigDecimal(100)).
                        divide(new BigDecimal(map.get("lastClosePrice").toString()), 4, BigDecimal.ROUND_HALF_UP));
        //涨幅=(目前价格-昨日价格)/昨日价格
        map.put("newIncreaseRate",
                new BigDecimal(map.get("newPrice").toString()).subtract(new BigDecimal(map.get("lastClosePrice").toString())).multiply(new BigDecimal(100)).
                        divide(new BigDecimal(map.get("lastClosePrice").toString()), 4, BigDecimal.ROUND_HALF_UP));

        //涨速=涨幅-竞价涨幅
        map.put("subIncreaseRate", ((BigDecimal) (map.get("newIncreaseRate"))).subtract((BigDecimal) (map.get("auctionIncreaseRate"))));
        //实时换手率
        map.put("turnOverRate",
                new BigDecimal(map.get("tradeAmount").toString()).multiply(new BigDecimal(100)).
                        divide(new BigDecimal(map.get("circulationMarketValue").toString()), 4, BigDecimal.ROUND_HALF_UP));
    }


    public void updateStockBaseInfo(List<TickInfo> list, String code) {
        CronRefreshConfigBo cronRefreshConfigBo = cronRefreshService.getCronRefreshConfigBo();
        if (list.size() > 0) {
            //获取股票基本信息key
            String nowStockInfoKey = RedisKeyUtils.getNowStockInfo(code);
            String stockDetailStr = (String) redisTemplate.opsForValue().get(nowStockInfoKey);
            Map newStockDetailMap = JsonUtil.readToValue(stockDetailStr, Map.class);
            //更新竞价信息
            upAuctionInfo(list, newStockDetailMap);
            rebuildTickArr(list);
            //更新tick计算信息
            upCalcModuleInfo(list, newStockDetailMap);
            redisTemplate.opsForValue().set(nowStockInfoKey, JsonUtil.toJson(newStockDetailMap), cronRefreshConfigBo.getCodeExistHour(), TimeUnit.HOURS);
        }
    }

    private void rebuildTickArr(List<TickInfo> list) {
        TickInfo beginTickInfo = list.get(0);
        for (TickInfo tickInfo : list) {
            if (tickInfo.getPrice().compareTo(beginTickInfo.getPrice()) == 0) {
                tickInfo.setBuySellFlag(UpDwonEnum.EQUAL.getType());
            }
            if (tickInfo.getPrice().compareTo(beginTickInfo.getPrice()) > 0) {
                tickInfo.setBuySellFlag(UpDwonEnum.UP.getType());
            }
            if (tickInfo.getPrice().compareTo(beginTickInfo.getPrice()) < 0) {
                tickInfo.setBuySellFlag(UpDwonEnum.DOWN.getType());
            }
            beginTickInfo = tickInfo;
        }
    }

    /**
     * 计算模型
     * 1.前五次 量能，必须，价格，
     * 2.前一分钟 修正
     *
     * @param list
     * @param newStockDetailMap
     */
    private void upCalcModuleInfo(List<TickInfo> list, Map newStockDetailMap) {
        BigDecimal lastClosePrice = new BigDecimal(newStockDetailMap.get("lastClosePrice").toString());
        TickInfo auctionInfo = getAuctionInfo(list);
        //获取集合竞价信息
        List<TickInfo> filterInfo = list.stream().filter(item -> item.getTime().compareTo("09:25:59") > 0).collect(Collectors.toList());
        List<TickInfo> beginFiveTickInfo = filterInfo.subList(0, 5);

        List<TickInfo> firstMinuter = list.stream().filter(item -> item.getTime().contains("09:30:")).collect(Collectors.toList());
        List<TickInfo> twoMinuter = list.stream().filter(item -> item.getTime().contains("09:30:") || item.getTime().contains("09:31:")).collect(Collectors.toList());

        //前5向上涨幅
        BigDecimal beginFiveTickVolPriceIncreaseRate = getTickVolPriceIncreaseRate(beginFiveTickInfo, auctionInfo, lastClosePrice);
        newStockDetailMap.put("beginFiveTickVolPriceIncreaseRate", beginFiveTickVolPriceIncreaseRate);

        //第一分钟向上涨幅
        BigDecimal beginFirstMinuterTickVolPriceIncreaseRate = getTickVolPriceIncreaseRate(firstMinuter, auctionInfo, lastClosePrice);
        newStockDetailMap.put("beginFirstMinuterTickVolPriceIncreaseRate", beginFirstMinuterTickVolPriceIncreaseRate);
        //前二分钟向上涨幅
        BigDecimal beginTwoMinuterTickVolPriceIncreaseRate = getTickVolPriceIncreaseRate(twoMinuter, auctionInfo, lastClosePrice);
        newStockDetailMap.put("beginTwoMinuterTickVolPriceIncreaseRate", beginTwoMinuterTickVolPriceIncreaseRate);
        //前五量占比
        BigDecimal proportionStatic = getProportionStatic(beginFiveTickInfo, auctionInfo);
        newStockDetailMap.put("beginFiveTickProportionStatic", proportionStatic);
        //前五单子量的标准差，
        BigDecimal stdStatic = getStdStatic(beginFiveTickInfo);
        newStockDetailMap.put("beginFiveTickStdStatic", stdStatic);
        //前五总交易金额
        BigDecimal allAmountStatic = getAllAmountStatic(beginFiveTickInfo);
        newStockDetailMap.put("beginFiveTickAllAmountStatic", allAmountStatic);
        //向上，向下的量比，
        BigDecimal beginFiveTickVolPrice = getBeginFiveTickVolPrice(beginFiveTickInfo, auctionInfo, lastClosePrice);
        newStockDetailMap.put("beginFiveTickVolPrice", beginFiveTickVolPrice);


        //是否一直向上，占比
        BeginFiveTickScore beginFiveTickScore = getBeginFiveTickUpDownScore(beginFiveTickInfo, auctionInfo, lastClosePrice);
        if (beginFiveTickScore.getFlag()) {
            newStockDetailMap.put("beginFiveTickUpDownScore", null);
            return;
        }
        newStockDetailMap.put("beginFiveTickUpDownScore", beginFiveTickScore.getScore());


    }

    private BigDecimal getProportionStatic(List<TickInfo> beginFiveTickInfo, TickInfo auctionInfo) {
        BigDecimal bigDecimal = beginFiveTickInfo.stream().map(TickInfo::getVol).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, (x1, x2) -> {
            if (x2 == null) {
                return x1;
            }
            if (x1 == null) {
                return x2;
            }
            return x1.add(x2);
        });
        return bigDecimal.divide(auctionInfo.getVol(), 2, BigDecimal.ROUND_HALF_DOWN);
    }

    private BigDecimal getAllAmountStatic(List<TickInfo> beginFiveTickInfo) {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (TickInfo tickInfo : beginFiveTickInfo) {
            BigDecimal value = tickInfo.getVol().multiply(tickInfo.getPrice());
            bigDecimal = bigDecimal.add(value);
        }
        return bigDecimal;

    }


    private BigDecimal getStdStatic(List<TickInfo> beginFiveTickInfo) {
        BigDecimal bigDecimal = beginFiveTickInfo.stream().map(TickInfo::getVol).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, (x1, x2) -> {
            if (x2 == null) {
                return x1;
            }
            if (x1 == null) {
                return x2;
            }
            return x1.add(x2);
        });
        //中位数
        BigDecimal medianNum = bigDecimal.divide(new BigDecimal(5), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal variance = BigDecimal.ZERO;
        for (int i = 0; i < beginFiveTickInfo.size(); i++) {
            BigDecimal b = beginFiveTickInfo.get(i).getVol().subtract(medianNum);
            variance = variance.add(b.multiply(b));
        }
        //方差
        variance = variance.divide(new BigDecimal(beginFiveTickInfo.size()), 4, BigDecimal.ROUND_HALF_UP);
        //标准差
        BigDecimal std = BigRoot.bigRoot(variance, 2, 4, BigDecimal.ROUND_HALF_UP);
        return std.divide(medianNum, 4, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getTickVolPriceIncreaseRate(List<TickInfo> beginFiveTickInfo, TickInfo auctionInfo, BigDecimal lastClosePrice) {
        BigDecimal volCount = beginFiveTickInfo.stream().map(TickInfo::getVol).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, (x1, x2) -> {
            if (x2 == null) {
                return x1;
            }
            if (x1 == null) {
                return x2;
            }
            return x1.add(x2);
        });
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (TickInfo tickInfo : beginFiveTickInfo) {
            BigDecimal value = tickInfo.getVol().multiply(tickInfo.getPrice());
            bigDecimal = bigDecimal.add(value);
        }
        BigDecimal avgPrice = bigDecimal.divide(volCount, 2, BigDecimal.ROUND_CEILING);
        BigDecimal avgIncreaseRate = getIncreaseRate(avgPrice, lastClosePrice);
        BigDecimal auctionIncreaseRate = getIncreaseRate(auctionInfo, lastClosePrice);
        return avgIncreaseRate.subtract(auctionIncreaseRate);
    }


    private BigDecimal getBeginFiveTickVolPrice(List<TickInfo> beginFiveTickInfo, TickInfo auctionInfo, BigDecimal lastClosePrice) {
        BigDecimal allNum = BigDecimal.ZERO;
        //向上，向下的量比，
        for (TickInfo tickInfo : beginFiveTickInfo) {
            BigDecimal currIncreaseRate = getIncreaseRate(tickInfo, lastClosePrice);
            BigDecimal auctionIncreaseRate = getIncreaseRate(auctionInfo, lastClosePrice);
            if (tickInfo.getBuySellFlag().equals(UpDwonEnum.UP.getType())) {
                BigDecimal multiply = currIncreaseRate.subtract(auctionIncreaseRate).multiply(tickInfo.getVol()).multiply(tickInfo.getPrice());
                allNum = allNum.add(multiply);
            }
            if (tickInfo.getBuySellFlag().equals(UpDwonEnum.DOWN.getType())) {
                BigDecimal multiply = currIncreaseRate.subtract(auctionIncreaseRate).multiply(tickInfo.getVol()).multiply(tickInfo.getPrice());
                allNum = allNum.subtract(multiply);
            }
        }
        return allNum;
    }


    private BigDecimal getIncreaseRate(TickInfo currPrice, BigDecimal lastClosePrice) {
        return currPrice.getPrice().subtract(lastClosePrice).divide(lastClosePrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }

    private BigDecimal getIncreaseRate(BigDecimal currPrice, BigDecimal lastClosePrice) {
        return currPrice.subtract(lastClosePrice).divide(lastClosePrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }

    private BeginFiveTickScore getBeginFiveTickUpDownScore(List<TickInfo> beginFiveTickInfo, TickInfo auctionInfo, BigDecimal lastClosePrice) {
        BeginFiveTickScore result = new BeginFiveTickScore();
        List<TickInfo> equalDownArr = beginFiveTickInfo.stream().filter(item -> item.getBuySellFlag().equals(UpDwonEnum.DOWN.getType()) || item.getBuySellFlag().equals(UpDwonEnum.EQUAL.getType())).collect(Collectors.toList());
        List<TickInfo> downArr = beginFiveTickInfo.stream().filter(item -> item.getBuySellFlag().equals(UpDwonEnum.DOWN.getType())).collect(Collectors.toList());
        List<TickInfo> upArr = beginFiveTickInfo.stream().filter(item -> item.getBuySellFlag().equals(UpDwonEnum.UP.getType())).collect(Collectors.toList());
        //砸盘量太多,第一笔朝地上打
        if (downArr.size() >= 3 || equalDownArr.size() >= 4 || beginFiveTickInfo.get(0).getBuySellFlag().equals(UpDwonEnum.DOWN.getType())) {
            result.setFlag(true);
            return result;
        } else {
            if (equalDownArr.size() > 0) {
                //最低点不超过集合竞价一个点
                TickInfo downMinTickInfo = equalDownArr.stream().min(Comparator.comparing(TickInfo::getPrice)).get();
                //竞价涨幅,最低点涨幅
                BigDecimal auctionIncreaseRate = auctionInfo.getPrice().subtract(lastClosePrice).divide(lastClosePrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                BigDecimal downMinIncreaseRate = downMinTickInfo.getPrice().subtract(lastClosePrice).divide(lastClosePrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                if (auctionIncreaseRate.compareTo(downMinIncreaseRate.add(BigDecimal.ONE)) > 0) {
                    result.setFlag(true);
                    return result;
                }
                //砸盘第二低点必须大于集合竞价
                List<TickInfo> newSortArr = equalDownArr.stream().sorted(Comparator.comparing(TickInfo::getPrice)).collect(Collectors.toList());
                if (newSortArr.size() > 1) {
                    if (newSortArr.get(1).getPrice().compareTo(auctionInfo.getPrice()) < 0) {
                        result.setFlag(true);
                        return result;
                    }
                }
            }
            if (downArr.size() == 2) {
                //砸盘的最高价必须大于集合竞价
                TickInfo downMaxTickInfo = downArr.stream().max(Comparator.comparing(TickInfo::getPrice)).get();
                if (auctionInfo.getPrice().compareTo(downMaxTickInfo.getPrice()) > 0) {
                    result.setFlag(true);
                    return result;
                }
            }
            if (upArr.size() >= 4) {
                result.setScore(new BigDecimal(100));
            } else {
                //最后一次平和砸盘的价格大于第一次平和砸盘的价格，
                TickInfo lastTickInfo = equalDownArr.get(equalDownArr.size() - 1);
                TickInfo beginTickInfo = equalDownArr.get(0);
                if (lastTickInfo.getPrice().compareTo(beginTickInfo.getPrice()) >= 0) {
                    result.setScore(new BigDecimal(100));
                } else {
                    result.setScore(new BigDecimal(50));
                }
                //拉升的最大量大于砸盘的最大量
                if (upArr.size() > 0 && downArr.size() > 0) {
                    TickInfo upMaxTickInfo = upArr.stream().max(Comparator.comparing(TickInfo::getVol)).get();
                    TickInfo downMaxTickInfo = downArr.stream().max(Comparator.comparing(TickInfo::getVol)).get();
                    if (upMaxTickInfo.getVol().compareTo(downMaxTickInfo.getVol()) < 0) {
                        result.setFlag(true);
                        return result;
                    }
                }

            }


        }
        return result;
    }


    /**
     * 更新竞价信息
     *
     * @param list
     * @param newStockDetailMap
     */
    private void upAuctionInfo(List<TickInfo> list, Map newStockDetailMap) {
        //获取集合竞价信息
        TickInfo auctionInfoMap = getAuctionInfo(list);
        //集合竞价金额
        BigDecimal auctionTradeAmount = null;
        if (newStockDetailMap.get("auctionTradeAmount") == null) {
            BigDecimal price = auctionInfoMap.getPrice();
            BigDecimal vol = auctionInfoMap.getVol();
            auctionTradeAmount = price.multiply(vol).multiply(new BigDecimal(100));
            newStockDetailMap.put("auctionTradeAmount", auctionTradeAmount);
        } else {
            auctionTradeAmount = new BigDecimal(newStockDetailMap.get("auctionTradeAmount").toString());
        }
        //集合竞价换手率
        if (newStockDetailMap.get("auctionTurnOverRate") == null) {
            Object circulationMarketValue = newStockDetailMap.get("circulationMarketValue");
            BigDecimal auctionTurnOverRate = auctionTradeAmount.divide(new BigDecimal(circulationMarketValue.toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            newStockDetailMap.put("auctionTurnOverRate", auctionTurnOverRate);
        }
    }

    private TickInfo getAuctionInfo(List<TickInfo> list) {
        List<TickInfo> auctionInfo = list.stream().filter(item -> item.getTime().contains("09:25:")).collect(Collectors.toList());
        TickInfo auctionInfoMap = auctionInfo.get(0);
        return auctionInfoMap;
    }

}
