package hooks;

import com.trio.qa.config.ConfigReader;
import com.trio.qa.core.DriverManager;
import com.trio.qa.reporting.ReportSetup;
import com.trio.qa.utils.exception.ExceptionHandler;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.trio.qa.utils.ReportManager.log;

public class CucumberHooks {

    private static final Logger logger = LogManager.getLogger(CucumberHooks.class);

    @Before
    public void setUp(Scenario scenario) {
        try {
            log("Cucumber Hook Setup", "Starting test setup for scenario: " + scenario.getName(), "info");
            logger.info("Starting test setup for scenario: {}", scenario.getName());

            // Configure Allure reporting if needed
            if (ReportSetup.getReportType().equalsIgnoreCase("allure")) {
                ReportSetup.setAllureResultsDirectory();
                log("Cucumber Hook Setup", "Allure results directory set up successfully.", "info");
            }

            // Get list of browsers from config
            String[] browsers = ConfigReader.getBrowserList();
            String browser = System.getProperty("browser", browsers[0]); // Use system property or first browser in list
            logger.info("Using browser: {}", browser);

            // Initialize WebDriver for the current browser
            DriverManager.initializeDriver(browser);
            logger.info("Driver initialized successfully for browser: {}", browser);

            // Navigate to base URL
            String baseUrl = ConfigReader.getEnvironmentSpecific("baseUrl");
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new IllegalArgumentException("Base URL not specified in the configuration.");
            }
            DriverManager.getDriver().get(baseUrl);
            log("Cucumber Hook Setup", "Navigated to URL: " + baseUrl, "info");
            logger.info("Navigated to URL: {}", baseUrl);

        } catch (Exception e) {
            log("Cucumber Hook Setup", "Setup failed: " + e.getMessage(), "fail");
            logger.error("Setup failed: {}", e.getMessage(), e);
            ExceptionHandler.handleException(e, DriverManager.getDriver());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            log("Cucumber Hook Teardown", "Starting test teardown for scenario: " + scenario.getName(), "info");
            logger.info("Starting test teardown for scenario: {}", scenario.getName());

            // Capture screenshot on failure
            if (scenario.isFailed() && DriverManager.getDriver() != null) {
                log("Cucumber Hook Teardown", "Scenario failed. Capturing screenshot...", "fail");
                logger.info("Capturing screenshot for failed scenario.");
                byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot - " + scenario.getName());

                // Save screenshot locally
                Files.createDirectories(Paths.get("screenshots"));
                Files.write(Paths.get("screenshots", scenario.getName() + ".png"), screenshot);
            }

        } catch (Exception e) {
            log("Cucumber Hook Teardown", "Teardown failed: " + e.getMessage(), "fail");
            logger.error("Teardown failed: {}", e.getMessage(), e);
        } finally {
            DriverManager.quitDriver();
            log("Cucumber Hook Teardown", "WebDriver closed successfully.", "info");
            logger.info("WebDriver closed successfully.");
        }
    }
}
