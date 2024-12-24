package com.trio.qa.reporting;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReportSetup {

    private static String reportType;
    private static String platform;
    private static String applicationName;
    private static String reportPath;

    static {
        // Load configuration from properties
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config/config.properties"));
            reportType = properties.getProperty("report.type", "testng").toLowerCase();
            platform = properties.getProperty("platform", "web").toLowerCase();
            applicationName = properties.getProperty("application.name", "default").toLowerCase();

            // Construct the base report path dynamically
            reportPath = "reports/" + reportType + "/" + platform + "/" + applicationName;
            createDirectory(reportPath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /**
     * Creates the directory if it doesn't exist.
     *
     * @param path Directory path to create.
     */
    private static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Sets up the Allure results directory.
     */
    public static void setAllureResultsDirectory() {
        if (reportType.equals("allure")) {
            String allureResultsPath = reportPath + "/allure-results";
            createDirectory(allureResultsPath);
            System.setProperty("allure.results.directory", allureResultsPath);
        }
    }

    /**
     * Returns the generated report path.
     *
     * @return The report path.
     */
    public static String getReportPath() {
        return reportPath;
    }

    /**
     * Returns the configured report type.
     *
     * @return The report type.
     */
    public static String getReportType() {
        return reportType;
    }
}
