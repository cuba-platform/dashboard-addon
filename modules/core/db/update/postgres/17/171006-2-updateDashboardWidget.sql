alter table AMXD_DASHBOARD_WIDGET drop column WIDGET_VIEW_TYPE cascade ;
alter table AMXD_DASHBOARD_WIDGET add column WIDGET_VIEW_TYPE varchar(25) ;
