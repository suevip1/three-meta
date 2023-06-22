package com.coatardbul.river.model.entity;

import lombok.Data;

@Data
public class DictInfo {
    private String id;

    /**
     * 业务类型
     */
    private String busiType;

    private String signKey;

    private String signValue;

    private String remark;
}