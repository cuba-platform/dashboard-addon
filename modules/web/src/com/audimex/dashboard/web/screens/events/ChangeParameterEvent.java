/*
 * Copyright (c) 2016-2017 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.screens.events;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bochkarev
 * @version $Id$
 */
public class ChangeParameterEvent {
    protected Map<String, Object> changeValues;

    public void addChangeValue(String key, Object value) {
        if (changeValues == null) {
            changeValues = new HashMap<>();
        }

        if (changeValues.containsKey(key)) {
            changeValues.replace(key, value);
        } else {
            changeValues.put(key, value);
        }
    }

    public Map<String, Object> getChangeValues() {
        return changeValues;
    }
}
