package com.browserstack.qa.task.utils.selenium;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String destination = "screenshots/" + screenshotName + ".png";
        try {
            FileUtils.copyFile(source, new File(destination));
            logger.info("Screenshot saved: " + destination);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save screenshot.");
        }
        return destination;
    }
}
