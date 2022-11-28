package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.stock.model.entity.StockBase;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.common.util.TongHuaShunUtil;
import com.coatardbul.stock.mapper.StockBaseMapper;
import com.coatardbul.stock.model.bo.StrategyBO;
import com.coatardbul.stock.model.dto.StockBaseDTO;
import com.coatardbul.stock.model.dto.StockStrategyQueryDTO;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/9/8
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockBaseService {

    @Autowired
    private StockBaseMapper stockBaseMapper;
    @Autowired
    private StockStrategyService stockStrategyService;

    public void addProcess() {
        for (int i = 1; i < 55; i++) {
            StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
            dto.setRiverStockTemplateId("1567739221362475008");
            dto.setPageSize(100);
            dto.setPage(i);
            dto.setDateStr("2022-11-03");
            StrategyBO strategy = null;
            try {
                strategy = stockStrategyService.strategy(dto);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (strategy != null && strategy.getTotalNum() > 0) {
                JSONArray data = strategy.getData();
                for (int j = 0; j < data.size(); j++) {
                    JSONObject jsonObject = data.getJSONObject(j);
                    String code = jsonObject.getString("code");
                    String name = jsonObject.getString("股票简称");
                    StockBase stockBase = new StockBase();
                    stockBase.setCode(code);
                    stockBase.setName(name);
                    stockBase.setNameAbbr(getNameAbbr(name));
                    try {
                        stockBaseMapper.insert(stockBase);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }

    }


    /**
     * 获取名称缩写
     * @param name
     * @return
     */
    private String getNameAbbr(String name) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            String trans = null;
            try {
                trans = TongHuaShunUtil.getTrans(name.substring(i, i + 1));
            } catch (Exception e) {
                return "";
            }
            sb.append(trans.trim().substring(0, 1));
        }
        String replace = sb.toString().replace("ā", "a");

        return replace;
    }


    public Object findAll(StockBaseDTO dto) {
        PageHelper.startPage(1, 200);
        if (StringUtils.isNotBlank(dto.getParamName())) {
            return stockBaseMapper.selectByCodeLike(dto.getParamName());
        } else {
            return stockBaseMapper.selectByAll(null);
        }

    }



}
