package com.coatardbul.stock.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockIndustryAnalyseDTO {
    /**
    * 日期
    */
    private String date;

    /**
    * 竞价信息，数组，字符串数组json
    */
    private List<String> callAuction;

    /**
    * 大于比例标准
    */
    private List<String> greateThanLine;

    /**
    * 大的拉升
    */
    private List<String> bigIncrease;

    /**
    * 明日竞价买
    */
    private List<String> nextCallAuctionBuy;

    /**
    * 小拉升
    */
    private List<String> smallIncrease;

    /**
    * 涨停信息
    */
    private List<String> uplimitInfo;

    /**
    * 备注
    */
    private String remark;
}