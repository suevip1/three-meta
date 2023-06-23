package com.coatardbul.stock.model.dto;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/6/3
 *
 * @author Su Xiaolei
 */
@Data
public class StockTradeLoginDTO {

    /**
     * 用户id
     */
    private String userId;

    private String account;

    private String password;

    private String randNumber;

    private String identifyCode;

    private String duration;

    private String authCode;

    private String type;


}
