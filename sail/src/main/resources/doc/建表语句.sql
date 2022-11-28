
DROP TABLE IF EXISTS `User_info` ;
CREATE TABLE `User_info`   (
`user_id`    VARCHAR(22)   not  NULL   COMMENT   '主键id',
`account`    VARCHAR(10)   DEFAULT  NULL   COMMENT   '账号',
`passWord`    VARCHAR(200)   DEFAULT  NULL   COMMENT   '密码',
`status`    CHAR(1)   DEFAULT  NULL   COMMENT   '账户状态1正常，2冻结',
`name`    VARCHAR(100)   DEFAULT  NULL   COMMENT   '姓名',
`birthDay`    VARCHAR(6)   DEFAULT  NULL   COMMENT   '生日',
`email`    VARCHAR(100)   DEFAULT  NULL   COMMENT   '邮箱地址',
`mobileNum`    VARCHAR(11)   DEFAULT  NULL   COMMENT   '手机号码',
`flag`    cHAR(1)   DEFAULT  NULL   COMMENT   '是否是管理员 0普通用户,1管理员',
`is_First_Login`    CHAR(1)   DEFAULT  NULL   COMMENT   '是否首次登陆：1是，0否',
`pwd_Update_TIMESTAMP`   TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT NULL    COMMENT   '密码修改时间',
`user_create_TIMESTAMP`    TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT NULL   COMMENT   '用户创建时间',
`password_History`    VARCHAR(500)   DEFAULT  NULL   COMMENT   '密码历史',
PRIMARY KEY  (`user_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='用户信息表';