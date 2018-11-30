/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.addon.dashboard.web.widget.lookup;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ScreenXmlLoader;
import org.dom4j.Element;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LookupWidgetEdit extends AbstractFrame {
    @Inject
    protected LookupField<String> lookupIdLookup;
    @Inject
    protected WindowConfig windowConfig;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ScreenXmlLoader screenXmlLoader;

    protected Datasource<Widget> widgetDs;

    @WidgetParam
    @WindowParam
    protected String lookupWindowId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        lookupIdLookup.setOptionsList(getAllLookupIds());
        lookupIdLookup.addValueChangeListener(e -> lookupIdSelected(e.getValue()));

        initWidgetDs(params);
        selectLookupId();
    }

    protected void initWidgetDs(Map<String, Object> params) {
        widgetDs = (Datasource<Widget>) params.get(WidgetEdit.ITEM_DS);
    }

    protected void selectLookupId() {
        if (isNotBlank(lookupWindowId)) {
            lookupIdLookup.setValue(lookupWindowId);
        }
    }

    protected void lookupIdSelected(String lookupWindowId) {
        this.lookupWindowId = lookupWindowId;
    }

    protected List<String> getAllLookupIds() {
        Collection<WindowInfo> allWindows = windowConfig.getWindows();
        List<String> lookupIds = new ArrayList<>();

        for (WindowInfo winInfo : allWindows) {
            if (WindowInfo.Type.FRAGMENT == winInfo.getType()) {
                if (isNotBlank(winInfo.getTemplate()) && isLookupWindow(winInfo)) {
                    lookupIds.add(winInfo.getId());
                } else if (LookupFragment.class.isAssignableFrom(winInfo.getControllerClass())) {
                    lookupIds.add(winInfo.getId());
                }
            }
        }

        filterLookupFields(lookupIds);
        return lookupIds;
    }

    protected boolean isLookupWindow(WindowInfo winInfo) {
        Element element = screenXmlLoader.load(winInfo.getTemplate(), winInfo.getId(), ParamsMap.empty());
        String screenClassName = element.attributeValue("class");

        try {
            Class<?> screenClass = Class.forName(screenClassName);

            if (LookupFragment.class.isAssignableFrom(screenClass)) {
                return true;
            }
        } catch (NullPointerException | ClassNotFoundException e) {
            return false;
        }
        return false;
    }

    protected void filterLookupFields(List<String> lookupIds) {
        lookupIds.removeIf(li -> "commonlookup".equalsIgnoreCase(li));
    }
}
