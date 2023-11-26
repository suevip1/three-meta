package com.coatardbul.baseCommon.model.entity;

import lombok.Data;

@Data
public class MsgAckConfig {
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 名称
     */
    private String name;

    /**
     * 配置的名称
     */
    private String msgName;

    /**
     * 配置类型：dingDing,weiXin,mail
     */
    private String msgType;

    /**
     * 参数1
     */
    private String param1;

    private String param2;

    private String param3;

    private String param4;

    private String param5;

    private String param6;

    /**
     * 结实说明
     */
    private String remark;
}