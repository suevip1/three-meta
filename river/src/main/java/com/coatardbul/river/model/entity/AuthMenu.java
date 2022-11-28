package com.coatardbul.river.model.entity;

import java.util.Date;
import lombok.Data;

/**
 * 菜单资源表
 */
@Data
public class AuthMenu {
    /**
     * 主键
     */
    private String id;

    /**
     * 父菜单ID
     */
    private String parentMenuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 路径或功能编码
     */
    private String routerUrl;

    /**
     * 顺序
     */
    private Integer sequent;

    /**
     * 菜单图标Base64编码
     */
    private String icon;

    /**
     * 状态 1-有效 0-无效
     */
    private Integer status;

    /**
     * 创建日期
     */
    private Date gmtCreate;

    /**
     * 修改日期
     */
    private Date gmtModified;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;
}