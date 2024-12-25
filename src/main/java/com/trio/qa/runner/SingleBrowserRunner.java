package com.trio.qa.runner;

import org.testng.annotations.BeforeClass;

public class SingleBrowserRunner extends TestRunner {

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        // Set the browser explicitly for single-browser execution
        setUp("chrome");
    }
}
