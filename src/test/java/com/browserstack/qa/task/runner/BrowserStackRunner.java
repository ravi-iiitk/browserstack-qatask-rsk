package com.browserstack.qa.task.runner;

import com.browserstack.qa.task.utils.testdata.TestDataLoader;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.TestNG;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.ArrayList;
import java.util.List;

@CucumberOptions(
        features = "src/test/resources/feature-files", // Path to your feature files
        glue = {"stepdefinitions.web.browserstack.qa.task.elpais", "hooks"}, // Include step definitions and hooks
        plugin = {
                "pretty", // Prints Gherkin steps in the console
                "html:target/cucumber-reports/cucumber-html-report.html", // HTML report
                "json:target/cucumber-reports/cucumber.json", // JSON report
                "junit:target/cucumber-reports/cucumber.xml" // JUnit XML report
        },
        tags = "@smoke", // Tags to filter tests
        monochrome = true // Better console output
)
public class BrowserStackRunner extends AbstractTestNGCucumberTests {
    private static final Logger logger = LogManager.getLogger(BrowserStackRunner.class);
    private static final ThreadLocal<String> threadLocalBrowser = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalOS = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalOSVersion = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalBrowserVersion = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalDeviceName = new ThreadLocal<>();

    @Parameters({"browser", "os", "osVersion", "browserVersion", "deviceName"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser,
                      @Optional("Windows") String os,
                      @Optional("11") String osVersion,
                      @Optional("latest") String browserVersion,
                      @Optional("") String deviceName) {
        threadLocalBrowser.set(browser);
        threadLocalOS.set(os);
        threadLocalOSVersion.set(osVersion);
        threadLocalBrowserVersion.set(browserVersion);
        threadLocalDeviceName.set(deviceName);
        logger.info("Browser in this thread: " + browser);
        logger.info("OS selected in this thread: " + os);
        logger.info("OS Version  in this thread: " + osVersion);
        logger.info("Browser Version selected in this thread: " + browserVersion);
        logger.info("Device Name selected in this thread: " + deviceName);
    }

    public static String getBrowser() {
        return threadLocalBrowser.get();
    }

    public static String getOS() {
        return threadLocalOS.get();
    }

    public static String getOSVersion() {
        return threadLocalOSVersion.get();
    }

    public static String getBrowserVersion() {
        return threadLocalBrowserVersion.get();
    }

    public static String getDeviceName() {
        return threadLocalDeviceName.get();
    }

}
