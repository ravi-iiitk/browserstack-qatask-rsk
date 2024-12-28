package com.browserstack.qa.task.reporting;

import com.browserstack.qa.task.config.ConfigReader;

import javax.sound.midi.SysexMessage;
import java.io.File;

public class LogSetup {
    public static void setupLoggingProperties() {
        // Fetch dynamic values
        String projectName = sanitize(ConfigReader.getGlobal("project.name"), "DefaultProject");
        String applicationName = sanitize(ConfigReader.getGlobal("application.name"), "DefaultApp");
        String buildName = sanitize(ConfigReader.getGlobal("build.name"), "DefaultBuild");

        // Set system properties for Log4j
        System.setProperty("projectName", projectName);
        System.setProperty("applicationName", applicationName);
        System.setProperty("buildName", buildName);

        // Ensure logs directory exists
        ensureLogDirectory("logs");
    }

    private static String sanitize(String value, String defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value.replaceAll("[\\\\/:*?\"<>|]", "_"); // Replace invalid characters with '_'
    }

    private static void ensureLogDirectory(String directoryPath) {
        File logDir = new File(directoryPath);
        if (!logDir.exists() && !logDir.mkdirs()) {
            System.err.println("Failed to create logs directory: " + directoryPath);
        }
    }
}
