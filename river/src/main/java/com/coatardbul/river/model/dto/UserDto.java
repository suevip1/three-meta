package com.coatardbul.river.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    /**
     * 主键id
     */
    private String userId;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;
}
