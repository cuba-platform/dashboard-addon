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
    --
    primary key (ID)
)^
-- end AMXD_DASHBOARD_WIDGET
