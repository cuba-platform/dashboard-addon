
package com.haulmont.addon.dashboard.web.parameter.frames;

import com.haulmont.addon.dashboard.model.paramtypes.EntityParameterValue;
import com.haulmont.addon.dashboard.model.paramtypes.ListEntitiesParameterValue;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.haulmont.cuba.gui.components.Component;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;

public class EntitiesListValueFrame extends AbstractFrame implements ValueFrame {
    @Inject
    protected ValueCollectionDatasourceImpl entitiesDs;

    protected Map<KeyValueEntity, EntityParameterValue> tableValues = new HashMap<>();
    protected KeyValueEntity oldValue;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initDs(params);
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
        EntityValueWindow editValueWindow = (EntityValueWindow) openWindow("dashboard$EntityValueWindow", DIALOG,
                ParamsMap.of(VALUE, value));

        editValueWindow.addCloseListener(actionId -> {
            if ("commit".equals(actionId)) {
                saveWindowValue((EntityParameterValue) editValueWindow.getValue());
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
