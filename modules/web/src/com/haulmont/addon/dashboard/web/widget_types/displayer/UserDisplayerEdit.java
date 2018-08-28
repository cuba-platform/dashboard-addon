/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.displayer;

import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ScreenXmlLoader;
import org.dom4j.Element;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class UserDisplayerEdit extends AbstractWidgetEdit {


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);


    }


}
