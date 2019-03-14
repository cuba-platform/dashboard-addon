/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */

package com.haulmont.addon.dashboard.web.dashboard.layouts;

import com.haulmont.addon.dashboard.model.visualmodel.ResponsiveArea;
import com.haulmont.addon.dashboard.model.visualmodel.ResponsiveLayout;
import com.haulmont.cuba.gui.components.Component;
import org.strangeway.responsive.web.components.impl.WebResponsiveColumn;
import org.strangeway.responsive.web.components.impl.WebResponsiveLayout;
import org.strangeway.responsive.web.components.impl.WebResponsiveRow;

import java.util.Iterator;
import java.util.UUID;

public class CanvasResponsiveLayout extends AbstractCanvasLayout {


    public static final String NAME = "canvasResponsiveLayout";

    protected org.strangeway.responsive.web.components.ResponsiveLayout responsiveLayout;

    public CanvasResponsiveLayout init(ResponsiveLayout model) {
        init(model, org.strangeway.responsive.web.components.ResponsiveLayout.class);
        responsiveLayout = (org.strangeway.responsive.web.components.ResponsiveLayout) delegate;
        WebResponsiveRow wrr = new WebResponsiveRow();

        responsiveLayout.setSizeFull();
        responsiveLayout.setSpacing(true);
        responsiveLayout.setFlexible(true);
        wrr.setSizeFull();

        responsiveLayout.addRow(wrr);
        return this;
    }

    @Override
    public org.strangeway.responsive.web.components.ResponsiveLayout getDelegate() {
        return responsiveLayout;
    }

    public void addComponent(Component component) {
        WebResponsiveLayout wrl = (WebResponsiveLayout) responsiveLayout;

        Iterator it = wrl.getOwnComponents().iterator();
        WebResponsiveRow wrr = (WebResponsiveRow) it.next();

        ResponsiveLayout rl = (ResponsiveLayout) model;

        ResponsiveLayout responsiveLayout = (ResponsiveLayout) (((AbstractCanvasLayout) component).getModel()).getParent();
        UUID componentUuid = ((AbstractCanvasLayout) component).getUuid();
        ResponsiveArea responsiveArea = responsiveLayout.getAreas().stream().
                filter(ra -> componentUuid.equals(ra.getComponent().getUuid())).
                findFirst().orElseThrow(() -> new RuntimeException("Can't find layout with uuid " + componentUuid));

        WebResponsiveColumn wrc = new WebResponsiveColumn();
        wrc.setDisplayRules(responsiveArea.getXs() == null ? rl.getXs() : responsiveArea.getXs(),
                responsiveArea.getSm() == null ? rl.getSm() : responsiveArea.getSm(),
                responsiveArea.getMd() == null ? rl.getMd() : responsiveArea.getMd(),
                responsiveArea.getLg() == null ? rl.getLg() : responsiveArea.getLg());
        component.setSizeFull();
        wrc.setContent(component);
        wrr.addColumn(wrc);
    }

}
