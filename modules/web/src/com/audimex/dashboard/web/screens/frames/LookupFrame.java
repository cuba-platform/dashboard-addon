/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.screens.frames;

import com.audimex.dashboard.entity.WidgetParameter;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.TokenList;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author bochkarev
 * @version $Id$
 */
public class LookupFrame extends AbstractFrame {
    public static final String WIDGET_PARAMETER = "WIDGET_PARAMETER";
    public static final String SCREEN_ID = "amxd$Lookup.frame";

    @WindowParam(name = WIDGET_PARAMETER)
    protected WidgetParameter widgetParameter;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        @SuppressWarnings("unchecked")
        Datasource<WidgetParameter> masterDs = new DsBuilder(getDsContext())
                .setJavaClass(WidgetParameter.class)
                .setViewName("master-widgetParameter-view")
                .setId("masterDs")
                .setAllowCommit(false)
                .setRefreshMode(CollectionDatasource.RefreshMode.NEVER)
                .buildDatasource();

        masterDs.setItem(widgetParameter);

        CollectionDatasource selectedDs = new DsBuilder(getDsContext())
                .setJavaClass(WidgetParameter.class)
                .setViewName("slave-widgetParameter-view")
                .setId("selectedDs")
                .setAllowCommit(false)
                .setRefreshMode(CollectionDatasource.RefreshMode.NEVER)
                .buildCollectionDatasource();

        if (widgetParameter.getListParameters() != null) {
            widgetParameter.getListParameters().forEach(selectedDs::addItem);
        }

        MetaClass metaClass = metadata.getSession().getClass(
                widgetParameter.getReferenceToEntity().getMetaClassName()
        );

        CollectionDatasource allItemsDs = new DsBuilder(getDsContext())
                .setJavaClass(metaClass.getJavaClass())
                .setViewName(widgetParameter.getReferenceToEntity().getViewName())
                .setId("allItemsDs")
                .setAllowCommit(false)
                .buildCollectionDatasource();

        allItemsDs.refresh();

        TokenList tokenList = componentsFactory.createComponent(TokenList.class);
        tokenList.setWidth("100%");
        tokenList.setDatasource(selectedDs);
        tokenList.setOptionsDatasource(allItemsDs);

        add(tokenList);
    }
}
