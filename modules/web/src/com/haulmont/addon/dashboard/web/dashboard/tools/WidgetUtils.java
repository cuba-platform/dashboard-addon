/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */

package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.cuba.core.global.Messages;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Component
public class WidgetUtils {

    @Inject
    protected WidgetRepository widgetRepository;

    @Inject
    protected Messages messages;

    public String getWidgetType(String frameId) {
        String result = StringUtils.EMPTY;
        Optional<WidgetTypeInfo> widgetTypeOpt = widgetRepository.getWidgetTypesInfo().stream()
                .filter(typeInfo -> frameId.equals(typeInfo.getFrameId()))
                .findFirst();

        if (widgetTypeOpt.isPresent()) {
            result = widgetTypeOpt.get().getName();
        }
        return result;
    }

    public Map<String, String> getWidgetCaptions() {
        Map<String, String> map = new HashMap<>();
        List<WidgetTypeInfo> typesInfo = widgetRepository.getWidgetTypesInfo();
        for (WidgetTypeInfo typeInfo : typesInfo) {
            String browseFrameId = typeInfo.getFrameId();
            String name = typeInfo.getName();
            String property = format("dashboard-widget.%s", name);
            String mainMessage = messages.getMainMessage(property);
            String caption = mainMessage.equals(property) ? name : mainMessage;

            map.put(caption, browseFrameId);
        }

        return map;
    }
}
