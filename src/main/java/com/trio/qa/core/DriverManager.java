package com.trio.qa.core;

import com.trio.qa.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver initializeDriver(String browser) {
        WebDriver driver = null;

        try {
            String executionPlatform = ConfigReader.getGlobal("execution-platform").toLowerCase();
            logger.info("Execution Platform: {}", executionPlatform);

            switch (executionPlatform) {
                case "local":
                    driver = initializeLocalDriver(browser);
                    break;
                case "docker":
                    driver = initializeDockerDriver(browser);
                    break;
                case "cloud":
                    driver = initializeCloudDriver(browser);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported execution platform: " + executionPlatform);
            }

            if (driver != null) {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.manage().window().maximize();
                driverThreadLocal.set(driver);
                logger.info("{} browser initialized successfully.", browser);
            }
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver for browser: {}", browser, e);
            throw new RuntimeException("WebDriver initialization failed.", e);
        }

        return driver;
    }

    private static WebDriver initializeLocalDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            case "edge":
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private static WebDriver initializeDockerDriver(String browser) throws MalformedURLException {
        String dockerHubURL = ConfigReader.getGlobal("docker-hub-url");
        DesiredCapabilities capabilities = new DesiredCapabilities();

        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setCapability("browserName", "chrome");
                capabilities.merge(chromeOptions);
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setCapability("browserName", "firefox");
                capabilities.merge(firefoxOptions);
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setCapability("browserName", "edge");
                capabilities.merge(edgeOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        return new RemoteWebDriver(new URL(dockerHubURL), capabilities);
    }

    private static WebDriver initializeCloudDriver(String browser) {
        String cloudProvider = ConfigReader.getGlobal("cloud-provider").toLowerCase();

        switch (cloudProvider) {
            case "browserstack":
                return initializeBrowserStackDriver(browser);
            default:
                throw new IllegalArgumentException("Unsupported cloud provider: " + cloudProvider);
        }
    }

    private static WebDriver initializeBrowserStackDriver(String browser) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browser", browser);
        capabilities.setCapability("browser_version", ConfigReader.getGlobal("browser-version"));
        capabilities.setCapability("os", ConfigReader.getGlobal("os"));
        capabilities.setCapability("os_version", ConfigReader.getGlobal("os-version"));

        try {
            return new RemoteWebDriver(new URL(ConfigReader.getGlobal("browserstack-url")), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid BrowserStack URL", e);
        }
    }

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            throw new IllegalStateException("Driver is not initialized.");
        }
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }
}
