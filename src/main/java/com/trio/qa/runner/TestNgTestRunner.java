package com.trio.qa.runner;


import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class TestNgTestRunner {

    public static void main(String[] args) {
        TestNG testng = new TestNG();

        // Dynamically setting the testng.xml file
        List<String> suites = new ArrayList<>();
        suites.add("src/test/resources/testng.xml");
        testng.setTestSuites(suites);

        // Set thread count dynamically
        int threadCount = Integer.parseInt(System.getProperty("threads", "2")); // Default: 2 threads
        testng.setSuiteThreadPoolSize(threadCount);

        // Execute the tests
        testng.run();
    }
}
