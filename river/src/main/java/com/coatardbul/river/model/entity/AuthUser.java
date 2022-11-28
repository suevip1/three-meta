package com.coatardbul.river.model.entity;

import java.util.Date;
import lombok.Data;

/**
    * 用户表
    */
@Data
public class AuthUser {
    private String id;

    /**
    * 用户名称
    */
    private String username;

    /**
    * 用户密码
    */
    private String password;

    /**
    * 昵称
    */
    private String nickname;

    /**
    * 状态
    */
    private Byte status;

    /**
    * 性别
    */
    private Byte sex;

    /**
    * 出生年月
    */
    private Date birthday;

    /**
    * 联系方式
    */
    private String phone;

    /**
    * 邮箱地址
    */
    private String email;

    /**
    * 备注
    */
    private String remark;

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