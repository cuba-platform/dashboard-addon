-- begin DASHBOARD_WIDGET_TEMPLATE
create table DASHBOARD_WIDGET_TEMPLATE (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    NAME varchar2(255 char) not null,
    IS_AVAILABLE_FOR_ALL_USERS char(1),
    WIDGET_MODEL clob not null,
    GROUP_ID varchar2(32),
    --
    primary key (ID)
)^
-- end DASHBOARD_WIDGET_TEMPLATE
-- begin DASHBOARD_WIDGET_TEMPLATE_GROUP
create table DASHBOARD_WIDGET_TEMPLATE_GROUP (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    NAME varchar2(255 char) not null,
    --
    primary key (ID)
)^
-- end DASHBOARD_WIDGET_TEMPLATE_GROUP
-- begin DASHBOARD_DASHBOARD_GROUP
create table DASHBOARD_DASHBOARD_GROUP (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    NAME varchar2(255 char) not null,
    --
    primary key (ID)
)^
-- end DASHBOARD_DASHBOARD_GROUP
-- begin DASHBOARD_PERSISTENT_DASHBOARD
create table DASHBOARD_PERSISTENT_DASHBOARD (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    DASHBOARD_MODEL clob not null,
    NAME varchar2(255 char) not null,
    REFERENCE_NAME varchar2(255 char) not null,
    GROUP_ID varchar2(32),
    IS_AVAILABLE_FOR_ALL_USERS char(1),
    --
    primary key (ID)
)^
-- end DASHBOARD_PERSISTENT_DASHBOARD
