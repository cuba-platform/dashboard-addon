/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.parameter_transformer;

import com.audimex.dashboard.model.param_value_types.EntityParameterValue;
import com.audimex.dashboard.model.param_value_types.ListEntitiesParameterValue;
import com.audimex.dashboard.model.param_value_types.ParameterValue;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.audimex.dashboard.parameter_transformer.ParameterTransformer.NAME;

@Service(NAME)
public class ParameterTransformerImpl implements ParameterTransformer {
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;

    @Override
    public Object transform(ParameterValue parameterValue) {
        if (parameterValue instanceof ListEntitiesParameterValue) {
            return loadEntities((ListEntitiesParameterValue) parameterValue);
        } else if (parameterValue instanceof EntityParameterValue) {
            return loadEntity((EntityParameterValue) parameterValue);
        } else {
            return parameterValue.getValue();
        }
    }

    protected List<Entity> loadEntities(ListEntitiesParameterValue parameter) {
        return parameter.getValue().stream()
                .map(this::loadEntity)
                .collect(Collectors.toList());
    }

    protected Entity loadEntity(EntityParameterValue parameter) {
        Class entityClass = metadata.getClass(parameter.getMetaClassName()).getJavaClass();

        LoadContext loadContext = LoadContext.create(entityClass)
                .setId(UUID.fromString(parameter.getEntityId()))
                .setView(parameter.getViewName());

        return dataManager.load(loadContext);
    }
}
