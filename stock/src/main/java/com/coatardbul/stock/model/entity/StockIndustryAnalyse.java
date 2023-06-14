package com.coatardbul.stock.model.entity;

import lombok.Data;

@Data
public class StockIndustryAnalyse {
    /**
    * 日期
    */
    private String date;

    /**
    * 竞价信息，数组，字符串数组json
    */
    private String callAuction;

    /**
    * 大于比例标准
    */
    private String greateThanLine;

    /**
    * 大的拉升
    */
    private String bigIncrease;

    /**
    * 明日竞价买
    */
    private String nextCallAuctionBuy;

    /**
    * 小拉升
    */
    private String smallIncrease;

    /**
    * 涨停信息
    */
    private String uplimitInfo;

    /**
    * 备注
    */
    private String remark;
}