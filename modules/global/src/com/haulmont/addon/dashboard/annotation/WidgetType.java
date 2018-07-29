/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.annotation;

import com.haulmont.addon.dashboard.model.Widget;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a widget type.
 * Such classes must be inherited from the {@link Widget}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WidgetType {

    /**
     * The name for widget-edit screen, can be localized in main message.properties by pattern:
     * widgetType.{name} = {localization message}
     */
    String name();

    /**
     * Contains frame ID for showing a widget. Frame Controller must be inherited from the class
     * {@link com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse}
     */
    String browseFrameId();

    /**
     *  Contains frame ID for editing a widget. This frame is embedded in the "widget-edit" screen
     */
    String editFrameId();
}
