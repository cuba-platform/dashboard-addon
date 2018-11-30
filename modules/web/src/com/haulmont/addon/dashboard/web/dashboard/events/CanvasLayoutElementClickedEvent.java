/*
 * Copyright (c) 2008-2018 Haulmont.
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
 *
 */

package com.haulmont.addon.dashboard.web.dashboard.events;

import com.haulmont.cuba.gui.components.MouseEventDetails;
import com.haulmont.cuba.gui.events.UiEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class CanvasLayoutElementClickedEvent extends ApplicationEvent implements UiEvent {

    private UUID layoutUuid;

    private MouseEventDetails mouseEventDetails;

    public CanvasLayoutElementClickedEvent(UUID source, MouseEventDetails mouseEventDetails) {
        super(source);
        layoutUuid = source;
        this.mouseEventDetails = mouseEventDetails;
    }

    @Override
    public UUID getSource() {
        return layoutUuid;
    }

    public MouseEventDetails getMouseEventDetails() {
        return mouseEventDetails;
    }
}
