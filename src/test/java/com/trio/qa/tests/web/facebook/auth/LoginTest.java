package com.trio.qa.tests.web.facebook.auth;

import com.trio.qa.base.WebBaseTest;
import com.trio.qa.config.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageobjects.web.facebook.auth.LoginPage;

import java.util.Map;

public class LoginTest extends WebBaseTest {

    @Test(description = "module:auth, submodule:login", dataProvider = "testDataProvider")
    public void testLogin(Map<String, Object> testData) {
        // Navigate to the login page

        // Initialize the LoginPage object
        LoginPage loginPage = new LoginPage(driver);

        // Perform login
        String username = (String) testData.get("username");
        String password = (String) testData.get("password");
        loginPage.login(username, password);

        // Validation
        if (testData.containsKey("expectedTitle")) {
            Assert.assertEquals(driver.getTitle(), testData.get("expectedTitle"), "Page title does not match!");
        } else if (testData.containsKey("expectedError")) {
            String actualError = loginPage.getErrorMessage();
            Assert.assertEquals(actualError, testData.get("expectedError"), "Error message does not match!");
        }
    }
}
