package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.stock.model.entity.StockTradeAssetPosition;
import com.coatardbul.stock.feign.BaseServerFeign;
import com.coatardbul.stock.mapper.StockTradeAssetPositionMapper;
import com.coatardbul.stock.service.romote.RiverRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/7/19
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockTradeAssetPositionService {
    @Autowired
    private StockTradeAssetPositionMapper stockTradeAssetPositionMapper;

    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    RiverRemoteService riverRemoteService;

    public void add(StockTradeAssetPosition dto) {
        dto.setId(baseServerFeign.getSnowflakeId());

        StockTradeAssetPosition stockTradeAssetPosition = stockTradeAssetPositionMapper.selectAllByCode(dto.getCode());
        if (stockTradeAssetPosition == null) {
            stockTradeAssetPositionMapper.insert(dto);
        }
    }

    public void modify(StockTradeAssetPosition dto) {
        stockTradeAssetPositionMapper.updateByPrimaryKeySelective(dto);

    }

    public void delete(StockTradeAssetPosition dto) {
        if (StringUtils.isNotBlank(dto.getId())) {
            stockTradeAssetPositionMapper.deleteByPrimaryKey(dto.getId());
        } else if (StringUtils.isNotBlank(dto.getCode())) {
            stockTradeAssetPositionMapper.deleteByCode(dto.getCode());
        }
    }


    public Object findAll() {
        return stockTradeAssetPositionMapper.selectByAll(null);
    }


    /**
     * 添加持仓信息
     *
     * @param buyNum
     * @param price
     * @param code
     * @param name
     * @param date
     */
    public void addAssetInfo(BigDecimal buyNum, BigDecimal price, String code, String name, String date) {
        StockTradeAssetPosition stockTradeAssetPosition = stockTradeAssetPositionMapper.selectAllByCode(code);
        //买入金额
        BigDecimal bugAmount = buyNum.multiply(price);
        if (stockTradeAssetPosition == null) {
            StockTradeAssetPosition addInfo = new StockTradeAssetPosition();
            addInfo.setId(baseServerFeign.getSnowflakeId());
            addInfo.setName(name);
            addInfo.setCode(code);
            addInfo.setCurrPosition(bugAmount.toString());
            addInfo.setDate(date);
            addInfo.setCurrNum(buyNum.toString());
            addInfo.setType(2);
            stockTradeAssetPositionMapper.insert(addInfo);
        } else {
            //目前持仓
            if (StringUtils.isNotBlank(stockTradeAssetPosition.getCurrPosition())) {
                BigDecimal allCurrPosition = new BigDecimal(stockTradeAssetPosition.getCurrPosition()).add(bugAmount);
                stockTradeAssetPosition.setCurrPosition(allCurrPosition.toString());
            } else {
                stockTradeAssetPosition.setCurrPosition(bugAmount.toString());
            }
            //目前数量
            if (StringUtils.isNotBlank(stockTradeAssetPosition.getCurrNum())) {
                BigDecimal bigDecimal = new BigDecimal(stockTradeAssetPosition.getCurrNum()).add(buyNum);
                stockTradeAssetPosition.setCurrNum(bigDecimal.toString());
            } else {
                stockTradeAssetPosition.setCurrNum(buyNum.toString());
            }
            stockTradeAssetPositionMapper.updateByPrimaryKeySelective(stockTradeAssetPosition);
        }

    }
}
