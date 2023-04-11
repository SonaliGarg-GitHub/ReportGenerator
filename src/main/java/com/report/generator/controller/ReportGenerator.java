package com.report.generator.controller;

import java.util.List;

import com.report.generator.service.GenerateReportService;
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
    public void generateReport(@RequestParam(value = "proc") String procName, @RequestParam("params") List<Object> params ) {
         reportService.generateWithGrouping(procName,params);
    }

    @GetMapping("/generateAdhoc")
    public void generateAdhoc(@RequestParam(value = "proc") String procName, @RequestParam("params") List<Object> params ) {
    }

    @GetMapping("hello")
    public String hello(){
        return "hello";
    }
}