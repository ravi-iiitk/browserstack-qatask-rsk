package com.browserstack.qa.task.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {


    private static final Properties globalProperties = new Properties();
    private static final Properties environmentProperties = new Properties();
    private static final Properties browserStackProperties = new Properties();
    private static String environment;
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    static {
        try {
            // Load global configuration
            FileInputStream globalConfig = new FileInputStream("src/main/resources/config/config.properties");
            globalProperties.load(globalConfig);
            logger.info("Loaded global configuration from config.properties.");

            // Determine the environment (default to 'qa')
            environment = ConfigReader.getGlobal("env").toLowerCase();

            // Load environment-specific configuration
            FileInputStream envConfig = new FileInputStream("src/main/resources/config/" + environment + ".properties");
            environmentProperties.load(envConfig);
            logger.info("Loaded {} environment configuration.", environment);

            // Load BrowserStack-specific configuration (if present)
            if(ConfigReader.getGlobal("execution-platform").equalsIgnoreCase("cloud") && ConfigReader.getGlobal("cloud-provider").equalsIgnoreCase("browserstack"))
            {
                FileInputStream bsConfig = new FileInputStream("src/main/resources/config/config-browserstack.properties");
                browserStackProperties.load(bsConfig);
                logger.info("Loaded BrowserStack-specific configuration from config-browserstack.properties.");
            }


        } catch (IOException e) {
            logger.error("Failed to load configuration files.", e);
            throw new RuntimeException("Failed to load configuration files.", e);
        }
    }

    /**
     * Retrieves a property value, checking environment-specific properties first, then global properties.
     *
     * @param key the property key
     * @return the property value or null if not found
     */
    public static String get(String key) {
        String value = environmentProperties.getProperty(key);
        if (value == null) {
            value = globalProperties.getProperty(key);
        }
        return value;
    }

    /**
     * Retrieves a property value from global properties only.
     *
     * @param key the property key
     * @return the property value or null if not found
     */
    public static String getGlobal(String key) {
        return globalProperties.getProperty(key);
    }

    /**
     * Retrieves a property value from environment-specific properties only.
     *
     * @param key the property key
     * @return the property value or null if not found
     */
    public static String getEnvironmentSpecific(String key) {
        return environmentProperties.getProperty(key);
    }

    /**
     * Retrieves a property value from BrowserStack-specific configuration.
     *
     * @param key the property key
     * @return the property value or null if not found
     */
    public static String getBrowserStack(String key) {
        return browserStackProperties.getProperty(key);
    }

    /**
     * Gets the current environment.
     *
     * @return the environment name
     */
    public static String getEnvironment() {
        return environment;
    }

    /**
     * Gets the browser name for the current test execution.
     * Prioritizes the browser passed as a TestNG parameter or system property.
     *
     * @return the browser name (default: "chrome")
     */
    public static String getBrowser() {
        String browser = System.getProperty("browser");
        if (browser == null || browser.isEmpty()) {
            browser = getGlobal("browser"); // Fallback to config file
        }
        if (browser == null || browser.isEmpty()) {
            browser = "chrome"; // Default browser
        }
        logger.info("Browser Provided in Configuration: {}", browser);
        return browser;
    }

    /**
     * Constructs the BrowserStack Hub URL dynamically.
     *
     * @return the BrowserStack Hub URL
     */
    public static String getBrowserStackHubUrl() {
        String username = getBrowserStack("browserstack-username");
        String accessKey = getBrowserStack("browserstack-access-key");
        if (username == null || accessKey == null) {
            throw new IllegalStateException("BrowserStack credentials are not properly configured.");
        }
        return String.format("http://%s:%s@hub-cloud.browserstack.com/wd/hub", username, accessKey);
    }
}
