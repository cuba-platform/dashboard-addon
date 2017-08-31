/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.screens.frames;

import com.audimex.dashboard.entity.WidgetParameter;
import com.audimex.dashboard.web.tools.ParameterTools;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.TokenList;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
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

    protected CollectionDatasource selectedDs;

    @Inject
    protected ParameterTools parameterTools;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        MetaClass metaClass = metadata.getSession().getClass(
                widgetParameter.getReferenceToEntity().getMetaClassName()
        );

        selectedDs = new DsBuilder(getDsContext())
                .setJavaClass(metaClass.getJavaClass())
                .setViewName(widgetParameter.getReferenceToEntity().getViewName())
                .setId("selectedDs")
                .setAllowCommit(false)
                .setRefreshMode(CollectionDatasource.RefreshMode.NEVER)
                .buildCollectionDatasource();

        if (widgetParameter.getListParameters() != null) {
            @SuppressWarnings("unchecked")
            List<Entity> o = (List<Entity>) parameterTools
                    .getWidgetLinkParameterValue(widgetParameter);
            o.forEach(selectedDs::includeItem);
        }

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

    @SuppressWarnings("unchecked")
    public void setValueChangeListener(CollectionDatasource.CollectionChangeListener collectionChangeListener) {
        selectedDs.addCollectionChangeListener(collectionChangeListener);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getWidgetParameterValues() {
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(selectedDs.getItems());
        return objectList;
    }
}
