package com.audimex.dashboard.web.layouts;

/**
 * Created by petunin on 27.02.2017.
 */
public interface HasGridSpan {
    void setColSpan(int colSpan);
    int getColSpan();

    void setRowSpan(int rowSpan);
    int getRowSpan();
}
