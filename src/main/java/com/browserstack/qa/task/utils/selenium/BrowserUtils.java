package com.browserstack.qa.task.utils.selenium;

import org.openqa.selenium.WebDriver;

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
}
