package com.browserstack.qa.task.unittests;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class FrameworkEdgeSetup {

    public static void main(String[] args) {
        try {
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.setCapability("browserName", "edge");
            edgeOptions.setCapability("platformName", "LINUX"); // Ensure compatibility for Docker
            edgeOptions.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
            edgeOptions.addArguments("--window-size=1920,1080");
            edgeOptions.addArguments("--remote-debugging-port=9222");

            RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), edgeOptions);

            driver.get("https://www.google.com");
            System.out.println("Page Title: " + driver.getTitle());

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
