alter table DASHBOARD_DASHBOARD_WIDGET drop column WIDGET_VIEW_TYPE cascade ;
alter table DASHBOARD_DASHBOARD_WIDGET add column WIDGET_VIEW_TYPE varchar(25) ;
