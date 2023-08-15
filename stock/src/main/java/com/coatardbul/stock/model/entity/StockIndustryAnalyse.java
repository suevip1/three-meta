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
    private String callAuctionGreate5Info;

    /**
     * 竞价涨幅大于2
     */
    private String callAuctionGreate2Info;

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
     * 涨幅大于5
     */
    private String increaseGreate5Info;

    /**
     * 备注
     */
    private String remark;
}