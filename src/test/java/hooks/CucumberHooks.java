package hooks;

import com.trio.qa.runner.TestRunner;
import com.trio.qa.core.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class CucumberHooks {

    private static final Logger logger = LogManager.getLogger(CucumberHooks.class);

    @Before
    public void setUp(Scenario scenario) {
        try {
            logger.info("Starting setup for scenario: {}", scenario.getName());

            // Fetch the browser for the current thread from TestRunner
            String browser = TestRunner.getBrowser();
            if (browser == null || browser.isEmpty()) {
                throw new IllegalArgumentException("Browser not specified for the current thread.");
            }

            // Initialize WebDriver
            DriverManager.initializeDriver(browser);
            logger.info("Initialized WebDriver for browser: {}", browser);

        } catch (Exception e) {
            logger.error("Setup failed for scenario: {}. Error: {}", scenario.getName(), e.getMessage(), e);
            throw new RuntimeException("Setup failed for scenario: " + scenario.getName(), e);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                logger.warn("Scenario failed: {}. Capturing screenshot...", scenario.getName());
                byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot for failed scenario");
            }
        } catch (Exception e) {
            logger.error("Teardown failed for scenario: {}. Error: {}", scenario.getName(), e.getMessage(), e);
        } finally {
            DriverManager.quitDriver();
            logger.info("Closed WebDriver for scenario: {}", scenario.getName());
        }
    }
}
