/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor.palette;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.WidgetTemplate;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;
import com.audimex.dashboard.web.dashboard.tools.component_factories.VaadinDropComponentsFactory;
import com.audimex.dashboard.web.widget.WidgetEdit;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.vaadin.ui.Layout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class PaletteFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "paletteFrame";

    @Inject
    protected ScrollBoxLayout widgetBox;
    @Inject
    protected ScrollBoxLayout layoutBox;
    @Inject
    protected ScrollBoxLayout widgetTemplateBox;
    @Inject
    protected Button doTemplateBtn;
    @Inject
    protected Button removeWidgetBtn;
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected VaadinDropComponentsFactory factory;
    @Inject
    protected JsonConverter converter;

    protected DDVerticalLayout ddWidgetBox;
    protected DDVerticalLayout ddWidgetTemplateBox;

    protected PaletteButton selectedWidgetBtn = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initWidgetBox();
        initLayoutBox();
        initWidgetTemplateBox();
    }

    protected void initWidgetBox() {
        ddWidgetBox = factory.createNotDroppedVerticalLayout();
        widgetBox.unwrap(Layout.class).addComponent(ddWidgetBox);
    }

    protected void initLayoutBox() {
        DDVerticalLayout ddLayoutBox = factory.createNotDroppedVerticalLayout();

        ddLayoutBox.addComponent(factory.createPaletteVerticalLayoutButton());
        ddLayoutBox.addComponent(factory.createPaletteHorizontalLayoutButton());
        ddLayoutBox.addComponent(factory.createPaletteGridLayoutButton());
        layoutBox.unwrap(Layout.class).addComponent(ddLayoutBox);
    }

    protected void initWidgetTemplateBox() {
        ddWidgetTemplateBox = factory.createNotDroppedVerticalLayout();

        for (Widget widget : getWidgetTemplates()) {
            PaletteButton widgetBtn = factory.createPaletteWidgetButton(widget);
            ddWidgetTemplateBox.addComponent(widgetBtn);
        }

        widgetTemplateBox.unwrap(Layout.class).addComponent(ddWidgetTemplateBox);
    }

    protected List<Widget> getWidgetTemplates() {
        widgetTemplatesDs.refresh();
        return widgetTemplatesDs.getItems().stream()
                .map(widgetTemplate -> converter.widgetFromJson(widgetTemplate.getWidgetModel()))
                .collect(Collectors.toList());
    }

    public void createWidget() {
        WidgetEdit editor = (WidgetEdit) openEditor(WidgetEdit.SCREEN_NAME, metadata.create(Widget.class), THIS_TAB);
        editor.addCloseWithCommitListener(() -> {
            PaletteButton widgetBtn = factory.createPaletteWidgetButton(editor.getItem());
            widgetBtn.addClickListener(event -> {
                selectedWidgetBtn = (PaletteButton) event.getSource();

                ddWidgetBox.forEach(btn -> {
                    btn.addStyleName("amxd-dashboard-button");
                    btn.removeStyleName("amxd-dashboard-selected-button");
                });

                selectedWidgetBtn.removeStyleName("amxd-dashboard-button");
                selectedWidgetBtn.addStyleName("amxd-dashboard-selected-button");
                doTemplateBtn.setEnabled(true);
                removeWidgetBtn.setEnabled(true);
            });

            ddWidgetBox.addComponent(widgetBtn);
        });
    }

    public void doTemplate() {
        if (selectedWidgetBtn != null) {
            Widget widget = ((WidgetLayout) selectedWidgetBtn.getLayout()).getWidget();

            WidgetTemplate widgetTemplate = metadata.create(WidgetTemplate.class);
            widget.setId(widgetTemplate.getId());
            widgetTemplate.setWidgetModel(converter.widgetToJson(widget));

            widgetTemplatesDs.addItem(widgetTemplate);
            widgetTemplatesDs.commit();

            PaletteButton widgetBtn = factory.createPaletteWidgetButton(widget);
            ddWidgetTemplateBox.addComponent(widgetBtn);
        }
    }

    public void removeWidget() {
        if (selectedWidgetBtn != null) {
            ddWidgetBox.removeComponent(selectedWidgetBtn);
            selectedWidgetBtn = null;
            doTemplateBtn.setEnabled(false);
            removeWidgetBtn.setEnabled(false);
        }
    }
}
