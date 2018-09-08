/*
 * Copyright (c) 2016-2018 Haulmont. All rights reserved.
 */

package com.haulmont.addon.dashboard.web.widget.displayer;

import com.haulmont.addon.dashboard.web.annotation.DashboardWidget;
import com.haulmont.addon.dashboard.web.events.ItemsSelectedEvent;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.security.entity.User;
import org.springframework.context.event.EventListener;

import javax.inject.Named;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.haulmont.addon.dashboard.web.widget.displayer.UserDisplayerBrowse.CAPTION;


@DashboardWidget(name = CAPTION, editFrameId = "dashboard$UserDispWidgetEdit")
public class UserDisplayerBrowse extends AbstractFrame {

    public static final String CAPTION = "UserDisp";

    @Named("userNameArea")
    protected Label label;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @EventListener
    public void showUserName(ItemsSelectedEvent event) {
        Collection col = event.getSelected();
        Iterator iterator = col.iterator();
        Object userobj = iterator.next();

        if (userobj instanceof User) {
            User user = (User) userobj;
            label.setCaption(user.getLogin().toUpperCase());
            label.setValue(user.getLogin().toUpperCase());
            
        }
    }

}
