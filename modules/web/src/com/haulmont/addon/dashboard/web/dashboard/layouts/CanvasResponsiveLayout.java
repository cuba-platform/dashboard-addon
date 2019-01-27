package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.ResponsiveLayout;
import com.haulmont.addon.dashboard.web.dashboard.events.CanvasLayoutElementClickedEvent;
import com.haulmont.addon.dnd.web.gui.components.WebDragAndDropWrapper;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.components.Component;
import org.strangeway.responsive.web.components.impl.WebResponsiveColumn;
import org.strangeway.responsive.web.components.impl.WebResponsiveLayout;
import org.strangeway.responsive.web.components.impl.WebResponsiveRow;

import java.util.Iterator;

public class CanvasResponsiveLayout extends AbstractCanvasLayout {
    protected WebDragAndDropWrapper wrapper;

    public CanvasResponsiveLayout(ResponsiveLayout model) {
        super(model, new WebDragAndDropWrapper());
        wrapper = (WebDragAndDropWrapper) delegate;
        org.strangeway.responsive.web.components.ResponsiveLayout responsiveLayout = new WebResponsiveLayout();

        WebResponsiveRow wrr = new WebResponsiveRow();
        Events events = AppBeans.get(Events.class);
        wrr.addLayoutClickListener(e -> {
            CanvasLayout selectedLayout = this;
            events.publish(new CanvasLayoutElementClickedEvent(selectedLayout.getUuid(), e.getMouseEventDetails()));

        });
        wrapper.setSizeFull();
        responsiveLayout.setSizeFull();
        responsiveLayout.setSpacing(true);
        responsiveLayout.setFlexible(true);
        wrr.setSizeFull();

        responsiveLayout.addRow(wrr);
        wrapper.add(responsiveLayout);
    }

    @Override
    public WebDragAndDropWrapper getDelegate() {
        return wrapper;
    }

    public void addComponent(Component component) {
        WebResponsiveLayout wrl = (WebResponsiveLayout) wrapper.getDraggedComponent();

        Iterator it = wrl.getOwnComponents().iterator();
        WebResponsiveRow wrr = (WebResponsiveRow) it.next();

        ResponsiveLayout rl = (ResponsiveLayout) model;

        WebResponsiveColumn wrc = new WebResponsiveColumn();
        wrc.setDisplayRules(rl.getXs(), rl.getSm(), rl.getMd(), rl.getLg());
        component.setSizeFull();
        wrc.setContent(component);
        wrr.addColumn(wrc);
    }
}
