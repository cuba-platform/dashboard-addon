/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web;

import com.audimex.dashboard.model.visual_model.DashboardLayout;
import com.audimex.dashboard.web.dashboard.drop_handlers.HorizontalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.drop_handlers.VerticalLayoutDropHandler;
import com.audimex.dashboard.web.dashboard.factory.LayoutFactory;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.cuba.gui.components.Component.Container;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DashboardWebConfig {

    @Bean
    @Scope("prototype")
    public LayoutFactory layoutFactory() {
        return new LayoutFactory() {
            @Override
            public DropHandler getVerticalDropHandler() {
                return verticalDropHandler();
            }

            @Override
            public DropHandler getHorizontalDropHandler() {
                return horizontalDropHandler();
            }
        };
    }

    @Bean
    @Scope("prototype")
    public Container component(DashboardLayout layout) {
        return layoutFactory().createContainer(layout);
    }

    @Bean
    @Scope("prototype")
    public VerticalLayoutDropHandler verticalDropHandler() {
        return new VerticalLayoutDropHandler() {
            @Override
            public Container getContainer(DashboardLayout layout) {
                return component(layout);
            }
        };
    }

    @Bean
    @Scope("prototype")
    public HorizontalLayoutDropHandler horizontalDropHandler() {
        return new HorizontalLayoutDropHandler() {
            @Override
            public Container getContainer(DashboardLayout layout) {
                return component(layout);
            }
        };
    }

//    @Bean
//    @Scope("prototype")
//    public NotDropHandler notDropHandler() {
//        return new NotDropHandler();
//    }
}
