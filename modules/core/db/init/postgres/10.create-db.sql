-- begin AMXD_DASHBOARD_GROUP
create table AMXD_DASHBOARD_GROUP (
    ID uuid,
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
-- begin AMXD_PERSISTENT_DASHBOARD
create table AMXD_PERSISTENT_DASHBOARD (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DASHBOARD_MODEL text not null,
    REFERENCE_NAME varchar(255) not null,
    DASHBOARD_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end AMXD_PERSISTENT_DASHBOARD
-- begin AMXD_WIDGET_TEMPLATE
create table AMXD_WIDGET_TEMPLATE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    WIDGET_MODEL text not null,
    --
    primary key (ID)
)^
-- end AMXD_WIDGET_TEMPLATE
