package com.trio.qa.utils.selenium;

import com.trio.qa.config.ConfigReader;
import com.trio.qa.utils.exception.CustomExceptions;
import com.trio.qa.utils.exception.ExceptionHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ElementUtils {

    public static void clickElement(WebElement element, WebDriver driver) {
        waitForElementToBeVisible(element,driver);
        waitForElementToBeClickable(element,driver);
        element.click();
    }

    public static void enterText(WebElement element, String text, WebDriver driver) {
        waitForElementToBeVisible(element,driver);
        waitForElementToBeClickable(element,driver);
        element.clear();
        element.sendKeys(text);
    }

    public static String getText(WebElement element, WebDriver driver) {
        waitForElementToBeVisible(element,driver);
        return element.getText();
    }

    public static String getAttribute(WebElement element, String attribute) {
        return element.getAttribute(attribute);
    }

    public static boolean isElementDisplayed(WebElement element, WebDriver driver) {
        waitForElementToBeVisible(element, driver);
        return element.isDisplayed();
    }

    public static void waitForElementToBeClickable(WebElement element, WebDriver driver)
    {
        try{
            WebDriverWait elementClickableWait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(ConfigReader.get("element-clickable-timeout"))) );
            elementClickableWait.until(ExpectedConditions.elementToBeClickable(element));
        }
        catch (CustomExceptions.ElementNotClickableException exception)
        {
            ExceptionHandler.handleException(exception,driver);
            throw new CustomExceptions.ElementNotClickableException("Element is not clickable -"+element.getAccessibleName());
        }

    }

    public static void waitForElementToBeVisible(WebElement element, WebDriver driver)
    {
        try
        {
            WebDriverWait elementPresentWait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(ConfigReader.get("element-visibility-timeout"))) );
            elementPresentWait.until(ExpectedConditions.visibilityOf(element));
        }
        catch (CustomExceptions.ElementNotVisibleException exception)
        {
            ExceptionHandler.handleException(exception,driver);
            throw new CustomExceptions.ElementNotVisibleException("This Element is not visible -"+element.getAccessibleName());
        }

    }
}
