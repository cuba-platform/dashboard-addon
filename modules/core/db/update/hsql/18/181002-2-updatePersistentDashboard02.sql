alter table DASHBOARD_PERSISTENT_DASHBOARD add column NAME varchar(255) ^
update DASHBOARD_PERSISTENT_DASHBOARD set NAME = '' where NAME is null ;
alter table DASHBOARD_PERSISTENT_DASHBOARD alter column NAME set not null ;
