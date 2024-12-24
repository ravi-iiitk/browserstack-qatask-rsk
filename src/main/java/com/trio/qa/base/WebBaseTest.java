package com.trio.qa.base;

import com.trio.qa.config.ConfigReader;
import com.trio.qa.core.DriverManager;
import com.trio.qa.utils.exception.ExceptionHandler;
import com.trio.qa.reporting.ReportSetup;
import com.trio.qa.utils.testdata.TestDataLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.trio.qa.utils.ReportManager.log;
import static com.trio.qa.utils.ReportManager.endExtentTest;

public class WebBaseTest {

    protected WebDriver driver;
    private static final Logger logger = LogManager.getLogger(WebBaseTest.class);

    @BeforeMethod
    public void setUp() {
        try {
            log("Base Test Setup", "Starting test setup", "info");
            logger.info("Starting test setup...");

            // Set up reporting directories
            if (ReportSetup.getReportType().equals("allure")) {
                ReportSetup.setAllureResultsDirectory();
                log("Base Test Setup", "Allure results directory set up", "info");
            }

            // Initialize WebDriver
            String browser = ConfigReader.get("browser");
            driver = DriverManager.initializeDriver(browser);
            log("Base Test Setup", "WebDriver initialized for browser: " + browser, "info");
            logger.info("WebDriver initialized for browser: {}", browser);

            // Navigate to base URL
            String baseUrl = ConfigReader.get("baseUrl");
            driver.get(baseUrl);
            log("Base Test Setup", "Navigated to URL: " + baseUrl, "info");
            logger.info("Navigated to URL: {}", baseUrl);



        } catch (Exception e) {
            log("Base Test Setup", "Setup failed: " + e.getMessage(), "fail");
            logger.error("Setup failed: {}", e.getMessage(), e);
            ExceptionHandler.handleException(e, driver);
        }
    }

    @AfterMethod
    public void tearDown() {
        try {
            log("Base Test Teardown", "Starting test teardown", "info");
            logger.info("Starting test teardown...");

            if (driver != null) {
                driver.quit();
                log("Base Test Teardown", "WebDriver closed successfully", "info");
                logger.info("WebDriver closed successfully.");
            }

        } catch (Exception e) {
            log("Base Test Teardown", "Teardown failed: " + e.getMessage(), "fail");
            logger.error("Teardown failed: {}", e.getMessage(), e);
            ExceptionHandler.handleException(e, driver);
        } finally {
            endExtentTest();
        }
    }

    private Map<String, String> parseDescription(String description) {
        try {
            String[] parts = description.split(",");
            return Map.of(
                    "module", parts[0].split(":")[1].trim(),
                    "subModule", parts[1].split(":")[1].trim()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse test description: " + description, e);
        }
    }
    @DataProvider(name = "testDataProvider")
    public Object[][] testDataProvider(Method method) {
        try {
            String description = method.getAnnotation(Test.class).description();
            Map<String, String> parsed = parseDescription(description);
            String module = parsed.get("module");
            String subModule = parsed.get("subModule");
            String scenario = System.getProperty("scenario", "all_positive").toLowerCase();

            List<Map<String, Object>> data = TestDataLoader.getTestData(module, subModule, scenario);

            return data.stream().map(d -> new Object[]{d}).toArray(Object[][]::new);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data", e);
        }
    }

}
