/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.audimex.dashboard.model.visual_model.LayoutType;
import com.audimex.dashboard.web.dashboard.drop_handlers.HorizontalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.factory.LayoutFactory;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.cuba.gui.components.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan(basePackages = "com.audimex.dashboard.web")
public class DashboardWebConfig {

    @Bean
    public LayoutFactory layoutFactory() {
        return new LayoutFactory() {
            @Override
            public DropHandler getDropHandler(LayoutType type) {
                return dropHandler((type));
            }
        };
    }

    @Bean
    @Scope("prototype")
    public Component layout(LayoutType type) {
        return layoutFactory().createLayout(type);
    }

    protected DropHandler dropHandler(LayoutType type) {
        switch (type) {
            case VERTICAL_LAYOUT:
                return verticalDropHandler();
            case HORIZONTAL_LAYOUT:
                return horizontalDropHandler();
            case GRID_LAYOUT:
                return null;
            case FRAME_PANEL:
                return null;
            case GRID_AREA:
                return null;
            default:
                return null;
        }
    }


    @Bean
    @Scope("prototype")
    public VerticalLayoutDropHandler verticalDropHandler() {
        return new VerticalLayoutDropHandler() {
            @Override
            public Component getLayout(LayoutType type) {
                return layout(type);
            }
        };
    }

    @Bean
    @Scope("prototype")
    public HorizontalLayoutDropHandler horizontalDropHandler() {
        return new HorizontalLayoutDropHandler() {
            @Override
            public Component getLayout(LayoutType type) {
                return layout(type);
            }
        };
    }
}
