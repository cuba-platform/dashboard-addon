package com.haulmont.addon.dashboard.web.widget_types;

import com.haulmont.addon.dashboard.model.Widget;
import com.haulmont.addon.dashboard.web.annotation_analyzer.WidgetRepository;
import com.haulmont.addon.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.Map;

public abstract class AbstractWidgetEdit extends AbstractFrame {

    @Inject
    protected WidgetRepository widgetRepository;

//    @Override
//    public void init(Map<String, Object> params) {
//        super.init(params);
//        Datasource<Widget> widgetDs = (Datasource<Widget>) params.get(WidgetEdit.ITEM_DS);
//        widgetRepository.initializeWidgetFields(this, widgetDs.getItem());
//    }
}
