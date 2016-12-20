/*
 * Copyright (c) 2016-2016 Haulmont. All rights reserved.
 */

package com.audimex.dashboard.web.frames.report;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Embedded;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import com.haulmont.yarg.reporting.ReportOutputDocument;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ReportDemoFrame extends AbstractFrame {
    @Inject
    protected Embedded reportContent;

    @Inject
    protected ReportGuiManager reportGuiManager;

    @Inject
    protected DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        LoadContext<Report> lc = new LoadContext<>(Report.class);
        lc.setQueryString(
                "select r from report$Report r where r.code = :code")
                .setParameter("code", "INVOICE");

        Report r = dataManager.load(lc);

        if (r == null) {
            byte[] errContent = "<html><body>INVOICE report not found</body></html>".getBytes(StandardCharsets.UTF_8);
            reportContent.setSource("error.html", new ByteArrayInputStream(errContent));
        } else {
            ReportOutputDocument result = reportGuiManager.getReportResult(r, Collections.emptyMap(), null);

            reportContent.setSource("report" + UUID.randomUUID() + ".html", new ByteArrayInputStream(result.getContent()));
        }
    }
}