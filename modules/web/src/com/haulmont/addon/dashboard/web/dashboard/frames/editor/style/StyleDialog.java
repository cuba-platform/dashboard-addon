/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.haulmont.addon.dashboard.web.dashboard.frames.editor.style;

import com.haulmont.addon.dashboard.model.visualmodel.SizeUnit;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class StyleDialog extends AbstractWindow {
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
        this.close(COMMIT_ACTION_ID);
    }

    public void cancel() {
        this.close(CLOSE_ACTION_ID);
    }
}