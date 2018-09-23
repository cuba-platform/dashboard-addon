package com.haulmont.addon.dashboard.web.dashboard.tools;

import com.haulmont.addon.dashboard.web.repository.WidgetRepository;
import com.haulmont.addon.dashboard.web.repository.WidgetTypeInfo;
import com.haulmont.cuba.core.global.Messages;
import org.apache.commons.lang.StringUtils;
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

    public String getWidgetType(String browseFrameId) {
        String result = StringUtils.EMPTY;
        Optional<WidgetTypeInfo> widgetTypeOpt = widgetRepository.getWidgetTypesInfo().stream()
                .filter(typeInfo -> browseFrameId.equals(typeInfo.getBrowseFrameId()))
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
            String browseFrameId = typeInfo.getBrowseFrameId();
            String name = typeInfo.getName();
            String property = format("widgetType.%s", name);
            String mainMessage = messages.getMainMessage(property);
            String caption = mainMessage.equals(property) ? name : mainMessage;

            map.put(caption, browseFrameId);
        }

        return map;
    }
}
