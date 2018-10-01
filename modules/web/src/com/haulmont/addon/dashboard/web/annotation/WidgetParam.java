package com.haulmont.addon.dashboard.web.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WidgetParam {

    String viewName() default "";
}
