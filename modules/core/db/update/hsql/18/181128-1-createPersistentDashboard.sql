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
);