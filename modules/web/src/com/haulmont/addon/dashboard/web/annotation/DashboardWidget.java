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
