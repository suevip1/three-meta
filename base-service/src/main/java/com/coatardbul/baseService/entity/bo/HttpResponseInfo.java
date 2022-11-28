package com.coatardbul.baseService.entity.bo;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/26
 *
 * @author Su Xiaolei
 */
@Data
public class HttpResponseInfo {


    /**
     * 返回的状态码
     */
    private int httpStatus;

    /**
     * 返回的信息
     */
    private String responseStr;
}
