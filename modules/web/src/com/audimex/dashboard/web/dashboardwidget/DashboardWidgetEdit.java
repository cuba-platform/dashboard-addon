/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.dashboardwidget;

import com.audimex.dashboard.entity.DashboardWidget;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ScreensHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class DashboardWidgetEdit extends AbstractEditor<DashboardWidget> {
    WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    protected ScreensHelper screensHelper;

    @Override
    protected void postInit() {
        super.postInit();

        fieldGroup.addCustomField("frameId", (datasource, propertyId) -> {
            LookupField lookupField = componentsFactory.createComponent(LookupField.class);
            Map<String, String> availableFrames = new HashMap<>();

            try {
                for (WindowInfo windowInfo : windowConfig.getWindows()) {
                    if (isApplicableScreen(windowInfo)) {
                        String screenCaption = screensHelper.getScreenCaption(windowInfo);
                        String screenName;
                        if (StringUtils.isNotBlank(screenCaption)) {
                            screenName = screenCaption +
                                    " (" +
                                    windowInfo.getId() +
                                    ")";
                        } else {
                            screenName = windowInfo.getId();
                        }
                        availableFrames.put(screenName,
                                windowInfo.getId());
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            lookupField.setOptionsMap(availableFrames);
            lookupField.setDatasource(datasource, propertyId);
            return lookupField;
        });
    }

    protected boolean isApplicableScreen(WindowInfo windowInfo) {
        if (windowInfo.getId().contains(".edit")) {
            return false;
        }

        if (windowInfo.getId().contains("filterSelect")) {
            return false;
        }

        if (windowInfo.getId().contains(".changePassword")) {
            return false;
        }

        if (windowInfo.getId().contains("filterEditor")) {
            return false;
        }

        if (windowInfo.getId().contains("loginWindow")) {
            return false;
        }

        if (windowInfo.getId().contains("mainWindow")) {
            return false;
        }

        return true;
    }
}