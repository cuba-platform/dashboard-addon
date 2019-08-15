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

package com.haulmont.addon.dashboard.gui.components.loaders;

import com.haulmont.bali.datastruct.Pair;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;
import com.haulmont.cuba.gui.xml.layout.LayoutLoader;
import com.haulmont.cuba.gui.xml.layout.ScreenXmlLoader;
import com.haulmont.cuba.gui.xml.layout.loaders.ComponentLoaderContext;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component(DashboardLayoutLoader.NAME)
public class DashboardLayoutLoader extends LayoutLoader {

    public static final String NAME = "dashboard_LayoutLoader";

    protected DashboardLayoutLoader(ComponentLoader.Context context) {
        super(context);
    }

    public Pair<ComponentLoader, Element> createFrameComponent(String resourcePath, String id, Map<String, Object> params) {
        ScreenXmlLoader screenXmlLoader = AppBeans.get(ScreenXmlLoader.class);
        Element element = screenXmlLoader.load(resourcePath, id, params);

        ComponentLoader loader = getLoader(element);

        ComponentLoaderContext ctx = (ComponentLoaderContext) loader.getContext();
        ctx.setCurrentFrameId(id);

        loader.createComponent();

        return new Pair<>(loader, element);
    }
}
