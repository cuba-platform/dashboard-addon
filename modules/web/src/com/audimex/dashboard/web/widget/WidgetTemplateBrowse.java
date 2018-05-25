package com.audimex.dashboard.web.widget;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.WidgetTemplate;
import com.audimex.dashboard.model.Widget;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.audimex.dashboard.web.widget.WidgetEdit.SCREEN_NAME;
import static com.haulmont.cuba.gui.WindowManager.OpenType.THIS_TAB;

public class WidgetTemplateBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<WidgetTemplate, UUID> widgetTemplatesDs;
    @Inject
    protected CollectionDatasource<Widget, UUID> modelWidgetsDs;
    @Inject
    protected JsonConverter converter;
    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initDs();
    }


    protected void initDs() {
        widgetTemplatesDs.addCollectionChangeListener(e -> updateTable());
        widgetTemplatesDs.refresh();
    }

    protected void updateTable() {
        modelWidgetsDs.clear();
        for (WidgetTemplate persWidget : widgetTemplatesDs.getItems()) {
            Widget model = converter.widgetFromJson(persWidget.getWidgetModel());
            modelWidgetsDs.includeItem(model);
        }
    }

    public void createWidget() {
        Widget widget = metadata.create(Widget.class);
        widget.setIsTemplate(true);
        openWidgetEditor(widget);
    }

    public void editWidget() {
        if (modelWidgetsDs.getItem() != null) {
            openWidgetEditor(modelWidgetsDs.getItem());
        }
    }

    public void removeWidget() {
        if (modelWidgetsDs.getItem() != null) {
            UUID widgetId = modelWidgetsDs.getItem().getId();

            widgetTemplatesDs.getItems().stream()
                    .filter(widget -> widgetId.equals(widget.getId()))
                    .findFirst()
                    .ifPresent(widget -> {
                        widgetTemplatesDs.removeItem(widget);
                        widgetTemplatesDs.commit();
                        widgetTemplatesDs.refresh();
                    });
        }
    }

    protected void openWidgetEditor(Widget widget) {
        AbstractEditor editor = openEditor(SCREEN_NAME, widget, THIS_TAB, modelWidgetsDs);
        editor.addCloseWithCommitListener(() -> addOrUpdateWidgetTemplate((Widget) editor.getItem()));
    }

    protected void addOrUpdateWidgetTemplate(Widget item) {
        UUID widgetId = item.getId();
        String jsonModel = converter.widgetToJson(item);

        Optional persWidgetOpt = widgetTemplatesDs.getItems().stream()
                .filter(widget -> widgetId.equals(widget.getId()))
                .findFirst();

        if (persWidgetOpt.isPresent()) {
            WidgetTemplate persWidget = (WidgetTemplate) persWidgetOpt.get();
            persWidget.setWidgetModel(jsonModel);
            widgetTemplatesDs.updateItem(persWidget);
        } else {
            WidgetTemplate persWidget = metadata.create(WidgetTemplate.class);
            persWidget.setId(item.getId());
            persWidget.setWidgetModel(jsonModel);
            widgetTemplatesDs.addItem(persWidget);
        }

        widgetTemplatesDs.commit();
        widgetTemplatesDs.refresh();
    }
}