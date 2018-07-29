/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.lookup;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.model.widget_types.LookupWidget;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ScreenXmlLoader;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget.WidgetEdit.ITEM_DS;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class LookupWidgetEdit extends AbstractFrame {
    @Inject
    protected LookupField lookupIdLookup;
    @Inject
    protected WindowConfig windowConfig;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ScreenXmlLoader screenXmlLoader;

    protected Datasource<Widget> widgetDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        lookupIdLookup.setOptionsList(getAllLookupIds());
        lookupIdLookup.addValueChangeListener(e -> lookupIdSelected((String) e.getValue()));

        initWidgetDs(params);
        selectLookupId();
    }

    protected void initWidgetDs(Map<String, Object> params) {
        widgetDs = (Datasource<Widget>) params.get(WidgetEdit.ITEM_DS);
        Widget widget = widgetDs.getItem();

        if (!(widget instanceof LookupWidget)) {
            LookupWidget lookupWidget = metadata.create(LookupWidget.class);
            BeanUtils.copyProperties(widget, lookupWidget);
            widgetDs.setItem(lookupWidget);
        }
    }

    protected void selectLookupId() {
        LookupWidget item = (LookupWidget) widgetDs.getItem();
        if (isNotBlank(item.getLookupWindowId())) {
            lookupIdLookup.setValue(item.getLookupWindowId());
        }
    }

    protected void lookupIdSelected(String lookupWindowId) {
        ((LookupWidget) widgetDs.getItem()).setLookupWindowId(lookupWindowId);
    }

    protected List<String> getAllLookupIds() {
        Collection<WindowInfo> allWindows = windowConfig.getWindows();
        List<String> lookupIds = new ArrayList<>();

        for (WindowInfo winInfo : allWindows) {
            if (isNotBlank(winInfo.getTemplate()) && isLookupWindow(winInfo)) {
                lookupIds.add(winInfo.getId());
            } else if (winInfo.getScreenClass() != null &&
                    AbstractLookup.class.isAssignableFrom(winInfo.getScreenClass())) {
                lookupIds.add(winInfo.getId());
            }
        }

        return lookupIds;
    }

    protected boolean isLookupWindow(WindowInfo winInfo) {
        Element element = screenXmlLoader.load(winInfo.getTemplate(), winInfo.getId(), ParamsMap.empty());
        String screenClassName = element.attributeValue("class");

        try {
            Class<?> screenClass = Class.forName(screenClassName);

            if (AbstractLookup.class.isAssignableFrom(screenClass)) {
                return true;
            }
        } catch (NullPointerException | ClassNotFoundException e) {
            return false;
        }
        return false;
    }
}
