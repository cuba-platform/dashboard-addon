/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widget_types.screen;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.widget_types.ScreenWidget;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.Datasource;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.widget_tempate.WidgetTemplateEdit.ITEM_DS;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ScreenWidgetEdit extends AbstractFrame {
    @Inject
    protected LookupField screenIdLookup;
    @Inject
    protected WindowConfig windowConfig;
    @Inject
    protected Metadata metadata;

    protected Datasource<Widget> widgetDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        screenIdLookup.setOptionsList(getAllScreensIds());
        screenIdLookup.addValueChangeListener(e -> screenIdSelected((String) e.getValue()));

        initWidgetDs(params);
        selectScreenId();
    }

    protected void initWidgetDs(Map<String, Object> params) {
        widgetDs = (Datasource<Widget>) params.get(ITEM_DS);
        Widget widget = widgetDs.getItem();

        if (!(widget instanceof ScreenWidget)) {
            ScreenWidget screenWidget = metadata.create(ScreenWidget.class);
            BeanUtils.copyProperties(widget, screenWidget);
            widgetDs.setItem(screenWidget);
        }
    }

    protected void selectScreenId() {
        ScreenWidget item = (ScreenWidget) widgetDs.getItem();
        if (isNotBlank(item.getScreenId())) {
            screenIdLookup.setValue(item.getScreenId());
        }
    }

    protected void screenIdSelected(String screenId) {
        ((ScreenWidget) widgetDs.getItem()).setScreenId(screenId);
    }

    protected List<String> getAllScreensIds() {
        return windowConfig.getWindows().stream()
                .map(WindowInfo::getId)
                .collect(Collectors.toList());
    }
}
