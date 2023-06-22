package com.coatardbul.river.model.entity;

import java.util.Date;
import lombok.Data;

@Data
public class LoginDetail {
    private String id;

    /**
    * ip
    */
    private String ip;

    /**
    * 用户代理
    */
    private String userAgent;

    /**
    * 代理端口
    */
    private String clientPort;

    /**
    * 请求url
    */
    private String requestUri;

    /**
    * 创建日期
    */
    private Date gmtCreate;
}