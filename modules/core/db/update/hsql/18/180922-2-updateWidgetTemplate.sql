alter table DASHBOARD_WIDGET_TEMPLATE add column NAME varchar(255) ^
update DASHBOARD_WIDGET_TEMPLATE set NAME = '' where NAME is null ;
alter table DASHBOARD_WIDGET_TEMPLATE alter column NAME set not null ;
alter table DASHBOARD_WIDGET_TEMPLATE add column DESCRIPTION varchar(4000) ;
