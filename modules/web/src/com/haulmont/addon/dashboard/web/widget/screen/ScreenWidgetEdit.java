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

package com.haulmont.addon.dashboard.web.widget.screen;

import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;

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

    @WidgetParam
    @WindowParam
    protected String screenId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        screenIdLookup.setOptionsList(getAllScreensIds());
        screenIdLookup.addValueChangeListener(e -> screenIdSelected((String) e.getValue()));

        selectScreenId();
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
        List<String> screenList = windowConfig.getWindows().stream()
                .map(WindowInfo::getId)
                .collect(Collectors.toList());
        filterLookupFields(screenList);
        return screenList;
    }

    protected void filterLookupFields(List<String> lookupIds) {
        lookupIds.removeIf(li -> "addcondition".equalsIgnoreCase(li));
    }
}
