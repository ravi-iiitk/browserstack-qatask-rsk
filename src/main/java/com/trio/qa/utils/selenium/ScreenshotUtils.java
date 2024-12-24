package com.trio.qa.utils.selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class ScreenshotUtils {

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String destination = "screenshots/" + screenshotName + ".png";
        try {
            FileUtils.copyFile(source, new File(destination));
            System.out.println("Screenshot saved: " + destination);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save screenshot.");
        }
        return destination;
    }
}
