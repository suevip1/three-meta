create table if not exists auth_calendar
(
    id varchar(32) not null comment '主键'
        primary key,
    date varchar(10) null comment 'YYYY-MM-DD',
    week int null comment '星期',
    date_prop int null comment '日期属性 1 工作日 2 休息日 3节假日  4 周六周日加班',
    holiday_name varchar(255) null comment '节日名称',
    lunar varchar(255) null comment '阴历日期中文',
    status tinyint default 0 not null comment '状态 1-有效 0-无效',
    gmt_create datetime null comment '创建日期',
    gmt_modified datetime null comment '修改日期',
    creator varchar(32) null comment '创建人',
    modifier varchar(32) null comment '修改人',
    constraint PK_AUTH_CALENDAR_DATE
        unique (date)
)
    comment '菜单资源表';

create table if not exists auth_menu
(
    id varchar(32) not null comment '主键'
        primary key,
    parent_menu_id varchar(32) not null comment '父菜单ID',
    menu_name varchar(300) null comment '菜单名称',
    router_url varchar(64) null comment '路径或功能编码',
    sequent tinyint null comment '顺序',
    icon text null comment '菜单图标Base64编码',
    status tinyint default 0 not null comment '状态 1-有效 0-无效',
    gmt_create datetime null comment '创建日期',
    gmt_modified datetime null comment '修改日期',
    creator varchar(32) null comment '创建人',
    modifier varchar(32) null comment '修改人'
)
    comment '菜单资源表';

create table if not exists auth_role
(
    id varchar(32) not null
        primary key,
    name varchar(64) null,
    status tinyint default 0 not null comment '状态 1-有效 0-无效',
    `describe` varchar(1024) null comment '描述',
    gmt_create datetime null comment '创建日期',
    gmt_modified datetime null comment '修改日期',
    creator varchar(45) null comment '创建人',
    modifier varchar(45) null comment '修改人'
);

create table if not exists auth_role_menu
(
    id varchar(32) not null
        primary key,
    menu_id varchar(32) null,
    role_id varchar(45) null,
    status tinyint default 0 not null,
    gmt_create datetime null comment '创建日期',
    gmt_modified datetime null comment '修改日期',
    creator varchar(32) null comment '创建人',
    modifier varchar(32) null comment '修改人'
)
    comment '角色资源关联表';

create table if not exists auth_user
(
    id varchar(32) not null
        primary key,
    username varchar(64) null comment '用户名称',
    password varchar(128) null comment '用户密码',
    nickname varchar(64) null comment '昵称',
    status tinyint null comment '状态',
    sex tinyint null comment '性别',
    birthday datetime null comment '出生年月',
    phone varchar(30) null comment '联系方式',
    email varchar(256) null comment '邮箱地址',
    remark varchar(255) null comment '备注',
    gmt_create datetime null comment '创建日期',
    gmt_modified datetime null comment '修改日期',
    creator varchar(32) null comment '创建人',
    modifier varchar(32) null comment '修改人'
)
    comment '用户表';

create table if not exists auth_user_role
(
    id varchar(32) not null
        primary key,
    role_id varchar(32) null comment '角色ID',
    user_id varchar(32) null comment '用户ID',
    status tinyint default 0 not null comment '状态',
    gmt_create datetime null comment '创建日期',
    gmt_modified datetime null comment '修改日期',
    creator varchar(32) null comment '创建人',
    modifier varchar(32) null comment '修改人'
)
    comment '用户与角色关联表';

create table if not exists stock_query_template
(
    id varchar(32) not null
        primary key,
    name varchar(256) null comment '模板名称',
    script_str varchar(2048) null comment '模板表达式',
    example_str varchar(2046) null comment '表达式样例',
    hot_value int null comment '热力值',
    remark varchar(1024) null comment '备注说明',
    today_str varchar(64) null comment '指定当日的字符串，和模板表达式匹配',
    template_sign varchar(256) null comment '英文标识，用来识别模板，不用id识别，不能重复',
    theme_Str varchar(1024) null comment '指定题材信息，和模板匹配',
    constraint stock_query_template_template_sign_uindex
        unique (template_sign)
)
    comment '股票问句模板';

create table if not exists stock_time_interval
(
    id varchar(32) not null
        primary key,
    time_str varchar(32) null,
    interval_type int null comment '间隔时间，单位分钟'
)
    comment '时间间隔表';

