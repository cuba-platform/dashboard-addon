-- begin DASHBOARD_WIDGET_TEMPLATE
create table DASHBOARD_WIDGET_TEMPLATE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    IS_AVAILABLE_FOR_ALL_USERS tinyint,
    WIDGET_MODEL varchar(max) not null,
    GROUP_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
)^
-- end DASHBOARD_WIDGET_TEMPLATE

-- begin DASHBOARD_DASHBOARD_GROUP
create table DASHBOARD_DASHBOARD_GROUP (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key nonclustered (ID)
)^
-- end DASHBOARD_DASHBOARD_GROUP
-- begin DASHBOARD_PERSISTENT_DASHBOARD
create table DASHBOARD_PERSISTENT_DASHBOARD (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    DASHBOARD_MODEL varchar(max) not null,
    NAME varchar(255) not null,
    REFERENCE_NAME varchar(255) not null,
    GROUP_ID uniqueidentifier,
    IS_AVAILABLE_FOR_ALL_USERS tinyint,
    --
    primary key nonclustered (ID)
)^
-- end DASHBOARD_PERSISTENT_DASHBOARD
-- begin DASHBOARD_TEMPLATE_GROUP
create table DASHBOARD_TEMPLATE_GROUP (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key nonclustered (ID)
)^
-- end DASHBOARD_TEMPLATE_GROUP
