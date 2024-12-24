package com.trio.qa.utils.exception;


import com.trio.qa.utils.selenium.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class ExceptionHandler {

    private static final Logger logger = LogManager.getLogger(ExceptionHandler.class);

    public static void handleException(Exception e, WebDriver driver) {
        // Log the exception
        logger.error("Exception occurred: ", e);

        // Capture a screenshot if WebDriver is not null
        if (driver != null) {
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "Exception_" + System.currentTimeMillis());
            logger.info("Screenshot captured for exception: " + screenshotPath);
        }

        // Optional: Add exception details to your reporting system
        // For example, using Allure:

        // Fail the test explicitly (if applicable)
        throw new RuntimeException(e);
    }
}
