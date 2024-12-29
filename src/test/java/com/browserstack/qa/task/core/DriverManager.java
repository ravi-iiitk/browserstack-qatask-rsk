package com.browserstack.qa.task.core;

import com.browserstack.qa.task.runner.BrowserStackRunner;
import com.browserstack.qa.task.config.ConfigReader;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
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
import java.util.HashMap;

public class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
        private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();


    public static WebDriver initializeDriver(String browser) {
        WebDriver driver = null;

        try {
            String executionPlatform = ConfigReader.getGlobal("execution-platform").toLowerCase();
            logger.info("Execution Platform: {}", executionPlatform);

            switch (executionPlatform) {
                case "local-driver":
                    driver = initializeLocalDriver(browser);
                    break;
                case "local-driver-manager":
                    driver = initializeLocalDriverManager(browser);
                    break;
                case "cloud":
                    driver = initializeCloudDriver(browser);
                    break;
                case "docker":
                    driver = initializeDockerDriver(browser);
                    break;
                case "grid":
                    driver = initializeGridDriver(browser);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported execution platform: " + executionPlatform);
            }

            if (driver != null) {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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
        String driverPath;
        switch (browser.toLowerCase()) {
            case "chrome":
                driverPath = ConfigReader.getGlobal("chrome-driver-path");
                System.setProperty("webdriver.chrome.driver", driverPath);
                logger.info("Initializing Chrome browser using local driver at path: {}", driverPath);
                return new ChromeDriver();
            case "firefox":
                driverPath = ConfigReader.getGlobal("firefox-driver-path");
                System.setProperty("webdriver.gecko.driver", driverPath);
                logger.info("Initializing Firefox browser using local driver at path: {}", driverPath);
                return new FirefoxDriver();
            case "edge":
                driverPath = ConfigReader.getGlobal("edge-driver-path");
                System.setProperty("webdriver.edge.driver", driverPath);
                logger.info("Initializing Edge browser using local driver at path: {}", driverPath);
                return new EdgeDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private static WebDriver initializeLocalDriverManager(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().config().setClearDriverCache(true);
                WebDriverManager.chromedriver().setup();
                logger.info("Initializing Chrome browser using WebDriverManager.");
                return new ChromeDriver();
            case "firefox":
                WebDriverManager.firefoxdriver().config().setClearDriverCache(true);
                WebDriverManager.firefoxdriver().setup();
                logger.info("Initializing Firefox browser using WebDriverManager.");
                return new FirefoxDriver();
            case "edge":
                WebDriverManager.edgedriver().config().setClearDriverCache(true);
                WebDriverManager.edgedriver().setup();
                logger.info("Initializing Edge browser using WebDriverManager.");
                return new EdgeDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private static WebDriver initializeCloudDriver(String browser) {
        String cloudProvider = ConfigReader.getGlobal("cloud-provider").toLowerCase();

        switch (cloudProvider) {
            case "browserstack":
                return initializeBrowserStackDriver();
            default:
                throw new IllegalArgumentException("Unsupported cloud provider: " + cloudProvider);
        }
    }

    private static WebDriver initializeDockerDriver(String browser) throws MalformedURLException {
        String dockerHubURL = ConfigReader.getGlobal("docker-hub-url");
        DesiredCapabilities capabilities = new DesiredCapabilities();

        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless", "--disable-gpu");
                chromeOptions.setCapability("browserName", "chrome");
                capabilities.merge(chromeOptions);
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setCapability("browserName", "firefox");
                firefoxOptions.addArguments("--headless", "--disable-gpu");
                capabilities.merge(firefoxOptions);
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless", "--disable-gpu", "--no-sandbox");
                capabilities.merge(edgeOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        return new RemoteWebDriver(new URL(dockerHubURL), capabilities);
    }

    private static WebDriver initializeGridDriver(String browser) throws MalformedURLException {
        String seleniumGridUrl = ConfigReader.getGlobal("grid-url");

        // Define browser options
        switch (browser.toLowerCase()) {
            case "chrome": {
                ChromeOptions chromeOptions = new ChromeOptions();
                logger.info("Initializing Chrome browser on Selenium Grid at {}", seleniumGridUrl);
                return new RemoteWebDriver(new URL(seleniumGridUrl), chromeOptions);
            }
            case "firefox": {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                logger.info("Initializing Firefox browser on Selenium Grid at {}", seleniumGridUrl);
                return new RemoteWebDriver(new URL(seleniumGridUrl), firefoxOptions);
            }
            case "edge": {
                EdgeOptions edgeOptions = new EdgeOptions();
                logger.info("Initializing Edge browser on Selenium Grid at {}", seleniumGridUrl);
                return new RemoteWebDriver(new URL(seleniumGridUrl), edgeOptions);
            }
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }


    public static WebDriver initializeBrowserStackDriver() {
        MutableCapabilities capabilities = new MutableCapabilities();
        HashMap<String, Object> bstackOptions = new HashMap<>();

        // Fetch parameters
        String os = BrowserStackRunner.getOS();
        String browser = BrowserStackRunner.getBrowser();
        String osVersion = BrowserStackRunner.getOSVersion();
        String deviceName = BrowserStackRunner.getDeviceName();
        String browserVersion = BrowserStackRunner.getBrowserVersion();
        String username = ConfigReader.getBrowserStack("browserstack.username");
        String accessKey = ConfigReader.getBrowserStack("browserstack.accessKey");

        // Hub URL for BrowserStack
        String hubUrl = String.format("https://%s:%s@hub.browserstack.com/wd/hub", username, accessKey);

        if (deviceName != null && !deviceName.isEmpty()) {
            // Mobile Configuration
            capabilities.setCapability("browserName", browser);
            bstackOptions.put("deviceName", deviceName);
            bstackOptions.put("osVersion", osVersion);
        } else {
            // Desktop Configuration
            capabilities.setCapability("browserName", browser);
            bstackOptions.put("os", os);
            bstackOptions.put("osVersion", osVersion);
            bstackOptions.put("browserVersion", browserVersion);
        }

        // Common BrowserStack Options
        bstackOptions.put("userName", username);
        bstackOptions.put("accessKey", accessKey);
        bstackOptions.put("consoleLogs", "debug");
        bstackOptions.put("buildName", ConfigReader.getBrowserStack("browserstack.buildName"));
        bstackOptions.put("projectName", ConfigReader.getBrowserStack("browserstack.projectName"));

        capabilities.setCapability("bstack:options", bstackOptions);
        logger.info("Hub URL is "+hubUrl);
        logger.info("Intializing Driver with Capabilities : "+capabilities);
        try {
            WebDriver driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
            logger.info("BrowserStack WebDriver initialized successfully with capabilities: {}", capabilities);
            return driver;
        } catch (MalformedURLException e) {
            logger.error("Invalid BrowserStack Hub URL.", e);
            throw new RuntimeException("Failed to initialize BrowserStack driver.", e);
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
