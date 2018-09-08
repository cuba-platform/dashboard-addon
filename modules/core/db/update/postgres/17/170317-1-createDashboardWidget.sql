create table DASHBOARD_DASHBOARD_WIDGET (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    WIDGET_ID varchar(255),
    CAPTION varchar(255),
    ICON varchar(255),
    DESCRIPTION varchar(255),
    FRAME_ID varchar(255),
    --
    primary key (ID)
);
