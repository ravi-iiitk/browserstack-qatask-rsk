package com.browserstack.qa.task.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.TestNG;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@CucumberOptions(
        features = "src/test/resources/feature-files",
        glue = {"stepdefinitions.web.browserstack.qa.task.elpais", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-html-report.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@smoke",
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    private static final ThreadLocal<String> threadLocalBrowser = new ThreadLocal<>();

    @Parameters("browser")
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        threadLocalBrowser.set(browser); // Store browser parameter for the current thread
    }

    public static String getBrowser() {
        return threadLocalBrowser.get(); // Retrieve browser parameter for the current thread
    }
}