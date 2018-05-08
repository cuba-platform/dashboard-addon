/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.screens;

import com.google.common.collect.Iterables;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//todo remove this class
public class Screen extends AbstractWindow {
    @Inject
    private DDVerticalLayout dashboard;
    @Inject
    private ComponentsFactory factory;

    @Override
    public void init(Map<String, Object> params) {

        dashboard.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();
                DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();

                Component sourceLayout = t.getSourceComponent();
                Component tComponent = t.getTransferableComponent();

                DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();
                VerticalDropLocation loc = details.getDropLocation();

                if (tComponent == null) {
                    return;
                }

                int indexTo = details.getOverIndex();
                int indexFrom = targetLayout.indexOf(tComponent);

                Component componentToAdd;

                if (sourceLayout == targetLayout) {
                    componentToAdd = tComponent;
                    if (indexFrom == indexTo) {
                        return;
                    }
                    targetLayout.remove(tComponent);
                    if (indexTo > indexFrom) {
                        indexTo--;
                    }
                    if (indexTo == -1) {
                        targetLayout.add(tComponent, indexFrom);
                    }
                } else {
                    componentToAdd = createDashboardElement(tComponent);
                    if (indexTo == -1) {
                        targetLayout.add(componentToAdd, targetLayout.getOwnComponents().size());
                    }
                }

                if (indexTo != -1) {
                    if (loc == VerticalDropLocation.MIDDLE || loc == VerticalDropLocation.BOTTOM) {
                        indexTo++;
                    }
                    targetLayout.add(componentToAdd, indexTo);
                }

                updateDashboardComponents(targetLayout);
            }

            @Override
            public AcceptCriterion getCriterion() {
                return AcceptCriterion.ACCEPT_ALL;
            }
        });
    }

    public Component createDashboardElement(Component component) {
        GroupBoxLayout groupBox = factory.createComponent(GroupBoxLayout.class);
        groupBox.setWidth("100%");

        HBoxLayout layout = factory.createComponent(HBoxLayout.class);
        layout.setWidth("100%");
        layout.setSpacing(true);

        Label countLabel = factory.createComponent(Label.class);
        countLabel.setId("countLabel");
        countLabel.setWidth("30px");

        Label titleLabel = factory.createComponent(Label.class);
        titleLabel.setValue(((Button) component).getCaption());
        titleLabel.setWidth("60px");

        LookupField lookupField = factory.createComponent(LookupField.class);

        Button deleteButton = factory.createComponent(Button.class);
        deleteButton.setIcon("font-icon:TIMES");

        BaseAction action = new BaseAction("remove") {
            @Override
            public void actionPerform(Component component) {
                HBoxLayout hBox = (HBoxLayout) component.getParent();
                GroupBoxLayout groupBox = (GroupBoxLayout) hBox.getParent();
                DDVerticalLayout ddLayout = (DDVerticalLayout) groupBox.getParent();
                ddLayout.remove(groupBox);
                updateDashboardComponents(ddLayout);
            }
        };
        action.setCaption("");
        deleteButton.setAction(action);

        layout.add(countLabel);
        layout.add(titleLabel);
        layout.add(lookupField);
        layout.expand(lookupField);
        layout.add(deleteButton);
        groupBox.add(layout);

        return groupBox;
    }

    public void updateDashboardComponents(DDVerticalLayout layout) {
        List<Component> components = new ArrayList<>(layout.getOwnComponents());
        int count = 0;
        for (Component component : components) {
            GroupBoxLayout groupBox = (GroupBoxLayout) component;
            HBoxLayout hBoxLayout = (HBoxLayout) Iterables.get(groupBox.getComponents(), 0);
            Label label = (Label)  Iterables.get(hBoxLayout.getComponents(), 0);
            label.setValue(++count);
        }
    }
}