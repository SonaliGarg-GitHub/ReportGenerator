package com.report.generator.service;

import com.report.generator.utility.AnnovaMailTemplate;
import com.report.generator.convertor.ExcelConverter;
import com.report.generator.repository.Procedures;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenerateReportServiceImpl implements GenerateReportService {

    @Autowired
    Procedures proc;
    @Autowired
    AnnovaMailTemplate annovaMailTemplate;
    @Autowired
    ExcelConverter excelConverter;
    @Override
    public void generateWithGrouping(String report,List<Object> params) {

        log.info("Fetching Daily reports ");

        List<Map<String, Object>> productionReport = proc.fetchProductionReport(params);
        List<Map<String, Object>> utilizationReport = proc.fetchUtilizationReport(params);
        List<Map<String, Object>> attendanceDetails = proc.fetchAttendanceDetails(params);
        List<Map<String, Object>> performanceSummary = proc.fetchPerformanceSummary(params);

        Map<Object, List<Map<String, Object>>> teamProductions = productionReport.stream()
                .collect(Collectors.groupingBy(row -> row.get("Supervisor")));
        Map<Object, List<Map<String, Object>>> teamUtilization = utilizationReport.stream()
                .collect(Collectors.groupingBy(row -> row.get("Supervisor")));
        Map<Object, List<Map<String, Object>>> teamAttendance = attendanceDetails.stream()
                .collect(Collectors.groupingBy(row -> row.get("Supervisor")));
        Map<Object, List<Map<String, Object>>> teamPerformance = performanceSummary.stream()
                .collect(Collectors.groupingBy(row -> row.get("Supervisor")));


        for (Map.Entry<Object, List<Map<String, Object>>> supervisor : teamProductions.entrySet()) {
            Object team = supervisor.getKey();
            log.info("Creating {}'s {} Report",team,report);
            List<Map<String, Object>> productions = supervisor.getValue();
            List<Map<String, Object>> utilization = teamUtilization.get(team);
            List<Map<String, Object>> attendance = teamAttendance.get(team);
            List<Map<String, Object>> performance = teamPerformance.get(team);

            Workbook workbook = excelConverter.getNewWorkbook();
            ExcelConverter.addSheet(workbook,"Team Production", productions);
            ExcelConverter.addSheet(workbook,"Team Utilization", utilization);
            ExcelConverter.addSheet(workbook,"Team Attendance", attendance);
            ExcelConverter.addSheet(workbook,"Team Performance", performance);

            String filePath = getFilePath(report+team);
            excelConverter.convertToExcelFile(workbook, filePath);
            log.info("Sending {}'s {} Report",team,report);

            Map<String, Object> replacements = new HashMap<>();
            replacements.put("supervisor", team); //team
            replacements.put("startDate", params.get(1));
            replacements.put("endDate", params.get(2));
            replacements.put("attachment", filePath);
            triggerMail(report,replacements);
        }
    }

    private static String getFilePath(String... fileNames) {
        return "/reports/"+UUID.randomUUID() + ".xlsx";
    }

    private boolean triggerMail(String report, Map<String, Object> replacements) {
        try {
            annovaMailTemplate.sendMail(report, replacements);
            log.info(" {} Sent ", report);
            return true;
        } catch (UnsupportedEncodingException e) {
            log.error("Mail couldn't be sent, please check sender address and credentials. " +
                    " Probably wrong Sender Address {}, please cross-check credentials", e.getMessage());
            throw new RuntimeException("Mail couldn't be sent, please check sender address and credentials.", e);
        } catch (MessagingException e) {
            log.error("Mail couldn't be sent, please check sender address and credentials. " +
                    " Probably wrong Sender Address {}, please cross-check credentials", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
