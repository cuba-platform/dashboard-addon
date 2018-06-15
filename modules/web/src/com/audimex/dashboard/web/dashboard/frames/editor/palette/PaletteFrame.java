/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.frames.editor.palette;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.WidgetTemplate;
import com.audimex.dashboard.model.Widget;
import com.audimex.dashboard.model.visual_model.WidgetLayout;
import com.audimex.dashboard.web.dashboard.frames.editor.vaadin_components.PaletteButton;
import com.audimex.dashboard.web.dashboard.tools.AccessConstraintsHelper;
import com.audimex.dashboard.web.dashboard.tools.component_factories.PaletteComponentsFactory;
import com.audimex.dashboard.web.dashboard.tools.drop_handlers.NotDropHandler;
import com.audimex.dashboard.web.widget.WidgetEdit;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class PaletteFrame extends AbstractFrame {
    public static final String SCREEN_NAME = "paletteFrame";

    @Inject
    protected DDVerticalLayout ddWidgetBox;
    @Inject
    protected DDVerticalLayout ddLayoutBox;
    @Inject
    protected DDVerticalLayout ddWidgetTemplateBox;
    @Inject
    protected Button doTemplateBtn;
    @Inject
    protected Button removeWidgetBtn;
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PaletteComponentsFactory factory;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected AccessConstraintsHelper accessHelper;

    protected PaletteButton selectedWidgetBtn = null;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initWidgetBox();
        initLayoutBox();
        initWidgetTemplateBox();
    }

    protected void initWidgetBox() {
        ddWidgetBox.setDropHandler(new NotDropHandler());
    }

    protected void initLayoutBox() {
        ddLayoutBox.setDropHandler(new NotDropHandler());

        ddLayoutBox.add(factory.createVerticalLayoutButton());
        ddLayoutBox.add(factory.createHorizontalLayoutButton());
        ddLayoutBox.add(factory.createGridLayoutButton());
    }

    protected void initWidgetTemplateBox() {
        ddWidgetTemplateBox.setDropHandler(new NotDropHandler());

        for (Widget widget : getWidgetTemplates()) {
            PaletteButton widgetBtn = factory.createWidgetButton(widget);
            ddWidgetTemplateBox.add(widgetBtn);
        }
    }

    protected List<Widget> getWidgetTemplates() {
        widgetTemplatesDs.refresh();
        return widgetTemplatesDs.getItems().stream()
                .map(widgetTemplate -> converter.widgetFromJson(widgetTemplate.getWidgetModel()))
                .filter(accessHelper::isWidgetTemplateAllowedCurrentUser)
                .collect(Collectors.toList());
    }

    public void createWidget() {
        WidgetEdit editor = (WidgetEdit) openEditor(WidgetEdit.SCREEN_NAME, metadata.create(Widget.class), THIS_TAB);
        editor.addCloseWithCommitListener(() -> {
            PaletteButton widgetBtn = factory.createWidgetButton(editor.getItem());
            widgetBtn.setAction(new BaseAction("createWidget").withHandler(event -> {
                selectedWidgetBtn = (PaletteButton) event.getComponent();

                for (Component btn : ddWidgetBox.getOwnComponents()) {
                    btn.addStyleName("amxd-dashboard-button");
                    btn.removeStyleName("amxd-dashboard-selected-button");
                }

                selectedWidgetBtn.removeStyleName("amxd-dashboard-button");
                selectedWidgetBtn.addStyleName("amxd-dashboard-selected-button");
                doTemplateBtn.setEnabled(true);
                removeWidgetBtn.setEnabled(true);
            }));

            ddWidgetBox.add(widgetBtn);
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

            PaletteButton widgetBtn = factory.createWidgetButton(widget);
            ddWidgetTemplateBox.add(widgetBtn);
        }
    }

    public void removeWidget() {
        if (selectedWidgetBtn != null) {
            ddWidgetBox.remove(selectedWidgetBtn);
            selectedWidgetBtn = null;
            doTemplateBtn.setEnabled(false);
            removeWidgetBtn.setEnabled(false);
        }
    }
}
