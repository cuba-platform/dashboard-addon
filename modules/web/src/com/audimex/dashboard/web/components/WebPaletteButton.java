package com.audimex.dashboard.web.components;

import com.audimex.dashboard.gui.components.PaletteButton;
import com.audimex.dashboard.model.visual_model.LayoutType;
import com.haulmont.cuba.web.gui.components.WebButton;

import java.util.UUID;

public class WebPaletteButton extends WebButton implements PaletteButton {
    protected LayoutType layoutType;
    protected UUID widgetUuid;

    public WebPaletteButton() {

    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public UUID getWidgetUuid() {
        return widgetUuid;
    }

    public void setWidgetUuid(UUID widgetUuid) {
        this.widgetUuid = widgetUuid;
    }
}
