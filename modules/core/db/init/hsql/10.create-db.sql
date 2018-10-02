-- begin DASHBOARD_PERSISTENT_DASHBOARD
create table DASHBOARD_PERSISTENT_DASHBOARD (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DASHBOARD_MODEL longvarchar not null,
    NAME varchar(255) not null,
    REFERENCE_NAME varchar(255) not null,
    GROUP_ID varchar(36),
    IS_AVAILABLE_FOR_ALL_USERS boolean,
    --
    primary key (ID)
)^
-- end DASHBOARD_PERSISTENT_DASHBOARD
-- begin DASHBOARD_WIDGET_TEMPLATE
create table DASHBOARD_WIDGET_TEMPLATE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    IS_AVAILABLE_FOR_ALL_USERS boolean,
    WIDGET_MODEL longvarchar not null,
    GROUP_ID varchar(36),
    --
    primary key (ID)
)^
-- end DASHBOARD_WIDGET_TEMPLATE
-- begin DASHBOARD_DASHBOARD_GROUP
create table DASHBOARD_DASHBOARD_GROUP (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end DASHBOARD_DASHBOARD_GROUP
-- begin DASHBOARD_WIDGET_TEMPLATE_GROUP
create table DASHBOARD_WIDGET_TEMPLATE_GROUP (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end DASHBOARD_WIDGET_TEMPLATE_GROUP
