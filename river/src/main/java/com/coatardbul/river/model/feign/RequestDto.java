package com.coatardbul.river.model.feign;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author: suxiaolei
 * @date: 2019/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto<T> {

    /**
     * 请求头
     */
    @NotNull(message = "请求头不能为空")
    private RequestHeadDto head;

    /**
     * 请求体
     */
    @NotNull(message = "请求体不能为空")
    private T body;
    /**
     * 数字签名
     */
    private String sign;
}
