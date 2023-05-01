package com.report.generator;

import com.report.generator.config.DatabaseConfig;
import com.report.generator.config.SchedulerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySource(value = "classpath:application-local.yaml")
@Import({DatabaseConfig.class, SchedulerConfig.class})
@Slf4j
public class DemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

		try(ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor())
		{
			// Cleaning up every day at 7am
			LocalTime time = LocalTime.of(7, 0);
			Duration initialDelay = Duration.between(LocalTime.now(), time);
			if (initialDelay.isNegative() || initialDelay.isZero()) {
				// If the specified time has already passed today, schedule the task for tomorrow instead
				initialDelay = initialDelay.plusDays(1);
			}

			executor.scheduleAtFixedRate(() -> {

				File directory = new File("/reports");
				File[] files = directory.listFiles();
				long currentTime = new Date().getTime();
				long twoHoursAgo = currentTime - 2 * 60 * 60 * 1000;

				for (File file : files) {
					if (file.lastModified() < twoHoursAgo) {
						log.warn("Deleting file: " + file.getName());
						file.delete();
					}
				}
			}, initialDelay.toHours(), 24, TimeUnit.HOURS);
		}
	}
}

