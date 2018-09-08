update DASHBOARD_DASHBOARD_WIDGET set WIDGET_ID = '' where WIDGET_ID is null ;
alter table DASHBOARD_DASHBOARD_WIDGET alter column WIDGET_ID set not null ;
update DASHBOARD_DASHBOARD_WIDGET set CAPTION = '' where CAPTION is null ;
alter table DASHBOARD_DASHBOARD_WIDGET alter column CAPTION set not null ;
update DASHBOARD_DASHBOARD_WIDGET set FRAME_ID = '' where FRAME_ID is null ;
alter table DASHBOARD_DASHBOARD_WIDGET alter column FRAME_ID set not null ;
