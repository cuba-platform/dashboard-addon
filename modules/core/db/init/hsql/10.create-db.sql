-- begin AMXD_DASHBOARD
create table AMXD_DASHBOARD (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID varchar(36),
    TITLE varchar(255),
    MODEL longvarchar,
    --
    primary key (ID)
)^
-- end AMXD_DASHBOARD
