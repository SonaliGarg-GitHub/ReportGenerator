package com.report.generator.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.report.generator.service.GenerateReportService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ReportGenerator {

    @Autowired
    GenerateReportService reportService;

    @GetMapping("/generateReport")
    public void generateReport(@RequestParam(value = "report") String report ) {
         reportService.generateWithGrouping(report,getParams(5)); //dataTableId=5;
    }

    @GetMapping("/generateAdhoc")
    public void generateAdhoc(@RequestParam(value = "report") String report, @RequestParam("params") List<Object> params ) {
        reportService.generateWithGrouping(report,params);
    }

    List<Object> getParams(int dataTableId){
        List<Object> list = Lists.newArrayList();
        list.add(dataTableId);
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -1);
        Date startDate = calendar.getTime();
        list.add(startDate); //yesterday
        list.add(endDate);   //today
        return list;

    }
}