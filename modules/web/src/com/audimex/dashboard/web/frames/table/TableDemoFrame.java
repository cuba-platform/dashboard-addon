/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.frames.table;

import com.audimex.dashboard.entity.Product;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class TableDemoFrame extends AbstractFrame {
    @Inject
    protected CollectionDatasource<Product, UUID> productsDs;
    @Inject
    protected Table<Product> table;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        productsDs.refresh();

        table.setSettingsEnabled(false);

        addProduct("Echo", "All-New Echo Dot", "39.99");
        addProduct("Fire TV Stick", "TV Stick with Alexa Voice Remote", "42.99");
        addProduct("Playstation 4 Slim", "PlayStation 4 Slim 500GB Console - Uncharted 4 Bundle", "249.00");
        addProduct("Steam Gift card 20$", "Steam Gift Card Limited", "20");
        addProduct("Fantastic Beasts and Where to Find Them", "The Original Screenplay", "14.99");
        addProduct("Fire Tablet", "Fire Tablet 7\" 8GB", "39.99");
    }

    private void addProduct(String title, String description, String price) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));

        productsDs.includeItem(product);
    }
}