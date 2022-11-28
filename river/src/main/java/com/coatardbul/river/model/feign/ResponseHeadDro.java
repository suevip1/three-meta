package com.coatardbul.river.model.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: suxiaolei
 * @date: 2019/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHeadDro {
    /**
     * 平台应答码
     */
    private String resCd;
    /**
     * 平台应答信息
     */
    private String resMsg;
    /**
     * 平台应答日期
     */
    private String resDt;
    /**
     * 平台应答时间
     */
    private String resTm;
    /**
     * 平台应答流水
     */
    private String resSeqNum;
    /**
     * 请求方流水号
     */

    private String reqSeqNum;

}
