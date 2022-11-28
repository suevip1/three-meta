package com.coatardbul.river.model.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author: suxiaolei
 * @date: 2019/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHeadDto {
    /**
     * 请求系统ID
     */
    @NotBlank(message = "请求系统ID不能为空")
    private String reqSysCd;
    /**
     * 请求系统日期
     */
    @NotBlank(message = "请求系统日期不能为空")
    private String reqSysDt;
    /**
     * 请求系统时间
     */
    @NotBlank(message = "请求系统时间不能为空")
    private String reqSysTm;
    /**
     * 银行编码
     */
    private String reqInterBank;
    /**
     * 请求方流水号
     */
    @NotBlank(message = "请求方流水号不能为空")
    private String reqSeqNum;
}
