
package com.audimex.dashboard.web.parameter.frames;

import com.audimex.dashboard.model.param_value_types.EntityValue;
import com.audimex.dashboard.model.param_value_types.ListEntitiesValue;
import com.audimex.dashboard.model.param_value_types.Value;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.haulmont.cuba.gui.WindowManager.OpenType.DIALOG;

public class EntitiesListValueFrame extends AbstractFrame implements ValueFrame {
    @Inject
    protected ValueCollectionDatasourceImpl entitiesDs;

    protected Map<KeyValueEntity, EntityValue> tableValues = new HashMap<>();
    protected KeyValueEntity oldValue;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initDs(params);
    }

    @Override
    public Value getValue() {
        return new ListEntitiesValue(new ArrayList<>(tableValues.values()));
    }

    protected void initDs(Map<String, Object> params) {
        ListEntitiesValue value = (ListEntitiesValue) params.get(VALUE);

        if (value == null || value.getEntityValues() == null) {
            value = new ListEntitiesValue();
        }

        for (EntityValue entityValue : value.getEntityValues()) {
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

    protected void openEntityValueWindow(EntityValue value) {
        EntityValueWindow editValueWindow = (EntityValueWindow) openWindow("entityValueWindow", DIALOG,
                ParamsMap.of(VALUE, value));

        editValueWindow.addCloseListener(actionId -> {
            if ("commit".equals(actionId)) {
                saveWindowValue((EntityValue) editValueWindow.getValue());
            }
        });
    }

    protected void saveWindowValue(EntityValue windowValue) {
        if (oldValue != null) {
            entitiesDs.removeItem(oldValue);
            tableValues.remove(oldValue);
            oldValue = null;
        }

        KeyValueEntity newValue = EntitiesListValueFrame.this.createKeyValueEntity(windowValue);
        entitiesDs.addItem(newValue);
        tableValues.put(newValue, windowValue);
    }

    protected KeyValueEntity createKeyValueEntity(EntityValue value) {
        KeyValueEntity keyValueEntity = new KeyValueEntity();
        keyValueEntity.setValue("metaClassName", value.getMetaClassName());
        keyValueEntity.setValue("entityId", value.getEntityId());
        keyValueEntity.setValue("viewName", value.getViewName());
        return keyValueEntity;
    }
}
