/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */
package com.audimex.dashboard.web.palette;

import com.audimex.dashboard.web.ComponentStructure;
import com.audimex.dashboard.web.drophandlers.DDVerticalLayoutDropHandler;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.ui.*;
import fi.jasoft.dragdroplayouts.DDHorizontalLayout;
import fi.jasoft.dragdroplayouts.DDVerticalLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;

import javax.inject.Inject;
import java.util.Map;

public class PaletteWindow extends AbstractWindow {

    @Inject
    private VBoxLayout containers;

    @Inject
    private VBoxLayout dropLayout;

    @Inject
    private VBoxLayout treeLayout;

    protected Tree tree;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Layout containersLayout = (Layout) WebComponentsHelper.unwrap(containers);
        Layout dropLayoutContainer = (Layout) WebComponentsHelper.unwrap(dropLayout);
        Layout treeLayoutContainer = (Layout) WebComponentsHelper.unwrap(treeLayout);

        DDVerticalLayout containersDraggableLayout = new DDVerticalLayout();
        containersDraggableLayout.setDragMode(LayoutDragMode.CLONE);
        containersDraggableLayout.setSpacing(true);

        DDVerticalLayout dropDraggableLayout = new DDVerticalLayout();
        dropDraggableLayout.setDragMode(LayoutDragMode.CLONE);

        tree = new Tree();
        tree.setSizeFull();

        dropDraggableLayout.setSpacing(true);
        dropDraggableLayout.setMargin(true);
        dropDraggableLayout.setDropHandler(new DDVerticalLayoutDropHandler());
        drawTree(dropDraggableLayout, null);

        ComponentStructure componentStructure = new ComponentStructure(dropDraggableLayout, null);

        ((DDVerticalLayoutDropHandler) dropDraggableLayout.getDropHandler()).setStructure(componentStructure);
        ((DDVerticalLayoutDropHandler) dropDraggableLayout.getDropHandler()).addStructureChangeListener(() -> {
            tree.removeAllItems();
            drawTree(dropDraggableLayout, null);
        });
        dropDraggableLayout.setSizeFull();
        dropDraggableLayout.setStyleName("dd-bordering");

        Button verticalLayoutButton = new Button("Vertical");
        verticalLayoutButton.setId("verticalLayout");
        verticalLayoutButton.setWidth("100%");
        verticalLayoutButton.setHeight("50px");
        Button horizontalLayoutButton = new Button("Horizontal");
        horizontalLayoutButton.setId("horizontalLayout");
        horizontalLayoutButton.setWidth("100%");
        horizontalLayoutButton.setHeight("50px");
        Button widgetButton = new Button("Widget");
        widgetButton.setId("widgetPanel");
        widgetButton.setWidth("100%");
        widgetButton.setHeight("50px");

        containersDraggableLayout.addComponent(verticalLayoutButton);
        containersDraggableLayout.addComponent(horizontalLayoutButton);
        containersDraggableLayout.addComponent(widgetButton);

        dropLayoutContainer.addComponent(dropDraggableLayout);
        containersLayout.addComponent(containersDraggableLayout);
        treeLayoutContainer.addComponent(tree);
    }

    private void drawTree(Component component, Component parent) {
        tree.addItem(component.toString());

        if (parent != null) {
            tree.setParent(component.toString(), parent.toString());
        }
        tree.expandItem(component.toString());

        if (component instanceof DDVerticalLayout) {
            tree.setItemCaption(component.toString(), "Vertical");
        } else if (component instanceof DDHorizontalLayout) {
            tree.setItemCaption(component.toString(), "Horizontal");
        } else {
            tree.setItemCaption(component.toString(), "Widget");
        }

        if (component instanceof AbstractOrderedLayout) {
            tree.setChildrenAllowed(component.toString(),
                    (((AbstractOrderedLayout) component).getComponentCount() != 0)
            );

            int count = ((AbstractOrderedLayout) component).getComponentCount();
            if (count != 0) {
                for (int i=0; i<count; i++) {
                    drawTree(((AbstractOrderedLayout) component).getComponent(i), component);
                }
            }
        } else {
            tree.setChildrenAllowed(component.toString(), false);
        }
    }
}