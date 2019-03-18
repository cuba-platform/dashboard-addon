package com.haulmont.addon.dashboard.model.visualmodel;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@MetaClass(name = "dashboard$ResponsiveLayout")
public class ResponsiveLayout extends DashboardLayout implements ContainerLayout {

    @MetaProperty
    protected Integer xs = 12;

    @MetaProperty
    protected Integer sm = 6;

    @MetaProperty
    protected Integer md = 6;

    @MetaProperty
    protected Integer lg = 3;

    @MetaProperty
    protected Set<ResponsiveArea> areas = new HashSet<>();

    @Override
    public String getCaption() {
        Messages messages = AppBeans.get(Messages.class);
        return messages.getMessage(getClass(), "Layout.responsive");
    }

    public Set<ResponsiveArea> getAreas() {
        return areas;
    }

    public void setAreas(Set<ResponsiveArea> areas) {
        this.areas = areas;
    }

    @Override
    public void addChild(DashboardLayout child) {
        Integer order = getAreas().stream()
                .map(ResponsiveArea::getOrder)
                .max(Comparator.naturalOrder()).orElse(0) + 1;
        ResponsiveArea ra = createArea(child, order);
        getAreas().add(ra);
    }

    private ResponsiveArea createArea(DashboardLayout child, Integer order) {
        Metadata metadata = AppBeans.get(Metadata.class);
        ResponsiveArea ra = metadata.create(ResponsiveArea.class);
        ra.setOrder(order);
        ra.setComponent(child);
        return ra;
    }

    @Override
    public void removeOwnChild(DashboardLayout child) {
        ResponsiveArea target = null;
        for (ResponsiveArea area : areas) {
            if (area.getComponent().equals(child)) {
                target = area;
                break;
            }
        }
        areas.remove(target);
    }

    @Override
    public List<DashboardLayout> getChildren() {
        return getAreas().stream()
                .sorted(Comparator.comparingInt(ResponsiveArea::getOrder))
                .map(ResponsiveArea::getComponent)
                .collect(Collectors.toList());
    }

    @Override
    public void setChildren(List<DashboardLayout> children) {
        int order = 1;
        Set<ResponsiveArea> newAreas = new HashSet<>();
        for (DashboardLayout layout : children) {
            ResponsiveArea area = findArea(layout);
            if (area != null) {
                area.setOrder(order);

            } else {
                area = createArea(layout, order);
            }
            newAreas.add(area);
            order++;
        }
        areas = newAreas;
    }

    private ResponsiveArea findArea(DashboardLayout layout) {
        return getAreas().stream()
                .filter(e -> e.getComponent().getId().equals(layout.getId()))
                .findAny()
                .orElse(null);
    }

    public Integer getXs() {
        return xs;
    }

    public void setXs(Integer xs) {
        this.xs = xs;
    }

    public Integer getSm() {
        return sm;
    }

    public void setSm(Integer sm) {
        this.sm = sm;
    }

    public Integer getMd() {
        return md;
    }

    public void setMd(Integer md) {
        this.md = md;
    }

    public Integer getLg() {
        return lg;
    }

    public void setLg(Integer lg) {
        this.lg = lg;
    }
}
