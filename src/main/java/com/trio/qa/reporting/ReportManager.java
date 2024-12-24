package com.trio.qa.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.qameta.allure.Allure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReportManager {

    private static ExtentReports extent;
    private static ExtentTest test;
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

            // Construct report path dynamically
            reportPath = "reports/" + reportType + "/" + platform + "/" + applicationName;
            createDirectory(reportPath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static String getReportPath() {
        return reportPath;
    }

    public static String getReportType() {
        return reportType;
    }

    // Initialize ExtentReports
    private static ExtentReports initExtentReports() {
        String extentReportFile = reportPath + "/TestExecutionReport.html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(extentReportFile);
        sparkReporter.config().setReportName("Automation Test Report");
        sparkReporter.config().setDocumentTitle("Test Execution Report");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        return extent;
    }

    // Start ExtentReports test
    public static ExtentTest startExtentTest(String testName) {
        if (extent == null) {
            extent = initExtentReports();
        }
        test = extent.createTest(testName);
        return test;
    }

    // Unified logging method
    public static void log(String testName, String message, String status) {
        if (reportType.equals("extent")) {
            if (test == null) {
                startExtentTest(testName);
            }
            switch (status.toLowerCase()) {
                case "info":
                    test.info(message);
                    break;
                case "pass":
                    test.pass(message);
                    break;
                case "fail":
                    test.fail(message);
                    break;
                default:
                    test.info(message);
                    break;
            }
        } else if (reportType.equals("allure")) {
            Allure.step(message);
        } else {
            System.out.println("[" + status.toUpperCase() + "] " + message);
        }
    }

    public static void endExtentTest() {
        if (extent != null) {
            extent.flush();
        }
    }
}
