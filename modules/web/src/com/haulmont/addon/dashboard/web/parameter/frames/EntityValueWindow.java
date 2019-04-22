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
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.screen.StandardCloseAction;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;

@UiController("dashboard$EntityValueWindow")
@UiDescriptor("entity-value-window.xml")
public class EntityValueWindow extends AbstractWindow implements ValueFrame, Window.Committable {

    @Inject
    protected EntityValueFrame entityValueFrame;

    protected EntityParameterValue prevValue;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityValueFrame.init(params);
        prevValue = (EntityParameterValue) entityValueFrame.getValue();
    }

    @Override
    public ParameterValue getValue() {
        return entityValueFrame.getValue();
    }

    public void commitAndClose() {
        close(new StandardCloseAction(COMMIT_ACTION_ID, false));
    }

    public void cancelAndClose() {
        close(new StandardCloseAction(COMMIT_ACTION_ID));
    }

    @Override
    public boolean isModified() {
        EntityParameterValue currentVal = (EntityParameterValue) entityValueFrame.getValue();
        if ((prevValue == null && currentVal != null) || (prevValue != null && currentVal == null)) {
            return true;
        }
        if (prevValue != null) {
            return !prevValue.equals(currentVal);
        }
        return false;
    }
}
