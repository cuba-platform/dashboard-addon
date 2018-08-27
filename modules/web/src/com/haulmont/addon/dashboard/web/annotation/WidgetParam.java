package com.haulmont.addon.dashboard.web.annotation;

import com.haulmont.addon.dashboard.model.ParameterType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WidgetParam {

    ParameterType type();

    String viewName() default "";
}
