/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.annotation;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a widget type.
 * Such classes must be inherited from the {@link com.haulmont.addon.dashboard.model.Widget}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DashboardWidget {

    /**
     * The name for widget-edit screen, can be localized in main message.properties by pattern:
     * dashboard-widget.{name} = {localization message}
     */
    String name();

    /**
     *  Contains frame ID for editing a widget. This frame is embedded in the "widget-edit" screen
     */
    String editFrameId() default "";
}
