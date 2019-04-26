
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
import com.haulmont.addon.dashboard.model.paramtypes.ListEntitiesParameterValue;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@UiController("dashboard$EntitiesListValueFrame")
@UiDescriptor("entities-list-value-frame.xml")
public class EntitiesListValueFrame extends AbstractFrame implements ValueFrame {
    @Inject
    protected ValueCollectionDatasourceImpl entitiesDs;

    @Inject
    protected Screens screens;

    protected Map<KeyValueEntity, EntityParameterValue> tableValues = new HashMap<>();
    protected KeyValueEntity oldValue;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initDs(params);
        entitiesDs.setModified(false);
    }

    @Override
    public ParameterValue getValue() {
        return new ListEntitiesParameterValue(new ArrayList<>(tableValues.values()));
    }

    protected void initDs(Map<String, Object> params) {
        ListEntitiesParameterValue value = (ListEntitiesParameterValue) params.get(VALUE);

        if (value == null || value.getValue() == null) {
            value = new ListEntitiesParameterValue();
        }

        for (EntityParameterValue entityValue : value.getValue()) {
            KeyValueEntity keyValueEntity = createKeyValueEntity(entityValue);
            entitiesDs.addItem(keyValueEntity);
            tableValues.put(keyValueEntity, entityValue);
        }
    }

    public void createEntityValue() {
        oldValue = null;
        openEntityValueWindow(null);
    }

    public void editEntityValue() {
        KeyValueEntity item = entitiesDs.getItem();
        if (item != null) {
            oldValue = item;
            openEntityValueWindow(tableValues.get(item));
        }
    }

    public void removeEntityValue() {
        KeyValueEntity item = entitiesDs.getItem();
        if (item != null) {
            entitiesDs.removeItem(item);
            tableValues.remove(item);
        }
    }

    protected void openEntityValueWindow(EntityParameterValue value) {
        screens.create(EntityValueWindow.class, OpenMode.DIALOG, new MapScreenOptions(ParamsMap.of(VALUE, value)))
                .show()
                .addAfterCloseListener(e -> {
                    StandardCloseAction closeAction = (StandardCloseAction) e.getCloseAction();
                    if (Window.COMMIT_ACTION_ID.equals(closeAction.getActionId())) {
                        saveWindowValue((EntityParameterValue) ((EntityValueWindow) e.getScreen()).getValue());
                    }
                });
    }

    protected void saveWindowValue(EntityParameterValue windowValue) {
        if (oldValue != null) {
            entitiesDs.removeItem(oldValue);
            tableValues.remove(oldValue);
            oldValue = null;
        }

        KeyValueEntity newValue = EntitiesListValueFrame.this.createKeyValueEntity(windowValue);
        entitiesDs.addItem(newValue);
        tableValues.put(newValue, windowValue);
    }

    protected KeyValueEntity createKeyValueEntity(EntityParameterValue value) {
        KeyValueEntity keyValueEntity = new KeyValueEntity();
        keyValueEntity.setValue("metaClassName", value.getMetaClassName());
        keyValueEntity.setValue("entityId", value.getEntityId());
        keyValueEntity.setValue("viewName", value.getViewName());
        return keyValueEntity;
    }

    public void onEdit(Component source) {
        KeyValueEntity item = entitiesDs.getItem();
        if (item != null) {
            oldValue = item;
            openEntityValueWindow(tableValues.get(item));
        }
    }
}
