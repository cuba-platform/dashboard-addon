package com.audimex.dashboard.gui.components;

import com.audimex.dashboard.model.visual_model.LayoutType;
import com.haulmont.cuba.gui.components.Button;

import java.util.UUID;

public interface PaletteButton extends Button {
    String NAME = "paletteButton";

    LayoutType getLayoutType();

    void setLayoutType(LayoutType layoutType);

    UUID getWidgetUuid();

    void setWidgetUuid(UUID widgetUuid);
}
