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
public class ResponseDto<T> {
    /**
     * 响应头
     */
    private ResponseHeadDro head;
    /**
     * 响应体
     */
    private T body;
}
