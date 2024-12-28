package com.browserstack.qa.task.utils.selenium;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BrowserUtils {

    public static void navigateTo(WebDriver driver, String url) {
        driver.get(url);
    }

    public static void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
    }

    public static void navigateBack(WebDriver driver) {
        driver.navigate().back();
    }

    public static void navigateForward(WebDriver driver) {
        driver.navigate().forward();
    }

    public static void maximizeWindow(WebDriver driver) {
        driver.manage().window().maximize();
    }
    public static void waitForPageToLoad(WebDriver driver, int timeoutInSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until((ExpectedCondition<Boolean>) wd -> {
            JavascriptExecutor js = (JavascriptExecutor) wd;
            return js.executeScript("return document.readyState").toString().equals("complete");
        });
    }
    public static void scrollDownTwice(WebDriver driver) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

            // Scroll down a little
            jsExecutor.executeScript("window.scrollBy(0, 100);");
            Thread.sleep(500); // Pause to simulate manual delay

            // Scroll down a little again
            jsExecutor.executeScript("window.scrollBy(0, 100);");
            Thread.sleep(500); // Pause to simulate manual delay
        } catch (InterruptedException e) {
            System.err.println("Interrupted during scrolling: " + e.getMessage());
        }
    }
}
