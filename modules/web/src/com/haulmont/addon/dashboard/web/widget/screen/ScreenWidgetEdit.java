/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget.screen;

import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ScreenWidgetEdit extends AbstractFrame {
    @Inject
    protected LookupField screenIdLookup;
    @Inject
    protected WindowConfig windowConfig;
    @Inject
    protected Metadata metadata;

    protected Datasource<Widget> widgetDs;

    @WidgetParam(type = ParameterType.STRING)
    @WindowParam
    protected String screenId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        screenIdLookup.setOptionsList(getAllScreensIds());
        screenIdLookup.addValueChangeListener(e -> screenIdSelected((String) e.getValue()));

        initWidgetDs(params);
        selectScreenId();
    }

    protected void initWidgetDs(Map<String, Object> params) {
        widgetDs = (Datasource<Widget>) params.get(WidgetEdit.ITEM_DS);
        /*Widget widget = widgetDs.getItem();

        if (!(widget instanceof ScreenWidget)) {
            ScreenWidget screenWidget = metadata.create(ScreenWidget.class);
            BeanUtils.copyProperties(widget, screenWidget);
            widgetDs.setItem(screenWidget);
        }*/
    }

    protected void selectScreenId() {
        if (isNotBlank(screenId)) {
            screenIdLookup.setValue(screenId);
        }
    }

    protected void screenIdSelected(String screenId) {
        this.screenId = screenId;
    }

    protected List<String> getAllScreensIds() {
        return windowConfig.getWindows().stream()
                .map(WindowInfo::getId)
                .collect(Collectors.toList());
    }
}
