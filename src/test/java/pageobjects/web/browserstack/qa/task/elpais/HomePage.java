package pageobjects.web.browserstack.qa.task.elpais;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

import static com.trio.qa.utils.selenium.ElementUtils.clickElement;
import static com.trio.qa.utils.selenium.ElementUtils.getText;

public class HomePage {

    private final WebDriver driver;
    private static final Logger logger = LogManager.getLogger(HomePage.class);

    @FindBy(xpath = "//body//*[text()]") // Find all elements on the page with visible text
    private List<WebElement> pageTextElements;

    @FindBy(xpath = "//html") // Accessing the lang attribute of the <html> tag
    private WebElement htmlElement;

    @FindBy(id = "didomi-notice-agree-button") // Accessing the lang attribute of the <html> tag
    private WebElement accptBtn;


    @FindBy(linkText = "Opinión") // Link to navigate to the Opinion section
    private WebElement opinionSectionLink;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        logger.info("Initialized HomePage elements.");
    }

    /**
     * Basic check to verify if the website's language is set to Spanish using the HTML lang attribute.
     *
     * @return true if the language is Spanish (lang="es"), false otherwise.
     */
    public boolean isWebsiteInSpanishBasic() {
        String lang = htmlElement.getAttribute("lang");
        logger.info("HTML lang attribute value: {}", lang);
        return "es".equalsIgnoreCase(lang);
    }

    /**
     * Navigates to the Opinion section of the website.
     */
    public void navigateToOpinionSection() {
        logger.info("Navigating to the Opinion section.");
        clickElement(opinionSectionLink, driver);
        logger.info("Successfully navigated to the Opinion section.");
    }

    public void acceptThePopUp()
    {
        clickElement(accptBtn,driver);
    }

    /**
     * Complete validation of the website's language based on visible text elements on the page.
     * It checks for the presence of common Spanish words.
     *
     * @return true if the majority of the text contains Spanish words, false otherwise.
     */
    public boolean isWebsiteInSpanishComplete() {
        logger.info("Starting complete language validation for the webpage.");
        for (WebElement element : pageTextElements) {
            String text = getText(element, driver).trim();
            if (!text.isEmpty() && !containsSpanishWords(text)) {
                logger.warn("Non-Spanish text detected: {}", text);
                return false;
            }
        }
        logger.info("All visible text on the page validated as Spanish.");
        return true;
    }

    /**
     * Helper method to determine if a given text contains common Spanish words.
     *
     * @param text The text to validate.
     * @return true if common Spanish words are found, false otherwise.
     */
    private boolean containsSpanishWords(String text) {
        String[] commonSpanishWords = {"el", "la", "de", "y", "en", "un", "una", "por", "que", "es", "para", "con"};
        for (String word : commonSpanishWords) {
            if (text.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }
}
