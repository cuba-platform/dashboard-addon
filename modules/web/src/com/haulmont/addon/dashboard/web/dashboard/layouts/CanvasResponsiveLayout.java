package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardResponsiveLayout;
import com.haulmont.addon.dnd.web.gui.components.WebDDGridLayout;
import com.haulmont.addon.dnd.web.gui.components.WebDragAndDropWrapper;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebHBoxLayout;
import com.haulmont.cuba.web.gui.components.WebTextArea;
import com.haulmont.cuba.web.toolkit.ui.CubaTextField;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.ui.TextField;
import org.strangeway.responsive.web.components.ResponsiveLayout;
import org.strangeway.responsive.web.components.impl.WebResponsiveColumn;
import org.strangeway.responsive.web.components.impl.WebResponsiveLayout;
import org.strangeway.responsive.web.components.impl.WebResponsiveRow;

public class CanvasResponsiveLayout extends AbstractCanvasLayout {
    protected WebDragAndDropWrapper wrapper;

    public CanvasResponsiveLayout(DashboardResponsiveLayout model) {
        super(model, new WebDragAndDropWrapper());
        wrapper = (WebDragAndDropWrapper) delegate;
        ResponsiveLayout responsiveLayout = new WebResponsiveLayout();

        WebResponsiveRow wrr = new WebResponsiveRow();
        WebResponsiveColumn wrc = new WebResponsiveColumn();
        wrc.add(new WebTextArea());
        wrr.addColumn(wrc);

        WebResponsiveColumn wrc2 = new WebResponsiveColumn();
        wrc2.add(new WebTextArea());
        wrr.addColumn(wrc2);

        WebResponsiveColumn wrc3 = new WebResponsiveColumn();
        wrc3.add(new WebTextArea());
        wrr.addColumn(wrc3);

        responsiveLayout.addRow(wrr);

        wrapper.add(responsiveLayout);

    }

    @Override
    public WebDragAndDropWrapper getDelegate() {
        return wrapper;
    }

    public void addComponent(Component component, int col, int row, int col2, int row2) {
        //gridLayout.add(component, col, row, col2, row2);
    }
}
