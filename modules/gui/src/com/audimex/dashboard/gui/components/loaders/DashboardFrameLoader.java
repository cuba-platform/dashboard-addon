/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.gui.components.loaders;

import com.audimex.dashboard.gui.components.DashboardFrame;
import com.haulmont.bali.datastruct.Pair;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.xml.layout.*;
import com.haulmont.cuba.gui.xml.layout.loaders.ContainerLoader;
import com.haulmont.cuba.gui.xml.layout.loaders.FrameLoader;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNotBlank;

//partial copy-paste from com.haulmont.cuba.gui.xml.layout.loaders.FrameComponentLoader
public class DashboardFrameLoader extends ContainerLoader<DashboardFrame> {

    protected String frameId;
    protected ComponentLoader frameLoader;
    protected String aClass;

    @Override
    public void createComponent() {
        frameId = element.attributeValue("id");
        aClass = element.attributeValue("class");

        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);
        WindowInfo windowInfo = windowConfig.getWindowInfo("dashboardComponent");
        String template = windowInfo.getTemplate();


        DashboardLayoutLoader layoutLoader = new DashboardLayoutLoader(context, factory, LayoutLoaderConfig.getFrameLoaders());
        layoutLoader.setLocale(getLocale());
        layoutLoader.setMessagesPack(getMessagesPack());

        String currentFrameId = context.getCurrentFrameId();
        context.setCurrentFrameId(frameId);
        try {
            Pair<ComponentLoader, Element> loaderElementPair = layoutLoader.createFrameComponent(template, frameId, context.getParams());
            frameLoader = loaderElementPair.getFirst();
            resultComponent = (DashboardFrame) frameLoader.getResultComponent();
        } finally {
            context.setCurrentFrameId(currentFrameId);
        }
    }

    @Override
    public void loadComponent() {
        if (resultComponent.getMessagesPack() == null) {
            resultComponent.setMessagesPack(messagesPack);
        }

        assignXmlDescriptor(resultComponent, element);
        loadVisible(resultComponent, element);

        loadStyleName(resultComponent, element);
        loadResponsive(resultComponent, element);

        loadAlign(resultComponent, element);

        loadHeight(resultComponent, element, ComponentsHelper.getComponentHeigth(resultComponent));
        loadWidth(resultComponent, element, ComponentsHelper.getComponentWidth(resultComponent));

        loadIcon(resultComponent, element);
        loadCaption(resultComponent, element);
        loadDescription(resultComponent, element);

        loadReferenceName(resultComponent, element);
        loadJsonPath(resultComponent, element);
        loadParams(resultComponent, element);

        if (context.getFrame() != null) {
            resultComponent.setFrame(context.getFrame());
        }

        String currentFrameId = context.getCurrentFrameId();
        context.setCurrentFrameId(frameId);


        frameLoader.loadComponent();
        context.setCurrentFrameId(currentFrameId);
    }

    protected void loadReferenceName(DashboardFrame resultComponent, Element element) {
        String referenceName = element.attributeValue("referenceName");
        if (isNotBlank(referenceName)) {
            resultComponent.setReferenceName(referenceName);
        }
    }

    protected void loadJsonPath(DashboardFrame resultComponent, Element element) {
        String jsonPath = element.attributeValue("jsonPath");
        if (isNotBlank(jsonPath)) {
            resultComponent.setJsonPath(jsonPath);
        }
    }

    protected void loadParams(DashboardFrame resultComponent, Element element) {
        List<Pair<String, String>> parameters = (List<Pair<String, String>>) element.content().stream()
                .filter(child -> child instanceof DefaultElement &&
                        "parameter".equals(((DefaultElement) child).getName()))
                .map(child -> {
                    String name = ((DefaultElement) child).attributeValue("name");
                    String value = ((DefaultElement) child).attributeValue("value");
                    return new Pair<>(name, value);
                })
                .collect(Collectors.toList());

        resultComponent.setParameters(parameters);
    }

    protected class DashboardLayoutLoader extends LayoutLoader {

        protected DashboardLayoutLoader(ComponentLoader.Context context, ComponentsFactory factory, LayoutLoaderConfig config) {
            super(context, factory, config);
        }

        public Pair<ComponentLoader, Element> createFrameComponent(String resourcePath, String id, Map<String, Object> params) {
            ScreenXmlLoader screenXmlLoader = AppBeans.get(ScreenXmlLoader.class);
            Element element = screenXmlLoader.load(resourcePath, id, params);

            //added replace for class-control
            //todo: check work
            if (isNotBlank(aClass) && isNotBlank(element.attributeValue("class"))) {
                element.addAttribute("class", aClass);
            }

            ComponentLoader loader = getLoader(element);
            FrameLoader frameLoader = (FrameLoader) loader;
            frameLoader.setFrameId(id);

            loader.createComponent();

            return new Pair<>(loader, element);
        }
    }

}
