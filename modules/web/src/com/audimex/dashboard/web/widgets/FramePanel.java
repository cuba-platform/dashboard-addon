/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.widgets;

import com.audimex.dashboard.web.drophandlers.TreeDropHandler;
import com.audimex.dashboard.web.layouts.HasGridSpan;
import com.audimex.dashboard.web.layouts.HasMainLayout;
import com.audimex.dashboard.web.layouts.HasWeight;
import com.audimex.dashboard.web.settings.DashboardSettings;
import com.audimex.dashboard.web.utils.DashboardUtils;
import com.audimex.dashboard.web.utils.LayoutUtils;
import com.audimex.dashboard.web.utils.TreeUtils;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.web.App;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.util.HashMap;
import java.util.Map;

public class FramePanel extends CssLayout implements HasWeight, HasGridSpan, HasMainLayout {
    private VerticalLayout contentLayout = new VerticalLayout();
    private Tree tree;

    private int weight = 1;
    private int colSpan = 1;
    private int rowSpan = 1;

    protected DashboardSettings dashboardSettings;
    private Frame parentFrame = null;
    private String frameId;

    public FramePanel(Tree tree) {
        this.tree = tree;
        dashboardSettings = AppBeans.get(DashboardSettings.class);

        HorizontalLayout buttonsPanel = new HorizontalLayout();
        Button configButton = new Button(FontAwesome.GEARS);
        configButton.addClickListener((Button.ClickListener) (event) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("widget", this);
            params.put("tree", tree);
            parentFrame.openWindow("dashboardElementConfig", WindowManager.OpenType.DIALOG, params);
        });
        Button removeButton = new Button(FontAwesome.TRASH);
        removeButton.addClickListener((Button.ClickListener) event -> {
            TreeUtils.removeComponent(tree, tree.getValue());
            ((TreeDropHandler) tree.getDropHandler()).getTreeChangeListener().accept(tree);
        });
        buttonsPanel.addComponent(configButton);
        buttonsPanel.addComponent(removeButton);
        buttonsPanel.addStyleName("dd-layout-controls");

        contentLayout.setSizeFull();
        contentLayout.setStyleName("ad-widget-content");
        contentLayout.setMargin(true);

        addComponent(buttonsPanel);
        addComponent(contentLayout);

        setSizeFull();
        addStyleName("dd-bordering");
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(Frame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setContent(String frameId) {
        WindowManager windowManager = App.getInstance().getWindowManager();
        WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
        WindowInfo windowInfo = windowConfig.getWindowInfo(frameId);
        setFrameId(frameId);

        Frame frame = windowManager.openFrame(parentFrame, null, windowInfo);
        frame.setParent(parentFrame);
        setContent(frame.unwrapComposition(Layout.class));
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public void setContent(Component c) {
        contentLayout.removeAllComponents();

        if (c != null) {
            c.setSizeFull();
            contentLayout.addComponent(c);
        }
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
        GridCell gridCell = LayoutUtils.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setColspan(colSpan);
        }

        if (getParent() instanceof GridLayout) {
            GridLayout parent = (GridLayout) getParent();
            parent.removeComponent(this);

            DashboardUtils.removeEmptyLabelsForSpan(parent, gridCell);
            parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                    gridCell.getColumn() + gridCell.getColspan() - 1,
                    gridCell.getRow() + gridCell.getRowspan() - 1);
            DashboardUtils.addEmptyLabelsToLayout(parent, tree);
            DashboardUtils.lockGridCells(parent, tree);
        }
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    @Override
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
        GridCell gridCell = LayoutUtils.getWidgetCell(tree, this);

        if (gridCell != null) {
            gridCell.setRowspan(rowSpan);
        }

        if (getParent() instanceof GridLayout) {
            GridLayout parent = (GridLayout) getParent();

            parent.removeComponent(this);

            DashboardUtils.removeEmptyLabelsForSpan(parent, gridCell);
            parent.addComponent(this, gridCell.getColumn(), gridCell.getRow(),
                    gridCell.getColumn() + gridCell.getColspan() - 1,
                    gridCell.getRow() + gridCell.getRowspan() - 1);
            DashboardUtils.addEmptyLabelsToLayout(parent, tree);
            DashboardUtils.lockGridCells(parent, tree);
        }
    }

    @Override
    public AbstractOrderedLayout getMainLayout() {
        return contentLayout;
    }

    @Override
    public void setMargin(boolean margin) {
        contentLayout.setMargin(margin);
    }
}