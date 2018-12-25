/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.style;

import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.model.visualmodel.SizeUnit;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UiController("dashboard$StyleDialog")
@UiDescriptor("style-dialog.xml")
public class StyleDialog extends AbstractWindow {

    private static final Pattern DIMENSION_PATTERN = Pattern.compile("^(\\d+)(px|%)$");

    public static final String WIDGET = "WIDGET";

    @Inject
    private TextField<String> styleName;

    @Inject
    private TextField<Integer> height;
    @Inject
    private TextField<Integer> width;
    @Inject
    private LookupField<SizeUnit> widthUnits;
    @Inject
    private LookupField<SizeUnit> heightUnits;
    @Inject
    private CheckBox autoHeight;
    @Inject
    private CheckBox autoWidth;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        DashboardLayout dashboardLayout = (DashboardLayout) params.get(WIDGET);
        styleName.setValue(dashboardLayout.getStyleName());
        width.setValue(dashboardLayout.getWidth());
        widthUnits.setOptionsMap(getSizeUnitsValues());
        widthUnits.setValue(dashboardLayout.getWidthUnit());
        height.setValue(dashboardLayout.getHeight());
        heightUnits.setOptionsMap(getSizeUnitsValues());
        heightUnits.setValue(dashboardLayout.getHeightUnit());

        width.addTextChangeListener(e -> {
            String text = e.getText();
            checkFieldInput(text, width, widthUnits);
            setSizeAutoVisible(autoWidth, height, heightUnits);
        });

        height.addTextChangeListener(e -> {
            String text = e.getText();
            checkFieldInput(text, height, heightUnits);
            setSizeAutoVisible(autoHeight, height, heightUnits);
        });

        setSizeAutoVisible(autoWidth, width, widthUnits);
        setSizeAutoVisible(autoHeight, height, heightUnits);

        addSizeAutoFieldListener(autoWidth, width, widthUnits);
        addSizeAutoFieldListener(autoHeight, height, heightUnits);
    }

    private void addSizeAutoFieldListener(CheckBox sizeAutoCheckbox, TextField<Integer> field, LookupField<SizeUnit> unitsField) {
        sizeAutoCheckbox.addValueChangeListener(e -> {
            Boolean value = e.getValue();
            if (BooleanUtils.isTrue(value)) {
                field.setValue(-1);
                unitsField.setValue(SizeUnit.PIXELS);
            } else {
                field.setValue(100);
                unitsField.setValue(SizeUnit.PERCENTAGE);
            }
            field.setVisible(!value);
            unitsField.setVisible(!value);
        });
    }

    private void setSizeAutoVisible(CheckBox sizeAutoCheckbox, TextField field, LookupField unitsField) {
        boolean autoSize = isAutoSize(field, unitsField);
        field.setVisible(!autoSize);
        unitsField.setVisible(!autoSize);
        sizeAutoCheckbox.setValue(autoSize);
    }

    private boolean isAutoSize(TextField field, LookupField unitsField) {
        return field.getValue() != null && field.getValue().equals(-1) && SizeUnit.PIXELS == unitsField.getValue();
    }

    //TODO fix text input
    private void checkFieldInput(String text, TextField field, LookupField<SizeUnit> unitsField) {
        Matcher matcher = DIMENSION_PATTERN.matcher(text);
        if (matcher.matches()) {
            String value = matcher.group(1);
            SizeUnit sizeUnit = SizeUnit.fromId(matcher.group(2));
            field.setValue(value);
            unitsField.setValue(sizeUnit);
        }
    }

    protected Map<String, SizeUnit> getSizeUnitsValues() {
        Map<String, SizeUnit> sizeUnitsMap = new HashMap<>();
        for (SizeUnit sizeUnit : SizeUnit.values()) {
            sizeUnitsMap.put(messages.getMessage(sizeUnit), sizeUnit);
        }
        return sizeUnitsMap;
    }

    public String getLayoutStyleName() {
        return styleName.getValue();
    }

    public Integer getLayoutHeight() {
        return height.getValue();
    }

    public SizeUnit getLayoutHeightUnit() {
        return heightUnits.getValue();
    }

    public Integer getLayoutWidth() {
        return width.getValue();
    }

    public SizeUnit getLayoutWidthUnit() {
        return widthUnits.getValue();
    }

    public void apply() {
        if (validateAll()) {
            this.close(new StandardCloseAction(COMMIT_ACTION_ID));
        }
    }

    public void cancel() {
        this.close(new StandardCloseAction(CLOSE_ACTION_ID));
    }
}