package com.browserstack.qa.task.unittests;


import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;


import java.net.URL;
import java.util.HashMap;

public class BrowserStackTimeoutTest {

    public static void main(String[] args) {
        WebDriver driver = null;

        try {
            // Set up capabilities
            MutableCapabilities capabilities = new MutableCapabilities();
            HashMap<String, Object> bstackOptions = new HashMap<>();
            capabilities.setCapability("browserName", "Chrome");
            bstackOptions.put("os", "Windows");
            bstackOptions.put("osVersion", "11");
            bstackOptions.put("browserVersion", "latest");
            bstackOptions.put("userName", "ravi_Wt0Od2");
            bstackOptions.put("accessKey", "AUqT9avXtnN9sBvzMNbD");
            bstackOptions.put("consoleLogs", "debug");
            bstackOptions.put("buildName", "BrowserStack Test Build");
            bstackOptions.put("projectName", "Timeout Verification");
            capabilities.setCapability("bstack:options", bstackOptions);

            // Define BrowserStack Hub URL
            String hubUrl = "https://ravishankar_MVw4eS:6p1zz1NL11ceghFKqwwD@hub.browserstack.com/wd/hub";

            // Initialize WebDriver
            driver = new RemoteWebDriver(new URL(hubUrl), capabilities);

            // Perform test actions
            System.out.println("Navigating to example website...");
            driver.get("https://www.example.com");
            System.out.println("Page Title: " + driver.getTitle());

            // Simulate actions to ensure no timeout occurs
            System.out.println("Navigating to Google...");
            driver.get("https://www.google.com");
            System.out.println("Page Title: " + driver.getTitle());

            // Simulate a wait to check session stability
            System.out.println("Simulating a delay to test session timeout...");
            Thread.sleep(10000); // Wait for 10 seconds (adjust if needed)
            System.out.println("Test completed successfully.");

        } catch (Exception e) {
            System.err.println("Error occurred during test execution: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
                System.out.println("BrowserStack session terminated.");
            }
        }
    }
}
