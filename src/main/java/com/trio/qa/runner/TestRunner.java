package com.trio.qa.runner;

import com.trio.qa.config.ConfigReader;
import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.testng.CucumberOptions;
import org.junit.runner.RunWith;

import io.cucumber.junit.platform.engine.Cucumber;

@Cucumber

@io.cucumber.junit.CucumberOptions(
        features = "src/test/resources/feature-files/", // Path to your feature files
        glue = {"stepdefinitions.web.browserstack.qa.task.elpais", "hooks"}, // Include only package paths
        plugin = {
                "pretty",                              // Prints Gherkin steps in console
                "html:target/cucumber-reports/cucumber-html-report.html",  // HTML report
                "json:target/cucumber-reports/cucumber.json",              // JSON report
                "junit:target/cucumber-reports/cucumber.xml"               // JUnit XML report
        },
        tags = "@smoke",                           // Tags to filter tests
        monochrome = true                          // For better console output
)
public class TestRunner {
    // No need for a main method when using JUnit 5
}
