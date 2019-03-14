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
);