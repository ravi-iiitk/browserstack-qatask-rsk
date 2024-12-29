package com.browserstack.qa.task.runner;

import com.browserstack.qa.task.config.ConfigReader;
import com.browserstack.qa.task.reporting.LogSetup;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(TestRunner.class);
    private static final ThreadLocal<String> threadLocalBrowser = new ThreadLocal<>();

    @Parameters("browser")
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional String browser) {
        if(browser ==null)
        {
            browser = ConfigReader.getBrowser();
        }
        else
        {
            logger.info("Browser Provided in TestNg Xml for this thread -:" + browser);
        }
        threadLocalBrowser.set(browser); // Store browser parameter for the current thread
    }

    public static String getBrowser() {
        return threadLocalBrowser.get(); // Retrieve browser parameter for the current thread
    }
}