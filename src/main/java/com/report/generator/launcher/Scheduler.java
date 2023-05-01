package com.report.generator.launcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Garg, Sonali
 * Created on 25-04-2023.
 */

@Component
@Slf4j
public class Scheduler {

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(cron = "0 */1 * * * ?") // every day at 7PM
    //"0 */1 * * * ?" -trigger every minute
    //"0 0 19 * * *") // every day at 7PM
    //"0 7,19 * * *"  // 7AM & 7PM Everyday
    public void executeRestApi() {
        String url = "http://localhost:8080/generateReport?proc=AdhocCall&params=5&params=2022-12-18&params=2023-12-19";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        // Handle response here
    }

    private List<Object> getParams() {

        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date endDate = calendar.getTime();
        int dataTableId = 5;

        log.info("start date= " + startDate);
        log.info("end date = " +endDate);
        log.info("DataTableId = " );

        List<Object> params = Lists.newArrayList();
        params.add(dataTableId);
        params.add(startDate);
        params.add(endDate);
        return params;
    }
}


