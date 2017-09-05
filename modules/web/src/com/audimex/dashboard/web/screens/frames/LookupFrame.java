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
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.TokenList;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bochkarev
 * @version $Id$
 */
public class LookupFrame extends AbstractFrame implements Component.HasValue {
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

        if (widgetParameter.getListParameters() != null && widgetParameter.getListParameters().size() != 0) {
            @SuppressWarnings("unchecked")
            List<Entity> o = (List<Entity>) parameterTools
                    .getWidgetLinkParameterValue(widgetParameter);
            o.forEach(selectedDs::addItem);
        }

        CollectionDatasource allItemsDs = new DsBuilder(getDsContext())
                .setJavaClass(metaClass.getJavaClass())
                .setViewName(widgetParameter.getReferenceToEntity().getViewName())
                .setId("allItemsDs")
                .setAllowCommit(false)
                .buildCollectionDatasource();

        TokenList tokenList = componentsFactory.createComponent(TokenList.class);
        tokenList.setWidth("100%");
        tokenList.setDatasource(selectedDs);
        tokenList.setOptionsDatasource(allItemsDs);

        add(tokenList);

        allItemsDs.refresh();
        selectedDs.refresh();
    }

    @SuppressWarnings("unchecked")
    public void setValueChangeListener(CollectionDatasource.CollectionChangeListener collectionChangeListener) {
        selectedDs.addCollectionChangeListener(collectionChangeListener);
    }

    @Override @SuppressWarnings("unchecked")
    public List<Object>  getValue() {
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(selectedDs.getItems());
        return objectList;
    }

    @Override @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        if (value == null) {
            selectedDs.clear();
        } else if (value instanceof List) {
            ((List) value).forEach(v ->
                    selectedDs.includeItem((Entity) v)
            );
        }
    }

    @Override
    public void addListener(ValueListener listener) {

    }

    @Override
    public void removeListener(ValueListener listener) {

    }

    @Override
    public void addValueChangeListener(ValueChangeListener listener) {

    }

    @Override
    public void removeValueChangeListener(ValueChangeListener listener) {

    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {

    }
}
