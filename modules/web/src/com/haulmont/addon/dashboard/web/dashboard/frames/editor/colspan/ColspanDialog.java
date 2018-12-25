/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.colspan;

import com.haulmont.addon.dashboard.model.visualmodel.GridArea;
import com.haulmont.addon.dashboard.model.visualmodel.GridCellLayout;
import com.haulmont.addon.dashboard.model.visualmodel.GridLayout;
import com.haulmont.addon.dashboard.utils.DashboardLayoutUtils;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@UiController("dashboard$ColspanDialog")
@UiDescriptor("colspan-dialog.xml")
public class ColspanDialog extends AbstractWindow {
    public static final String WIDGET = "WIDGET";

    @Inject
    protected HBoxLayout sliderBox;

    @Inject
    private Label<String> leftLabel;

    @Inject
    private Label<String> rightLabel;

    @Inject
    private Metadata metadata;

    private Slider colSpanSlider = null;
    private Slider rowSpanSlider = null;
    private GridCellLayout layout;
    private GridLayout parent;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        layout = (GridCellLayout) params.get(WIDGET);
        int cols = layout.getColSpan() + 1;
        int rows = layout.getRowSpan() + 1;
        parent = (GridLayout) layout.getParent();

        colSpanSlider = new Slider();
        colSpanSlider.setWidth("100%");
        colSpanSlider.setMin(1);
        int maxColSpan = DashboardLayoutUtils.availableColumns(parent, layout);
        int maxRowSpan = DashboardLayoutUtils.availableRows(parent, layout);

        colSpanSlider.setMax(maxColSpan);

        colSpanSlider.addValueChangeListener(event ->
                leftLabel.setValue(String.format(
                        getMessage("dashboard.columnSpan"),
                        colSpanSlider.getValue().intValue(),
                        maxColSpan)
                )
        );
        leftLabel.setValue(String.format(
                getMessage("dashboard.columnSpan"),
                cols,
                maxColSpan
        ));
        colSpanSlider.setResolution(0);

        colSpanSlider.setValue((double) cols);
        sliderBox.unwrap(Layout.class).addComponent(colSpanSlider);
        colSpanSlider.focus();

        rowSpanSlider = new Slider();
        rowSpanSlider.setWidth("100%");
        rowSpanSlider.setMin(1);
        rowSpanSlider.setMax(maxRowSpan);
        rowSpanSlider.addValueChangeListener(event ->
                rightLabel.setValue(String.format(
                        getMessage("dashboard.rowSpan"),
                        rowSpanSlider.getValue().intValue(),
                        maxRowSpan))
        );
        rightLabel.setValue(String.format(getMessage(
                "dashboard.rowSpan"),
                rows,
                maxRowSpan
        ));
        rowSpanSlider.setResolution(0);
        rowSpanSlider.setValue((double) rows);

        sliderBox.unwrap(Layout.class).addComponent(rowSpanSlider);
    }

    public void apply() {
        layout.setColSpan(colSpanSlider.getValue().intValue() - 1);
        layout.setRowSpan(rowSpanSlider.getValue().intValue() - 1);
        GridArea gridArea = parent.getGridArea(layout.getColumn(), layout.getRow());
        gridArea.setCol2(gridArea.getCol() + layout.getColSpan());
        gridArea.setRow2(gridArea.getRow() + layout.getRowSpan());
        reorderGridAreas();
        this.close(new StandardCloseAction(COMMIT_ACTION_ID));
    }

    private void reorderGridAreas() {
        Set<GridArea> gridAreas = new HashSet<>();
        for (int col = 0; col < parent.getColumns(); col++) {
            for (int row = 0; row < parent.getRows(); row++) {
                GridArea gridArea = parent.getGridArea(col, row);
                if (gridArea == null) {
                    gridArea = metadata.create(GridArea.class);
                    gridArea.setRow(row);
                    gridArea.setCol(col);
                    GridCellLayout gcl = metadata.create(GridCellLayout.class);
                    gcl.setRow(row);
                    gcl.setColumn(col);
                    gridArea.setComponent(gcl);
                    gcl.setParent(parent);
                    gridAreas.add(gridArea);
                } else {
                    gridAreas.add(gridArea);
                }
            }
        }
        parent.setAreas(gridAreas);
    }

    public void cancel() {
        close(new StandardCloseAction(CLOSE_ACTION_ID));
    }
}