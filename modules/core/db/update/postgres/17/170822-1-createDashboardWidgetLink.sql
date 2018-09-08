create table DASHBOARD_DASHBOARD_WIDGET_LINK (
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
);
