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
 *
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;


import com.haulmont.addon.dashboard.model.visualmodel.DashboardLayout;
import com.haulmont.addon.dashboard.web.DashboardStyleConstants;
import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentContainer;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.LayoutClickNotifier;
import com.haulmont.cuba.web.gui.components.WebCssLayout;
import com.haulmont.cuba.web.widgets.CubaCssActionsLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Layout;

import javax.inject.Inject;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractCanvasLayout extends WebCssLayout implements CanvasLayout {
    protected ComponentContainer delegate;
    protected HBoxLayout buttonsPanel;
    protected UUID uuid;
    protected DashboardLayout model;

    @Inject
    protected UiComponents components;

    public AbstractCanvasLayout init(DashboardLayout model, ComponentContainer delegate) {
        this.delegate = delegate;
        this.model = model;
        buttonsPanel = components.create(HBoxLayout.class);
        this.delegate.setSizeFull();
        super.add(this.delegate);
        super.add(buttonsPanel);
        super.addStyleName(DashboardStyleConstants.DASHBOARD_WIDGET);
        this.unwrap(CubaCssActionsLayout.class).setId(model.getId().toString());
        return this;
    }

    public <T extends ComponentContainer> AbstractCanvasLayout init(DashboardLayout model, Class<T> componentClass) {
        return init(model, components.create(componentClass));
    }

    @Override
    public ComponentContainer getDelegate() {
        return delegate;
    }

    public void addComponent(Component component) {
        delegate.add(component);
    }

    @Override
    public HBoxLayout getButtonsPanel() {
        return buttonsPanel;
    }

    public void setButtonsPanel(HBoxLayout buttonsPanel) {
        this.buttonsPanel = buttonsPanel;
    }

    @Override
    public Subscription addLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        return ((LayoutClickNotifier) getDelegate()).addLayoutClickListener(listener);
    }

    @Override
    public void removeLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        ((LayoutClickNotifier) getDelegate()).removeLayoutClickListener(listener);
    }

    @Override
    public void setWeight(int weight) {
        Layout unwrapThis = this.unwrap(Layout.class);
        HasComponents parent = unwrapThis.getParent();

        if (parent instanceof AbstractOrderedLayout) {
            for (com.vaadin.ui.Component child : parent) {
                if (unwrapThis.equals(child)) {
                    ((AbstractOrderedLayout) parent).setExpandRatio(unwrapThis, weight);
                } else if (((AbstractOrderedLayout) parent).getExpandRatio(child) == 0) {
                    ((AbstractOrderedLayout) parent).setExpandRatio(child, 1);
                }
            }
        }
    }

    @Override
    public int getWeight() {
        Layout unwrapThis = this.unwrap(Layout.class);
        HasComponents parent = unwrapThis.getParent();

        if (parent instanceof AbstractOrderedLayout) {
            int weight = (int) ((AbstractOrderedLayout) parent).getExpandRatio(unwrapThis);
            return weight > 0 ? weight : 1;
        } else {
            return 1;
        }
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public DashboardLayout getModel() {
        return model;
    }
}
