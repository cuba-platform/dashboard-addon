/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.annotation;

import com.audimex.dashboard.model.Widget;

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
     * The caption for widget-edit screen, can be localized in main message.properties by pattern:
     * widgetTypeCaption{caption} = {localization message}
     */
    String caption();

    /**
     * Contains frame ID for showing a widget. Frame Controller must be inherited from the class
     * {@link com.audimex.dashboard.web.widget_types.AbstractWidgetBrowse}
     */
    String browseFrameId();

    /**
     *  Contains frame ID for editing a widget. This frame is embedded in the "widget-edit" screen
     */
    String editFrameId();
}
