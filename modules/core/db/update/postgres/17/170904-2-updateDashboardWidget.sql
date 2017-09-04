alter table AMXD_DASHBOARD_WIDGET add column REPORT_ID uuid ;
alter table AMXD_DASHBOARD_WIDGET drop column REPORT cascade ;
