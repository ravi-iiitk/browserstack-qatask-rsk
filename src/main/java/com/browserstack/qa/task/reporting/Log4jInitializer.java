package com.browserstack.qa.task.reporting;

import com.browserstack.qa.task.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log4jInitializer {

    public static void initialize() {
        // Fetch and sanitize dynamic properties
        String projectName = sanitize(ConfigReader.getGlobal("project.name"), "DefaultProject");
        String applicationName = sanitize(ConfigReader.getGlobal("application.name"), "DefaultApp");
        String buildName = sanitize(ConfigReader.getGlobal("build.name"), "DefaultBuild");
        String executionTimestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        String logFileName = String.format("logs/%s-%s-%s-%s.log",
                projectName, applicationName, buildName, executionTimestamp);

        // Ensure logs directory exists
        ensureLogDirectory("logs");

        // Force Log4j to use programmatic configuration
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.stop(); // Stop current configuration

        Configuration config = context.getConfiguration();

        // Define log pattern
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n")
                .build();

        // File Appender
        FileAppender fileAppender = FileAppender.newBuilder()
                .withFileName(logFileName)
                .withName("File")
                .withAppend(true)
                .withLayout(layout)
                .build();
        fileAppender.start();

        // Console Appender
        ConsoleAppender consoleAppender = ConsoleAppender.newBuilder()
                .setName("Console")
                .setLayout(layout)
                .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                .build();
        consoleAppender.start();

        // Add appenders to the configuration
        config.addAppender(fileAppender);
        config.addAppender(consoleAppender);

        LoggerConfig loggerConfig = config.getRootLogger();
        loggerConfig.addAppender(fileAppender, null, null);
        loggerConfig.addAppender(consoleAppender, null, null);

        // Reconfigure Log4j context
        context.start(config);
        context.updateLoggers();
    }


    private static String sanitize(String value, String defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value.replaceAll("[\\\\/:*?\"<>|]", "_"); // Replace invalid characters with '_'
    }

    private static void ensureLogDirectory(String directoryPath) {
        File logDir = new File(directoryPath);
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create logs directory: " + directoryPath);
            }
        }
    }
}
