package com.report.generator.service;

import java.util.List;

public interface GenerateReportService {

    void generateWithGrouping(String report, List<Object> params);

}
