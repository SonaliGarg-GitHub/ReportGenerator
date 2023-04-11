package com.report.generator.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class Procedures {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> execute() {
        String sql = "SELECT name FROM [Workflow].[dbo].[Accounts]";
        List<String> result = jdbcTemplate.queryForList(sql, String.class);
        return result;
    }

    public  List<Map<String, Object>> execute(String sql, List<Object> params) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());
        return rows;
    }
    public  List<Map<String, Object>> fetchProductionReport( List<Object> params) {
        String sql = "{call [dbo].[GetDataTableProductionReportByDate](?,?,?)}";
        return execute(sql, params);
    }
    public  List<Map<String, Object>> fetchUtilizationReport( List<Object> params) {
        String sql = "{call [dbo].[GetDataTableProductionReportByDate](?,?,?)}";
        return execute(sql, params);
    }

    public  List<Map<String, Object>> fetchAttendanceDetails( List<Object> params) {
        String sql = "{call [dbo].[GetDataTableProductionReportByDate](?,?,?)}";
        return execute(sql, params);
    }


    public  List<Map<String, Object>> fetchPerformanceSummary( List<Object> params) {
        String sql = "{call [dbo].[GetDataTableProductionReportByDate](?,?,?)}";
        return execute(sql, params);
    }


}
