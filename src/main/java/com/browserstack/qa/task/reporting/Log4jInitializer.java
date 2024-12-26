package com.browserstack.qa.task.reporting;

import com.browserstack.qa.task.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log4jInitializer {

    public static void initialize() {
        // Set dynamic properties
        String projectName = ConfigReader.getGlobal("project.name");
        String applicationName = ConfigReader.getGlobal("application.name");;
        String buildName = ConfigReader.getGlobal("build.name");;
        String executionTimestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        String logFileName = String.format("logs/%s-%s-%s-%s.log",
                projectName, applicationName, buildName, executionTimestamp);

        System.setProperty("projectName", projectName);
        System.setProperty("applicationName", applicationName);
        System.setProperty("buildName", buildName);
        System.setProperty("executionTimestamp", executionTimestamp);

        // Reconfigure Log4j programmatically
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n")
                .build();

        FileAppender appender = FileAppender.newBuilder()
                .withFileName(logFileName)
                .withName("File")
                .withAppend(true)
                .withLayout(layout)
                .build();
        appender.start();

        config.addAppender(appender);

        AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
        LoggerConfig loggerConfig = config.getRootLogger();
        loggerConfig.addAppender(appender, null, null);

        context.updateLoggers(); // This causes all Loggers to reconfigure
    }
}
