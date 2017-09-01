/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.audimex.dashboard.entity.*;
import com.audimex.dashboard.web.layouts.*;
import com.audimex.dashboard.web.screens.filters.FakeQueryFilterSupport;
import com.audimex.dashboard.web.tools.DashboardTools;
import com.audimex.dashboard.web.tools.ParameterTools;
import com.audimex.dashboard.web.widgets.frames.UndefinedParametersFrame;
import com.haulmont.bali.util.Dom4j;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.QueryFilter;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Filter;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.filter.ConditionsTree;
import com.haulmont.cuba.gui.components.filter.FilterDelegate;
import com.haulmont.cuba.gui.components.filter.FilterParser;
import com.haulmont.cuba.gui.components.filter.Param;
import com.haulmont.cuba.gui.components.filter.edit.FilterEditor;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.FilterEntity;
import com.haulmont.cuba.security.entity.SearchFolder;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.gui.components.WebFilter;
import com.haulmont.cuba.web.gui.components.WebTable;
import com.vaadin.ui.*;
import org.apache.commons.lang.BooleanUtils;
import org.dom4j.Element;

import java.util.*;
import java.util.function.Consumer;

import static com.haulmont.cuba.gui.components.Window.COMMIT_ACTION_ID;

public class FramePanel extends CssLayout implements HasWeight, HasGridSpan, HasMainLayout, HasDragCaption {
    protected VerticalLayout contentLayout = new VerticalLayout();
    protected Tree tree;

    protected int weight = 1;
    protected int colSpan = 1;
    protected int rowSpan = 1;

    protected Dashboard dashboard;

    protected DashboardWidget widget;

    protected DashboardWidget templateWidget;

    protected DashboardTools dashboardTools;

    protected ParameterTools parameterTools;

    protected Metadata metadata;

    protected Frame parentFrame;

    public FramePanel(Tree tree, Dashboard dashboard, DashboardWidget widget, Frame parentFrame, Consumer<Tree> treeHandler) {
        this.tree = tree;
        this.dashboard = dashboard;
        this.widget = widget;
        this.parentFrame = parentFrame;

        dashboardTools = AppBeans.get(DashboardTools.NAME);
        parameterTools = AppBeans.get(ParameterTools.NAME);
        metadata = AppBeans.get(Metadata.NAME);

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        Button configButton = new Button(WebComponentsHelper.getIcon("icons/gear.png"));
        configButton.addClickListener(event -> showConfigWindow());
        Button removeButton = new Button(WebComponentsHelper.getIcon("icons/trash.png"));
        removeButton.addClickListener((Button.ClickListener) event -> {
            dashboardTools.removeComponent(tree, tree.getValue());
            treeHandler.accept(tree);
        });

        if (WidgetViewType.LIST.equals(widget.getWidgetViewType())) {
            initFilterButton(buttonsPanel);
        }

        buttonsPanel.addComponent(configButton);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addStyleName(DashboardTools.AMXD_LAYOUT_CONTROLS);

        contentLayout.setSizeFull();
        contentLayout.setStyleName("amxd-widget-content");
        contentLayout.setMargin(true);

        addComponent(buttonsPanel);
        addComponent(contentLayout);

        setSizeFull();
        addStyleName(DashboardTools.AMXD_SHADOW_BORDER);

        initContent();
    }

    protected void initFilterButton(HorizontalLayout buttonsPanel) {
        Button filterConfigButton = new Button(WebComponentsHelper.getIcon("font-icon:FILTER"));
        filterConfigButton.addClickListener((Button.ClickListener) event -> {
            String entityName = widget.getEntityType();
            MetaClass entityNameFromMetaClass = metadata.getSession().getClassNN(entityName);
            FakeQueryFilterSupport fakeFilterSupport = new FakeQueryFilterSupport(parentFrame, entityNameFromMetaClass);

            WindowManager windowManager = App.getInstance().getWindowManager();
            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            WindowInfo windowInfo = windowConfig.getWindowInfo("filterEditor");

            Optional<DashboardWidgetLink> optional = widget.getDashboardLinks().stream().findFirst();
            final DashboardWidgetLink link = optional.orElse(null);


            Map<String, Object> params = new HashMap<>();
            String filterXml = link.getFilter();
            final Filter fakeFilter = fakeFilterSupport.createFakeFilter();
            //fakeFilter.setShowCollections(true);
            final FilterEntity filterEntity = fakeFilterSupport.createFakeFilterEntity(filterXml);
            final ConditionsTree conditionsTree = fakeFilterSupport.createFakeConditionsTree(fakeFilter, filterEntity);

            params.put("filter", fakeFilter);
            params.put("filterEntity", filterEntity);
            params.put("conditions", conditionsTree);
            params.put("useShortConditionForm", true);

            FilterEditor filterEditor = (FilterEditor) windowManager.openWindow(windowInfo, WindowManager.OpenType.DIALOG, params);
            filterEditor.addCloseListener(actionId -> {
                if (!COMMIT_ACTION_ID.equals(actionId)) return;
                FilterParser filterParser = AppBeans.get(FilterParser.class);
                filterEntity.setXml(filterParser.getXml(filterEditor.getConditions(), Param.ValueProperty.DEFAULT_VALUE));
                if (filterEntity.getXml() != null) {
                    link.setFilter(filterEntity.getXml());
                    initContent();
                }
            });
        });

        buttonsPanel.addComponent(filterConfigButton);
    }

    public void initContent() {
        WindowManager windowManager = App.getInstance().getWindowManager();
        WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
        WindowInfo windowInfo = windowConfig.getWindowInfo(widget.getFrameId());

        List<String> undefinedParameters = new ArrayList<>();
        Map<String, Object> params = getParameters();

        for (Map.Entry<String, Object> par : params.entrySet()) {
            if (par.getValue() == null) {
                undefinedParameters.add(par.getKey());
            }
        }

        Frame frame;

        if (undefinedParameters.size() == 0) {
            frame = windowManager.openFrame(parentFrame, null, windowInfo, params);
            frame.setId("widgetFrame");
            if (WidgetViewType.LIST.equals(widget.getWidgetViewType())) {
                /*frame.getComponents().forEach(component -> {
                    if (component instanceof WebFilter) {
                        WebFilter filter = (WebFilter) component;
                        Optional<DashboardWidgetLink> optional = widget.getDashboardLinks().stream().findFirst();
                        DashboardWidgetLink link = optional.orElse(null);
                        if (link != null && link.getFilter() != null) {
                            FilterEntity filterEntity = metadata.create(FilterEntity.class);
                            filterEntity.setComponentId(filter.getId());
                            filterEntity.setName(filter.getCaption());

                            filterEntity.setXml(link.getFilter());
                            filter.setFilterEntity(filterEntity);
                        }
                    }
                    if (component instanceof WebTable) {
                        *//*WebTable table = (WebTable) component;
                        CollectionDatasource datasource = table.getDatasource();
                        if(datasource.getMetaClass().getName().equals(widget.getEntityType())) {
                            Optional<DashboardWidgetLink> optional = widget.getDashboardLinks().stream().findFirst();
                            DashboardWidgetLink link = optional.orElse(null);
                            if (link != null && link.getFilter() != null) {
                                Element element = Dom4j.readDocument(link.getFilter()).getRootElement();
                                QueryFilter filter = new QueryFilter(element);
                                datasource.setQueryFilter(filter);
                            }
                        }*//*
                    }
                });*/
                frame.getDsContext().refresh();
            } else if (WidgetViewType.CHART.equals(widget.getWidgetViewType())) {

            }
        } else {
            windowInfo = windowConfig.getWindowInfo("amxd$UndefinedParameters.frame");
            params.put(UndefinedParametersFrame.PARAMETER_UNDEFINED_VALUES_LIST, undefinedParameters);
            params.put(UndefinedParametersFrame.PARAMETER_WIDGET, widget);
            frame = windowManager.openFrame(parentFrame, null, windowInfo, params);
        }


        frame.setParent(parentFrame);
        setContent(frame.unwrapComposition(Layout.class));
    }

    protected Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        for (DashboardWidgetLink link : widget.getDashboardLinks()) {
            for (WidgetParameter parameter : link.getDashboardParameters()) {
                Object value = parameterTools.getWidgetLinkParameterValue(parameter);
                params.put(parameter.getName(), value);
            }
        }
        return params;
    }

    protected void showConfigWindow() {
        Map<String, Object> params = new HashMap<>();
        params.put("widget", this);
        params.put("tree", tree);
        com.haulmont.cuba.gui.components.Window window =
                parentFrame.openWindow("widgetConfigWindow", WindowManager.OpenType.DIALOG, params);

        window.addCloseListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                initContent();
            }
        });
    }

    public void setContent(Component c) {
        contentLayout.removeAllComponents();

        if (c != null) {
            c.setSizeFull();
            contentLayout.addComponent(c);
        }
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public DashboardWidget getWidget() {
        return widget;
    }

    public String getFrameId() {
        return widget.getFrameId();
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;

        if (getParent() instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) getParent()).setExpandRatio(this, weight);
        }
    }

    @Override
    public int getColSpan() {
        return colSpan;
    }

    @Override
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
        GridCell gridCell = dashboardTools.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setColspan(colSpan);
        }

        if (getParent() != null) {
            if (getParent().getParent() instanceof DashboardGridLayout) {
                DashboardGridLayout parent = (DashboardGridLayout) getParent().getParent();
                parent.removeComponent(this);

                dashboardTools.removeEmptyLabelsForSpan(parent, gridCell);
                parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                        gridCell.getColumn() + gridCell.getColspan() - 1,
                        gridCell.getRow() + gridCell.getRowspan() - 1);
                dashboardTools.addEmptyLabels(parent, tree);
                dashboardTools.lockGridCells(parent, tree);
            }
        }
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    @Override
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
        GridCell gridCell = dashboardTools.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setRowspan(rowSpan);
        }

        if (getParent() != null) {
            if (getParent().getParent() instanceof DashboardGridLayout) {
                DashboardGridLayout parent = (DashboardGridLayout) getParent().getParent();

                parent.removeComponent(this);

                dashboardTools.removeEmptyLabelsForSpan(parent, gridCell);
                parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                        gridCell.getColumn() + gridCell.getColspan() - 1,
                        gridCell.getRow() + gridCell.getRowspan() - 1);
                dashboardTools.addEmptyLabels(parent, tree);
                dashboardTools.lockGridCells(parent, tree);
            }
        }
    }

    @Override
    public String getWidgetIcon() {
        return widget.getIcon();
    }

    public void setWidgetIcon(String icon) {

    }

    @Override
    public String getWidgetCaption() {
        return widget.getCaption();
    }

    @Override
    public void setWidgetCaption(String caption) {

    }

    @Override
    public AbstractOrderedLayout getMainLayout() {
        return contentLayout;
    }

    @Override
    public void setMargin(boolean margin) {
        contentLayout.setMargin(margin);
    }

    public DashboardWidget getTemplateWidget() {
        return templateWidget;
    }

    public void setTemplateWidget(DashboardWidget templateWidget) {
        this.templateWidget = templateWidget;
    }
}