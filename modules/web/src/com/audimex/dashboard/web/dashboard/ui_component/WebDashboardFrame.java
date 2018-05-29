/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.dashboard.ui_component;

import com.audimex.dashboard.converter.JsonConverter;
import com.audimex.dashboard.entity.DashboardPersist;
import com.audimex.dashboard.gui.components.DashboardFrame;
import com.audimex.dashboard.model.Dashboard;
import com.audimex.dashboard.model.Parameter;
import com.audimex.dashboard.model.param_value_types.StringParameterValue;
import com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame;
import com.haulmont.bali.datastruct.Pair;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.VBoxLayout;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.audimex.dashboard.web.dashboard.frames.canvas.CanvasFrame.VISUAL_MODEL;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class WebDashboardFrame extends AbstractFrame implements DashboardFrame {
    @Inject
    protected VBoxLayout canvasBox;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected JsonConverter jsonConverter;
    @Inject
    protected ResourceLoader resourceLoader;
    @Inject
    protected Metadata metadata;

    protected CanvasFrame canvasFrame;

    protected UUID dashboardId;
    protected String jsonPath;
    protected List<Pair<String, String>> xmlParameters = new ArrayList<>();

    protected Dashboard dashboard;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        refresh();
    }

    @Override
    public void refresh() {
        if (isNotBlank(jsonPath)) {
            dashboard = loadDashboard(jsonPath);
        } else if (dashboardId != null) {
            dashboard = loadDashboard(dashboardId);
        }

        if (dashboard != null) {
            addXmlParameters(dashboard);
            updateCanvasFrame(dashboard);
        }
    }

    public void refresh(Timer source) {
        refresh();
    }

    protected Dashboard loadDashboard(String jsonPath) {
        Resource jsonRes = resourceLoader.getResource(format("classpath:%s", jsonPath));
        if (!jsonRes.exists()) {
            throw new RuntimeException(format("There isn't the json file by the path: %s", jsonPath));
        }

        try {
            String json = IOUtils.toString(jsonRes.getInputStream(), UTF_8);
            return jsonConverter.dashboardFromJson(json);
        } catch (Exception e) {
            throw new RuntimeException(format("Error reading the json by the path: %s", jsonPath), e);
        }
    }

    protected Dashboard loadDashboard(UUID dashboardId) {
        LoadContext<DashboardPersist> loadContext = LoadContext.create(DashboardPersist.class)
                .setId(dashboardId)
                .setView("_local");

        DashboardPersist entity = dataManager.load(loadContext);
        return jsonConverter.dashboardFromJson(entity.getDashboardModel());
    }

    protected void addXmlParameters(Dashboard dashboard) {
        List<Parameter> parameters = dashboard.getParameters();
        parameters.removeAll(getDuplicatesParams(dashboard));
        parameters.addAll(createParametersFromXml());
    }

    protected List<Parameter> getDuplicatesParams(Dashboard dashboard) {
        return dashboard.getParameters().stream()
                .filter(param -> {
                    for (Pair<String, String> xmlParameter : xmlParameters) {
                        if (param.getName().equals(xmlParameter.getFirst())) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    protected List<Parameter> createParametersFromXml() {
        return xmlParameters.stream()
                .map(pair -> {
                    Parameter parameter = metadata.create(Parameter.class);
                    parameter.setName(pair.getFirst());
                    parameter.setParameterValue(new StringParameterValue(pair.getSecond()));
                    return parameter;
                })
                .collect(Collectors.toList());
    }

    protected void updateCanvasFrame(Dashboard dashboard) {
        canvasFrame = (CanvasFrame) openFrame(canvasBox, CanvasFrame.SCREEN_NAME, ParamsMap.of(
                VISUAL_MODEL, dashboard.getVisualModel()
        ));
    }

    @Override
    public UUID getDashboardId() {
        return dashboardId;
    }

    @Override
    public void setDashboardId(String dashboardId) {
        this.dashboardId = UUID.fromString(dashboardId);
    }

    @Override
    public String getJsonPath() {
        return jsonPath;
    }

    @Override
    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public List<Pair<String, String>> getParameters() {
        return xmlParameters;
    }

    @Override
    public void setParameters(List<Pair<String, String>> parameters) {
        xmlParameters = parameters;
    }
}
