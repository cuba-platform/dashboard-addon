/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.style;

import com.haulmont.addon.dashboard.model.visualmodel.SizeUnit;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleDialog extends AbstractWindow {

    private static final Pattern DIMENSION_PATTERN = Pattern.compile("^(\\d+)(px|%)$");

    public static final String SCREEN_NAME = "dashboard$StyleDialog";
    public static final String STYLENAME = "STYLENAME";
    public static final String HEIGHT = "HEIGHT";
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT_UNITS = "HEIGHT_UNITS";
    public static final String WIDTH_UNITS = "WIDTH_UNITS";

    @Inject
    private TextField styleName;

    @Inject
    private TextField height;
    @Inject
    private TextField width;
    @Inject
    private LookupField widthUnits;
    @Inject
    private LookupField heightUnits;
    @Inject
    private CheckBox autoHeight;
    @Inject
    private CheckBox autoWidth;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        styleName.setValue(params.get(STYLENAME));
        width.setValue(params.get(WIDTH));
        widthUnits.setOptionsMap(getSizeUnitsValues());
        widthUnits.setValue(params.get(WIDTH_UNITS));
        height.setValue(params.get(HEIGHT));
        heightUnits.setOptionsMap(getSizeUnitsValues());
        heightUnits.setValue(params.get(HEIGHT_UNITS));

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

    private void addSizeAutoFieldListener(CheckBox sizeAutoCheckbox, TextField field, LookupField unitsField) {
        sizeAutoCheckbox.addValueChangeListener(e -> {
            Boolean value = (Boolean) e.getValue();
            if (value) {
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

    private void checkFieldInput(String text, TextField field, LookupField unitsField) {
        Matcher matcher = DIMENSION_PATTERN.matcher(text);
        if (matcher.matches()) {
            String value = matcher.group(1);
            SizeUnit sizeUnit = SizeUnit.fromId(matcher.group(2));
            field.setValue(value);
            unitsField.setValue(sizeUnit);
        }
    }

    protected Map<String, Object> getSizeUnitsValues() {
        Map<String, Object> sizeUnitsMap = new HashMap<>();
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
            this.close(COMMIT_ACTION_ID);
        }
    }

    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }
}