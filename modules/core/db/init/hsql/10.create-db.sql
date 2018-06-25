-- begin AMXD_PERSISTENT_DASHBOARD
create table AMXD_PERSISTENT_DASHBOARD (
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
    REFERENCE_NAME varchar(255) not null,
    DASHBOARD_GROUP_ID varchar(36),
    --
    primary key (ID)
)^
-- end AMXD_PERSISTENT_DASHBOARD
-- begin AMXD_WIDGET_TEMPLATE
create table AMXD_WIDGET_TEMPLATE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    WIDGET_MODEL longvarchar not null,
    WIDGET_TEMPLATE_GROUP_ID varchar(36),
    --
    primary key (ID)
)^
-- end AMXD_WIDGET_TEMPLATE
-- begin AMXD_DASHBOARD_GROUP
create table AMXD_DASHBOARD_GROUP (
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
-- end AMXD_DASHBOARD_GROUP
-- begin AMXD_WIDGET_TEMPLATE_GROUP
create table AMXD_WIDGET_TEMPLATE_GROUP (
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
-- end AMXD_WIDGET_TEMPLATE_GROUP
