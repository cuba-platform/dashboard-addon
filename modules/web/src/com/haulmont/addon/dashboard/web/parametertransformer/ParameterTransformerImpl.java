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

package com.haulmont.addon.dashboard.web.parametertransformer;

import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.model.paramtypes.*;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.chile.core.datatypes.impl.EnumClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.haulmont.addon.dashboard.model.ParameterType.*;
import static com.haulmont.addon.dashboard.web.parametertransformer.ParameterTransformer.NAME;

@Component(NAME)
public class ParameterTransformerImpl implements ParameterTransformer {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Override
    public Object transform(ParameterValue parameterValue) {
        if (parameterValue == null) {
            return null;
        }
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
                .setId(java.util.UUID.fromString(parameter.getEntityId()))
                .setView(parameter.getViewName());

        return dataManager.load(loadContext);
    }

    public boolean compareParameterTypes(ParameterType parameterType, Field field) {
        if (parameterType == ENTITY) {
            return Entity.class.isAssignableFrom(field.getType());
        } else if (parameterType == LIST_ENTITY) {
            return List.class.isAssignableFrom(field.getType());
        } else if (parameterType == ENUM) {
            return field.isEnumConstant();
        } else if (parameterType == DATE) {
            return Date.class.isAssignableFrom(field.getType());
        } else if (parameterType == DATETIME) {
            return Date.class.isAssignableFrom(field.getType());
        } else if (parameterType == TIME) {
            return Date.class.isAssignableFrom(field.getType());
        } else if (parameterType == UUID) {
            return UUID.class.isAssignableFrom(field.getType());
        } else if (parameterType == INTEGER) {
            return Integer.class.isAssignableFrom(field.getType()) || int.class.isAssignableFrom(field.getType());
        } else if (parameterType == STRING) {
            return String.class.isAssignableFrom(field.getType());
        } else if (parameterType == DECIMAL) {
            return BigDecimal.class.isAssignableFrom(field.getType());
        } else if (parameterType == BOOLEAN) {
            return Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType());
        } else if (parameterType == LONG) {
            return Long.class.isAssignableFrom(field.getType()) || long.class.isAssignableFrom(field.getType());
        }
        return false;
    }

    public ParameterType getParameterType(Field field) {
        if (Entity.class.isAssignableFrom(field.getType())) {
            return ParameterType.ENTITY;
        } else if (List.class.isAssignableFrom(field.getType())) {
            return ParameterType.LIST_ENTITY;
        } else if (field.isEnumConstant()) {
            return ParameterType.ENUM;
        } else if (Date.class.isAssignableFrom(field.getType())) {
            return ParameterType.DATETIME;
        } else if (UUID.class.isAssignableFrom(field.getType())) {
            return ParameterType.UUID;
        } else if (Integer.class.isAssignableFrom(field.getType()) || int.class.isAssignableFrom(field.getType())) {
            return ParameterType.INTEGER;
        } else if (String.class.isAssignableFrom(field.getType())) {
            return ParameterType.STRING;
        } else if (BigDecimal.class.isAssignableFrom(field.getType())) {
            return ParameterType.DECIMAL;
        } else if (Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType())) {
            return ParameterType.BOOLEAN;
        } else if (Long.class.isAssignableFrom(field.getType()) || long.class.isAssignableFrom(field.getType())) {
            return ParameterType.LONG;
        }
        return ParameterType.STRING;
    }

    @Override
    public ParameterValue createParameterValue(Field field, ScreenFragment widgetFrame) {
        ParameterValue parameterValue = null;
        ParameterType parameterType = getParameterType(field);
        try {
            if (parameterType == ENTITY) {
                Entity entity = (Entity) FieldUtils.readField(field, widgetFrame, true);
                if (entity != null) {
                    WidgetParam ann = field.getAnnotation(WidgetParam.class);
                    parameterValue = new EntityParameterValue(field.getClass().toString(), entity.getId().toString(), ann.viewName());
                }
            } else if (parameterType == LIST_ENTITY) {
                List<Entity> listEntity = (List) FieldUtils.readField(field, widgetFrame, true);
                WidgetParam ann = field.getAnnotation(WidgetParam.class);
                if (listEntity != null) {
                    List<EntityParameterValue> resultList = listEntity.stream()
                            .map(entity -> new EntityParameterValue(field.getClass().toString(), entity.getId().toString(), ann.viewName()))
                            .collect(Collectors.toList());
                    parameterValue = new ListEntitiesParameterValue(resultList);
                }
            } else if (parameterType == ENUM) {
                EnumClass<String> rawValue = (EnumClass) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    String enumClassName = rawValue.getClass().toString();
                    parameterValue = new EnumParameterValue(enumClassName);
                }
            } else if (parameterType == DATE) {
                Date rawValue = (Date) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new DateParameterValue(rawValue);
                }
            } else if (parameterType == DATETIME) {
                Date rawValue = (Date) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new DateParameterValue(rawValue);
                }
            } else if (parameterType == TIME) {
                Date rawValue = (Date) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new DateParameterValue(rawValue);
                }
            } else if (parameterType == UUID) {
                UUID rawValue = (UUID) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new UuidParameterValue(rawValue);
                }
            } else if (parameterType == INTEGER) {
                Integer rawValue = (Integer) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new IntegerParameterValue(rawValue);
                }
            } else if (parameterType == STRING) {
                String rawValue = (String) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new StringParameterValue(rawValue);
                }
            } else if (parameterType == DECIMAL) {
                BigDecimal rawValue = (BigDecimal) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new DecimalParameterValue(rawValue);
                }
            } else if (parameterType == BOOLEAN) {
                Boolean rawValue = (Boolean) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new BooleanParameterValue(rawValue);
                }
            } else if (parameterType == LONG) {
                Long rawValue = (Long) FieldUtils.readField(field, widgetFrame, true);
                if (rawValue != null) {
                    parameterValue = new LongParameterValue(rawValue);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error on parameter init", e);
        }
        return parameterValue;
    }

    @Override
    @Nullable
    public ParameterValue createParameterValue(Object obj) {
        if (obj instanceof Boolean) {
            return new BooleanParameterValue((Boolean) obj);
        } else if (obj instanceof Date) {
            return new DateParameterValue((Date) obj);
        } else if (obj instanceof BigDecimal) {
            return new DecimalParameterValue((BigDecimal) obj);
        } else if (obj instanceof Long) {
            return new LongParameterValue((Long) obj);
        } else if (obj instanceof Integer) {
            return new IntegerParameterValue((Integer) obj);
        } else if (obj instanceof String) {
            return new StringParameterValue((String) obj);
        } else if (obj instanceof UUID) {
            return new UuidParameterValue((UUID) obj);
        } else if (obj instanceof EnumClass) {
            return new EnumParameterValue(obj.getClass().toString());
        } else if (obj instanceof Entity) {
            return new EntityParameterValue(metadata.getClassNN(obj.getClass()).getName(), ((Entity) obj).getId().toString(), null);
        } else if (obj instanceof List) {
            List<?> list = (List) obj;
            List<EntityParameterValue> entityList = list.stream()
                    .filter(t -> t instanceof Entity)
                    .map(entity -> new EntityParameterValue(metadata.getClassNN(obj.getClass()).getName(), ((Entity) entity).getId().toString(), null))
                    .collect(Collectors.toList());
            return new ListEntitiesParameterValue(entityList);
        }
        return null;
    }

}
