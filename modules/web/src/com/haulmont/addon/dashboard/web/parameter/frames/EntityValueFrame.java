package com.haulmont.addon.dashboard.web.parameter.frames;

import com.haulmont.addon.dashboard.model.paramtypes.EntityParameterValue;
import com.haulmont.addon.dashboard.model.paramtypes.ParameterValue;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.LookupField;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

            Optional<MetaClass> classOpt = ((List<MetaClass>) metaClassLookup.getOptionsList())
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
                    ((List<Entity>) entitiesLookup.getOptionsList())
                            .stream()
                            .filter(entity -> entityId.equals(entity.getId().toString()))
                            .findFirst()
                            .ifPresent(entity -> entitiesLookup.setValue(entity));
                }

                String viewName = value.getViewName();
                if (isNotBlank(viewName) && viewLookup.getOptionsList().contains(viewName)) {
                    viewLookup.setValue(viewName);
                }
            }
        }
    }


    protected void metaClassValueChanged(MetaClass metaClass) {
        if (metaClass == null) {
            entitiesLookup.setValue(null);
            entitiesLookup.setOptionsList(null);
            viewLookup.setValue(null);
            viewLookup.setOptionsList(null);
        }

        loadEntities(metaClass);
        loadViewNames(metaClass);
    }

    protected void loadEntities(MetaClass metaClass) {
        LoadContext loadContext = LoadContext.create(metaClass.getJavaClass())
                .setQuery(LoadContext.createQuery(format("select e from %s e", metaClass.getName())));
        List entities = dataManager.loadList(loadContext);
        entitiesLookup.setOptionsList(entities);
    }

    protected void loadViewNames(MetaClass metaClass) {
        List<String> viewNames = new ArrayList<>(metadata.getViewRepository().getViewNames(metaClass));
        viewLookup.setOptionsList(viewNames);
    }

}
