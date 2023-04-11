package com.report.generator.service;

import com.report.generator.utility.Mailer;
import com.report.generator.convertor.ExcelConverter;
import com.report.generator.repository.Procedures;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenerateReportServiceImpl implements GenerateReportService {

    @Autowired
    Procedures proc;
    @Autowired
    Mailer mailer;
    @Autowired
    ExcelConverter excelConverter;
    @Override
    public void generateWithGrouping(String procName, List<Object> params) {

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
            List<Map<String, Object>> productions = supervisor.getValue();
            List<Map<String, Object>> utilization = teamUtilization.get(team);
            List<Map<String, Object>> attendance = teamAttendance.get(team);
            List<Map<String, Object>> performance = teamPerformance.get(team);


            String filePath = getFilePath(procName+team);
            Workbook workbook = excelConverter.getNewWorkbook();
            excelConverter.addSheet(workbook,"Team Production", productions);
            excelConverter.addSheet(workbook,"Team Utilization", utilization);
            excelConverter.addSheet(workbook,"Team Attendance", attendance);
            excelConverter.addSheet(workbook,"Team Performance", performance);
            excelConverter.convertToExcelFile(workbook, filePath);
            emailReports(filePath);
        }
    }


    //TODO : add a file Path Absolute Location Value. or do you want these files to be deleted
    private static String getFilePath(String... fileNames) {
        return String.join("_", fileNames).replaceAll("[^a-zA-Z0-9]+", "") + ".xlsx";
    }

    private void emailReports(String filePath) {
            triggerMail(filePath, getRecipients());
    }

    //TODO : Update how to get the the FROM Address, Message Body, Message Subject, basically Email Template & recipients addresses.
    private boolean triggerMail(String filePath, Map<Message.RecipientType, InternetAddress[]> recipients) {

        String emailFrom = "er.sonaligarg@gmail.com";
        try {
            InternetAddress sender = new InternetAddress(emailFrom, "Er Sonali Garg");

            mailer.sendMail(sender, recipients, new File(filePath));
           log.info(" Mail Sent ");
            return true;
        } catch (UnsupportedEncodingException e) {
            log.error("Mail couldn't be sent, please check sender address and credentials. " +
                    " Probably wrong Sender Address {}, please cross-check credentials", e.getMessage());
            throw new RuntimeException("Mail couldn't be sent, please check sender address and credentials.", e);
        }

    }

    private Map<Message.RecipientType, InternetAddress[]> getRecipients() {

        Map<Message.RecipientType, InternetAddress[]> recipients = new HashMap<>();
        String[] emailTos = new String[]{"gargs1707@gmail.com"}; // ,"amkrishna4u@gmail.com","raviteja_joshi@outlook.com"
        String[] emailCcs = new String[]{"gargs1707@outlook.com"};

        InternetAddress[] tos = getInternetAddresses(emailTos);
        InternetAddress[] ccs = getInternetAddresses(emailCcs);

        recipients.put(Message.RecipientType.TO, tos);
        recipients.put(Message.RecipientType.CC, ccs);

        return recipients;
    }

    private InternetAddress[] getInternetAddresses(String[] emails) {
        int i = 0;
        InternetAddress[] validEmails = null;
        if (emails != null) {
            validEmails = new InternetAddress[emails.length];
            for (String mail : emails) {
                try {
                    validEmails[i++] = new InternetAddress(mail);
                } catch (AddressException ex) {
                    log.warn(" Skipping Invalid Email address : {},  {}", mail, ex.getMessage());
                }
            }
        }
        return validEmails;
    }
}
