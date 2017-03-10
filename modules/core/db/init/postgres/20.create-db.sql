-- begin AMXD_DASHBOARD
alter table AMXD_DASHBOARD add constraint FK_AMXD_DASHBOARD_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_AMXD_DASHBOARD_USER on AMXD_DASHBOARD (USER_ID)^
-- end AMXD_DASHBOARD
