package com.trio.qa.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/feature-files", // Path to your feature files
        glue = {"stepdefinitions.web.browserstack.qa.task.elpais"}, // Include only package paths
        plugin = {
                "pretty",                              // Prints Gherkin steps in console
                "html:target/cucumber-reports/cucumber-html-report.html",  // HTML report
                "json:target/cucumber-reports/cucumber.json",              // JSON report
                "junit:target/cucumber-reports/cucumber.xml"               // JUnit XML report
        },
        tags = "@smoke",                           // Tags to filter tests
        monochrome = true                          // For better console output
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
        // No additional code required; AbstractTestNGCucumberTests handles everything
}
