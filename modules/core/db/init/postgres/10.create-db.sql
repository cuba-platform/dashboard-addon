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
