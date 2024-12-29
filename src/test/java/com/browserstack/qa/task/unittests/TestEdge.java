package com.browserstack.qa.task.unittests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class TestEdge {
    public static void main(String[] args) {
        try {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.merge(options);
            WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);

            driver.get("https://www.google.com");
            System.out.println("Page Title: " + driver.getTitle());
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
