/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget_types.displayer;

import com.haulmont.addon.dashboard.model.ParameterType;
import com.haulmont.addon.dashboard.web.annotation.WidgetParam;
import com.haulmont.addon.dashboard.web.annotation.WidgetType;
import com.haulmont.addon.dashboard.web.events.DashboardEvent;
import com.haulmont.addon.dashboard.web.events.WidgetEntitiesSelectedEvent;
import com.haulmont.addon.dashboard.web.widget_types.AbstractWidgetBrowse;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.security.entity.User;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget_types.displayer.UserDisplayerBrowse.CAPTION;


@WidgetType(name = CAPTION, editFrameId = "userdispWidgetEdit")
public class UserDisplayerBrowse extends AbstractWidgetBrowse {

    public static final String CAPTION = "UserDisp";

    @Named("userNameArea")
    protected Label label;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        refresh(params);
    }

    @Override
    public void refresh(Map<String, Object> params) {

    }

    @EventListener
    public void showUserName(WidgetEntitiesSelectedEvent event) {
        WidgetEntitiesSelectedEvent.WidgetWithEntities source = event.getSource();
        Collection col = source.getEntities();
        Iterator iterator = col.iterator();
        Object userobj = iterator.next();

        if (userobj instanceof User) {
            User user = (User) userobj;
            label.setCaption(user.getLogin().toUpperCase());
            label.setValue(user.getLogin().toUpperCase());
            
        }
    }

}
