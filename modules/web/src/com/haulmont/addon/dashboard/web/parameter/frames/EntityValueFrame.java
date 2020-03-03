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

package com.haulmont.addon.dashboard.web.parameter.frames;

import com.haulmont.addon.dashboard.model.paramtypes.EntityParameterValue;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UiController("dashboard$EntityValueFrame")
@UiDescriptor("entity-value-frame.xml")
public class EntityValueFrame extends AbstractFrame implements ValueFrame {

    @Inject
    protected LookupField<MetaClass> metaClassLookup;

    @Inject
    protected LookupField<Entity> entitiesLookup;

    @Inject
    protected LookupField<String> viewLookup;

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        loadAllPersistentClasses();
        selectIfExist((EntityParameterValue) params.get(VALUE));
        metaClassLookup.addValueChangeListener(e -> metaClassValueChanged(e.getValue()));
    }

    @Override
    public ParameterValue getValue() {
        EntityParameterValue value = new EntityParameterValue();
        MetaClass metaClass = metaClassLookup.getValue();

        if (metaClass != null) {
            value.setMetaClassName(metaClass.getName());

            Entity entity = entitiesLookup.getValue();
            value.setEntityId(entity == null ? null : entity.getId().toString());

            String viewName = viewLookup.getValue();
            value.setViewName(viewName);
        }

        return value;
    }

    protected void loadAllPersistentClasses() {
        List<MetaClass> metaClasses = new ArrayList<>(metadata.getTools().getAllPersistentMetaClasses());
        metaClassLookup.setOptionsList(metaClasses);
    }

    public void selectIfExist(EntityParameterValue value) {
        if (value != null && isNotBlank(value.getMetaClassName())) {
            String metaClassName = value.getMetaClassName();

            Optional<MetaClass> classOpt = metaClassLookup.getOptions().getOptions().collect(Collectors.toList())
                    .stream()
                    .filter(clazz -> metaClassName.equals(clazz.getName()))
                    .findFirst();

            if (classOpt.isPresent()) {
                MetaClass metaClass = classOpt.get();
                metaClassLookup.setValue(metaClass);
                loadEntities(metaClass);
                loadViewNames(metaClass);

                String entityId = value.getEntityId();
                if (isNotBlank(entityId)) {
                    entitiesLookup.getOptions().getOptions().collect(Collectors.toList())
                            .stream()
                            .filter(entity -> entityId.equals(entity.getId().toString()))
                            .findFirst()
                            .ifPresent(entity -> entitiesLookup.setValue(entity));
                }

                String viewName = value.getViewName();
                if (isNotBlank(viewName) && viewLookup.getOptions().getOptions().anyMatch(e -> e.equals(viewName))) {
                    viewLookup.setValue(viewName);
                }
            }
        }
    }

    protected void metaClassValueChanged(MetaClass metaClass) {
        if (metaClass == null) {
            entitiesLookup.setValue(null);
            entitiesLookup.setOptionsList(Collections.emptyList());
            viewLookup.setValue(null);
            viewLookup.setOptionsList(Collections.emptyList());
        } else {
            loadEntities(metaClass);
            loadViewNames(metaClass);
        }
    }

    protected void loadEntities(MetaClass metaClass) {
        LoadContext loadContext = LoadContext.create(metaClass.getJavaClass())
                .setQuery(LoadContext.createQuery(format("select e from %s e", metaClass.getName())));
        List entities = dataManager.loadList(loadContext);
        entitiesLookup.setOptionsList(entities);
    }

    protected void loadViewNames(MetaClass metaClass) {
        List<String> viewNames = new ArrayList<>(metadata.getViewRepository().getViewNames(metaClass));
        viewNames.add(0, View.LOCAL);
        viewNames.add(1, View.MINIMAL);
        viewNames.add(2, View.BASE);
        viewLookup.setOptionsList(viewNames);
    }
}
