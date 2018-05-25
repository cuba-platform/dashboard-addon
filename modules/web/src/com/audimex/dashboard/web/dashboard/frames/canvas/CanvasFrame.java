package com.audimex.dashboard.web.dashboard.frames.canvas;

import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.VerticalLayout;
import com.audimex.dashboard.web.dashboard.events.LayoutRemoveEvent;
import com.audimex.dashboard.web.dashboard.events.OpenWidgetEditorEvent;
import com.audimex.dashboard.web.dashboard.tools.DashboardModelConverter;
import com.audimex.dashboard.web.dashboard.tools.DropLayoutTools;
import com.audimex.dashboard.web.dashboard.tools.VaadinComponentsFactory;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasVerticalLayout;
import com.audimex.dashboard.web.dashboard.vaadin_components.layouts.CanvasWidgetLayout;
import com.audimex.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Layout;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.Map;

import static com.audimex.dashboard.web.DashboardStyleConstants.AMXD_LAYOUT_SELECTED;
import static com.audimex.dashboard.web.widget.WidgetEdit.SCREEN_NAME;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class CanvasFrame extends AbstractFrame {
    public static final String VISUAL_MODEL = "VISUAL_MODEL";

    @Inject
    protected VBoxLayout canvas;
    @Inject
    protected DropLayoutTools tools;
    @Inject
    protected DashboardModelConverter converter;
    @Inject
    protected VaadinComponentsFactory factory;

    protected CanvasVerticalLayout vLayout;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        vLayout = factory.createCanvasVerticalLayout();
        initDashboardModel(params);
        tools.init(this, vLayout);
        canvas.unwrap(Layout.class).addComponent(vLayout);
        addLayoutClickListener();
    }

    @EventListener
    public void onRemoveLayout(LayoutRemoveEvent event) {
        CanvasLayout source = event.getSource();
        AbstractLayout parent = (AbstractLayout) source.getParent();
        parent.removeComponent(source);
    }

    @EventListener
    public void onOpenWidgetEditor(OpenWidgetEditorEvent event) {
        CanvasWidgetLayout source = event.getSource();
        Widget widget = source.getWidget();

        WidgetEdit editor = (WidgetEdit) openEditor(SCREEN_NAME, widget, THIS_TAB);
        editor.addCloseWithCommitListener(() -> {
            AbstractLayout parent = (AbstractLayout) source.getParent();
            parent.removeComponent(source);

            CanvasWidgetLayout newLayout = factory.createCanvasWidgetLayout(this, editor.getItem());
            tools.addDropHandler(newLayout);
            parent.addComponent(newLayout);
        });
    }

    public VerticalLayout getDashboardModel() {
        return converter.containerToModel(vLayout);
    }


    protected void initDashboardModel(Map<String, Object> params) {
        VerticalLayout model = (VerticalLayout) params.get(VISUAL_MODEL);

        if (model != null) {
            vLayout = (CanvasVerticalLayout) converter.modelToContainer(this, model);
        }
    }

    protected void addLayoutClickListener() {
        vLayout.addLayoutClickListener(e -> {
                    if (e.getClickedComponent() != null) {
                        deselectChildren(vLayout);
                        selectLayout(e.getClickedComponent());
                    }
                }
        );
    }

    protected void deselectChildren(AbstractLayout layout) {
        layout.forEach(child -> {
            if (child instanceof AbstractLayout) {
                child.removeStyleName(AMXD_LAYOUT_SELECTED);
                deselectChildren((AbstractLayout) child);
            }
        });
    }

    protected void selectLayout(com.vaadin.ui.Component component) {
        CanvasLayout layout = (CanvasLayout) getCanvasLayoutParent(component);
        layout.addStyleName(AMXD_LAYOUT_SELECTED);
    }

    protected com.vaadin.ui.Component getCanvasLayoutParent(com.vaadin.ui.Component layout) {
        if (layout instanceof CanvasLayout) {
            return layout;
        } else {
            return getCanvasLayoutParent(layout.getParent());
        }
    }
}
