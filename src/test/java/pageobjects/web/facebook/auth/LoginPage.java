package pageobjects.web.facebook.auth;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static com.trio.qa.utils.selenium.ElementUtils.clickElement;
import static com.trio.qa.utils.selenium.ElementUtils.enterText;

public class LoginPage {

    private WebDriver driver;

    // Locators using @FindBy annotation
    @FindBy(id = "email")
    private WebElement usernameField;

    @FindBy(id = "pass")
    private WebElement passwordField;

    @FindBy(name = "login")
    private WebElement loginButton;

    @FindBy(css = ".uiHeaderTitle") // Example locator for error message
    private WebElement errorMessage;

    // Constructor to initialize the PageFactory elements
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Actions for the Login Page

    /**
     * Enters the username in the username field.
     *
     * @param username User's username
     */
    public void enterUsername(String username) {
        enterText(usernameField,username, driver);
    }

    /**
     * Enters the password in the password field.
     *
     * @param password User's password
     */
    public void enterPassword(String password) {
        enterText(passwordField,password, driver);
    }

    /**
     * Clicks the login button.
     */
    public void clickLoginButton() {
       clickElement(loginButton,driver);
    }

    /**
     * Gets the error message displayed on login failure.
     *
     * @return Error message as a string
     */
    public String getErrorMessage() {
        return errorMessage.getText();
    }

    /**
     * Performs login action.
     *
     * @param username User's username
     * @param password User's password
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
}
