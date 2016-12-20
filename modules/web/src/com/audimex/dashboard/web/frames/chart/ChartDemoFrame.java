/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.frames.chart;

import com.audimex.dashboard.entity.CountryData;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class ChartDemoFrame extends AbstractFrame {

    @Inject
    protected CollectionDatasource<CountryData, UUID> countryDataDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addCountryData("Russia", 256);
        addCountryData("Ireland", 131);
        addCountryData("Germany", 115);
        addCountryData("Austria", 109);
        addCountryData("UK", 65);
        addCountryData("Belgium", 40);
    }

    private void addCountryData(String name, int value) {
        CountryData item = new CountryData();
        item.setCountry(name);
        item.setValue(value);

        countryDataDs.includeItem(item);
    }
}