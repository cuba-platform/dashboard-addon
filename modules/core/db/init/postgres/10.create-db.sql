-- begin AMXD_DASHBOARD
create table AMXD_DASHBOARD (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID uuid,
    TITLE varchar(255),
    MODEL text,
    ENTITY_TYPE varchar(255),
    --
    primary key (ID)
)^
-- end AMXD_DASHBOARD
-- begin AMXD_DASHBOARD_WIDGET
create table AMXD_DASHBOARD_WIDGET (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    WIDGET_ID varchar(255) not null,
    CAPTION varchar(255) not null,
    ICON varchar(255),
    DESCRIPTION varchar(255),
    FRAME_ID varchar(255) not null,
    WIDGET_VIEW_TYPE varchar(25),
    ENTITY_TYPE varchar(255),
    REPORT_ID uuid,
    IS_TEMPLATE boolean,
    --
    primary key (ID)
)^
-- end AMXD_DASHBOARD_WIDGET
-- begin AMXD_DASHBOARD_WIDGET_LINK
create table AMXD_DASHBOARD_WIDGET_LINK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DASHBOARD_ID uuid,
    FILTER_ varchar(255),
    DASHBOARD_WIDGET_ID uuid,
    --
    primary key (ID)
)^
-- end AMXD_DASHBOARD_WIDGET_LINK
-- begin AMXD_WIDGET_PARAMETER
create table AMXD_WIDGET_PARAMETER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REFERENCE_TO_ENTITY_ENTITY_ID uuid,
    REFERENCE_TO_ENTITY_META_CLASS_NAME varchar(255),
    REFERENCE_TO_ENTITY_VIEW_NAME varchar(255),
    --
    NAME varchar(255),
    ALIAS varchar(255),
    MAPPED_ALIAS varchar(255),
    PARAMETER_TYPE integer,
    DASHBOARD_WIDGET_ID uuid,
    DASHBOARD_WIDGET_LINK_ID uuid,
    INTEGER_VALUE integer,
    STRING_VALUE varchar(255),
    DECIMAL_VALUE decimal(19, 2),
    DATE_VALUE date,
    BOOL_VALUE boolean,
    LONG_VALUE bigint,
    DOUBLE_VALUE double precision,
    MASTER_PARAMETER_ID uuid,
    INPUT_TYPE integer,
    --
    primary key (ID)
)^
-- end AMXD_WIDGET_PARAMETER
