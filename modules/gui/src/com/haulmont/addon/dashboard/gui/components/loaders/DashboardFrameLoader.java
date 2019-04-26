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

import com.haulmont.addon.dashboard.gui.components.DashboardFrame;
import com.haulmont.addon.dashboard.model.Parameter;
import com.haulmont.addon.dashboard.model.paramtypes.*;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.FrameContext;
import com.haulmont.cuba.gui.components.Fragment;
import com.haulmont.cuba.gui.components.sys.FragmentImplementation;
import com.haulmont.cuba.gui.components.sys.FrameImplementation;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.model.impl.ScreenDataImpl;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.ScreenContext;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.sys.FrameContextImpl;
import com.haulmont.cuba.gui.sys.ScreenContextImpl;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;
import com.haulmont.cuba.gui.xml.layout.LayoutLoader;
import com.haulmont.cuba.gui.xml.layout.ScreenXmlLoader;
import com.haulmont.cuba.gui.xml.layout.loaders.ComponentLoaderContext;
import com.haulmont.cuba.gui.xml.layout.loaders.ContainerLoader;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.haulmont.cuba.gui.screen.UiControllerUtils.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DashboardFrameLoader extends ContainerLoader<Fragment> {

    protected String frameId;

    protected ComponentLoader fragmentLoader;
    protected ComponentLoaderContext innerContext;

    protected DashboardFrame dashboardFrame;

    protected Metadata metadata;


    @Override
    public void createComponent() {
        metadata = AppBeans.get(Metadata.class);
        frameId = element.attributeValue("id");

        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);
        WindowInfo windowInfo = windowConfig.getWindowInfo("dashboard$DashboardComponent");

        Fragment fragment = factory.create(Fragment.NAME);
        ScreenFragment controller = createController(windowInfo, fragment, windowInfo.asFragment());
        dashboardFrame = (DashboardFrame) controller;

        // setup screen and controller
        ComponentLoaderContext parentContext = (ComponentLoaderContext) getContext();

        FrameOwner hostController = parentContext.getFrame().getFrameOwner();

        ScreenContext hostScreenContext = getScreenContext(hostController);

        setHostController(controller, hostController);
        setWindowId(controller, windowInfo.getId());
        setFrame(controller, fragment);
        setScreenContext(controller,
                new ScreenContextImpl(windowInfo, parentContext.getOptions(),
                        hostScreenContext.getScreens(),
                        hostScreenContext.getDialogs(),
                        hostScreenContext.getNotifications(),
                        hostScreenContext.getFragments(),
                        hostScreenContext.getUrlRouting())
        );
        setScreenData(controller, new ScreenDataImpl());

        FragmentImplementation fragmentImpl = (FragmentImplementation) fragment;
        fragmentImpl.setFrameOwner(controller);
        fragmentImpl.setId(frameId);

        FrameContext frameContext = new FrameContextImpl(fragment);
        ((FrameImplementation) fragment).setContext(frameContext);

        // load from XML if needed

        if (windowInfo.getTemplate() != null) {
            if (parentContext.getFullFrameId() != null) {
                frameId = parentContext.getFullFrameId() + "." + frameId;
            }

            innerContext = new ComponentLoaderContext(context.getOptions());
            innerContext.setCurrentFrameId(frameId);
            innerContext.setFullFrameId(frameId);
            innerContext.setFrame(fragment);
            innerContext.setParent(parentContext);

            LayoutLoader layoutLoader = beanLocator.getPrototype(LayoutLoader.NAME, innerContext);
            layoutLoader.setLocale(getLocale());
            layoutLoader.setMessagesPack(getMessagesPack());

            ScreenXmlLoader screenXmlLoader = beanLocator.get(ScreenXmlLoader.NAME);

            Element windowElement = screenXmlLoader.load(windowInfo.getTemplate(), windowInfo.getId(),
                    getContext().getParams());

            this.fragmentLoader = layoutLoader.createFragmentContent(fragment, windowElement, frameId);
        }

        this.resultComponent = fragment;
    }

    @Override
    public void loadComponent() {

        if (context.getFrame() != null) {
            resultComponent.setFrame(context.getFrame());
            dashboardFrame.setFrame(context.getFrame());
        }

        assignXmlDescriptor(resultComponent, element);
        loadVisible(resultComponent, element);

        loadStyleName(resultComponent, element);
        loadResponsive(resultComponent, element);

        loadAlign(resultComponent, element);

        loadHeight(resultComponent, element, ComponentsHelper.getComponentHeight(resultComponent));
        loadWidth(resultComponent, element, ComponentsHelper.getComponentWidth(resultComponent));

        loadIcon(resultComponent, element);
        loadCaption(resultComponent, element);
        loadDescription(resultComponent, element);

        loadReferenceName(dashboardFrame, element);
        loadJsonPath(dashboardFrame, element);
        loadParams(dashboardFrame, element);
        loadTimerDelay(dashboardFrame, element);
        loadAssistanceBeanName(dashboardFrame, element);

        if (context.getFrame() != null) {
            resultComponent.setFrame(context.getFrame());
        }

        // propagate init phases

        if (innerContext != null) {
            ComponentLoaderContext parentContext = (ComponentLoaderContext) getContext();

            parentContext.getInjectTasks().addAll(innerContext.getInjectTasks());
            parentContext.getInitTasks().addAll(innerContext.getInitTasks());
            parentContext.getPostInitTasks().addAll(innerContext.getPostInitTasks());
        }

        String currentFrameId = context.getCurrentFrameId();
        context.setCurrentFrameId(frameId);


        fragmentLoader.loadComponent();
        context.setCurrentFrameId(currentFrameId);
    }

    protected <T extends ScreenFragment> T createController(@SuppressWarnings("unused") WindowInfo windowInfo,
                                                            @SuppressWarnings("unused") Fragment fragment,
                                                            Class<T> screenClass) {
        Constructor<T> constructor;
        try {
            constructor = screenClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new DevelopmentException("No accessible constructor for screen class " + screenClass);
        }

        T controller;
        try {
            controller = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to create instance of screen class " + screenClass);
        }

        return controller;
    }

    protected void loadReferenceName(DashboardFrame resultComponent, Element element) {
        String referenceName = element.attributeValue("code");
        if (isNotBlank(referenceName)) {
            resultComponent.setCode(referenceName);
        }
    }

    protected void loadJsonPath(DashboardFrame resultComponent, Element element) {
        String jsonPath = element.attributeValue("jsonPath");
        if (isNotBlank(jsonPath)) {
            resultComponent.setJsonPath(jsonPath);
        }
    }

    protected void loadTimerDelay(DashboardFrame resultComponent, Element element) {
        String timerDelayValue = element.attributeValue("timerDelay");
        if (isNotBlank(timerDelayValue)) {
            resultComponent.setTimerDelay(Integer.parseInt(timerDelayValue));
        }
    }

    protected void loadParams(DashboardFrame resultComponent, Element element) {
        List<Parameter> parameters = element.content().stream()
                .filter(child -> child instanceof DefaultElement &&
                        "parameter".equals(child.getName()))
                .map(xmlParam -> createParameter((DefaultElement) xmlParam))
                .collect(Collectors.toList());

        resultComponent.setXmlParameters(parameters);
    }

    protected Parameter createParameter(DefaultElement xmlParam) {
        String name = xmlParam.attributeValue("name");
        String value = xmlParam.attributeValue("value");
        String type = xmlParam.attributeValue("type");

        Parameter parameter = metadata.create(Parameter.class);
        parameter.setName(name);
        parameter.setParameterValue(createParameterValue(type, value));
        return parameter;
    }

    protected ParameterValue createParameterValue(String type, String value) {
        switch (type) {
            case "boolean":
                return new BooleanParameterValue(Boolean.valueOf(value));
            case "date":
                return new DateParameterValue(Date.valueOf(value));
            case "dateTime":
                return new DateTimeParameterValue(Date.valueOf(value));
            case "decimal":
                return new DecimalParameterValue(new BigDecimal(value));
            case "int":
                return new IntegerParameterValue(Integer.valueOf(value));
            case "long":
                return new LongParameterValue(Long.valueOf(value));
            case "time":
                return new TimeParameterValue(Date.valueOf(value));
            case "uuid":
                return new UuidParameterValue(UUID.fromString(value));
            case "string":
            default:
                return new StringParameterValue(value);
        }
    }

    protected void loadAssistanceBeanName(DashboardFrame resultComponent, Element element) {
        String assistantBeanName = element.attributeValue("assistantBeanName");
        if (isNotBlank(assistantBeanName)) {
            resultComponent.setAssistantBeanName(assistantBeanName);
        }
    }

}
