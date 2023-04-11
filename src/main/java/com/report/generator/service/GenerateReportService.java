package com.report.generator.service;

import java.util.List;

public interface GenerateReportService {

    void generateWithGrouping(String procName, List<Object> params);

}
