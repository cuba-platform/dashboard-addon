/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.screens.filters;

import com.haulmont.bali.util.Dom4j;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.FrameContextImpl;
import com.haulmont.cuba.gui.components.Filter;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.filter.ConditionsTree;
import com.haulmont.cuba.gui.components.filter.FilterParser;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.cuba.gui.data.impl.DsContextImpl;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.FilterEntity;

import java.util.Collections;

/**
 * @author bochkarev
 * @version $Id$
 */
public class FakeQueryFilterSupport {
    protected Filter filter;
    protected FilterEntity filterEntity;
    protected MetaClass metaClass;
    protected ConditionsTree conditionsTree;
    protected Frame frame;
    protected FilterParser filterParser;

    public FakeQueryFilterSupport(Frame frame, MetaClass metaClass) {
        this.metaClass = metaClass;
        this.frame = frame;
        filterParser =  AppBeans.get(FilterParser.class);
    }

    public Filter createFakeFilter() {
        if (filter != null) {
            return filter;
        }

        final Filter fakeFilter = AppBeans.get(ComponentsFactory.NAME, ComponentsFactory.class).createComponent(Filter.class);
        fakeFilter.setXmlDescriptor(Dom4j.readDocument("<filter/>").getRootElement());
        CollectionDatasourceImpl fakeDatasource = new CollectionDatasourceImpl();
        DsContextImpl fakeDsContext = new DsContextImpl(frame.getDsContext().getDataSupplier());
        FrameContextImpl fakeFrameContext = new FrameContextImpl(frame, Collections.<String, Object>emptyMap());
        fakeDsContext.setFrameContext(fakeFrameContext);
        fakeDatasource.setDsContext(fakeDsContext);
        //Attention: this query should match the logic in com.haulmont.reports.wizard.ReportingWizardBean.createJpqlDataSet()
        fakeDatasource.setQuery("select queryEntity from " + metaClass.getName() + " queryEntity");
        fakeDatasource.setMetaClass(metaClass);
        fakeFilter.setDatasource(fakeDatasource);
        fakeFilter.setFrame(this.frame);
        return fakeFilter;
    }

    public ConditionsTree createFakeConditionsTree(Filter filter, FilterEntity filterEntity) {
        boolean emptyFilter = filterEntity.getXml() == null || filterEntity.getXml().equals("<filter/>");
        return conditionsTree != null
                ? conditionsTree
                : emptyFilter ? new ConditionsTree() : filterParser.getConditions(filter, filterEntity.getXml());
    }

    public FilterEntity createFakeFilterEntity(String xml) {
        if (filterEntity != null) return filterEntity;

        Metadata metadata = AppBeans.get(Metadata.NAME);
        FilterEntity fakeFilterEntity = metadata.create(FilterEntity.class);
        fakeFilterEntity.setXml(xml);
        return fakeFilterEntity;
    }
}
