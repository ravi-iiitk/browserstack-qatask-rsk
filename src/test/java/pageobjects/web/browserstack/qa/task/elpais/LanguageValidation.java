package pageobjects.web.browserstack.qa.task.elpais;

import com.browserstack.qa.task.runner.BrowserStackRunner;
import com.browserstack.qa.task.utils.misc.TranslationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static com.browserstack.qa.task.utils.selenium.ElementUtils.getText;

public class LanguageValidation {
    private WebDriver driver;

    public LanguageValidation(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Iteratively traverses the DOM and collects up to 500 visible text elements.
     *
     * @param rootElement The root element to start traversal.
     * @return A list of up to 500 extracted visible text from the webpage.
     */
    private static final Logger logger = LogManager.getLogger(LanguageValidation.class);
    private List<String> collectLimitedText(WebElement rootElement) {
        Deque<WebElement> stack = new LinkedList<>(); // Stack for iterative traversal
        List<String> limitedText = new ArrayList<>(); // List to store up to 500 pieces of text
        stack.push(rootElement);

        logger.info("Starting traversal to collect up to 500 text elements...");
        while (!stack.isEmpty() && limitedText.size() < 100) {
            WebElement currentElement = stack.pop();
            try {
                String text = currentElement.getText().trim();

                if (!text.isEmpty()) {
                    limitedText.add(text); // Add text to the list
                    logger.info("Collected text: " + text); // Log collected text
                }

                // Add child elements to the stack
                JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                Long childCount = (Long) jsExecutor.executeScript("return arguments[0].children.length;", currentElement);

                for (int i = 0; i < childCount; i++) {
                    try {
                        WebElement childElement = (WebElement) jsExecutor.executeScript(
                                "return arguments[0].children[" + i + "];", currentElement);
                        stack.push(childElement);
                    } catch (StaleElementReferenceException e) {
                        logger.warn("Child element became stale while accessing: " + e.getMessage());
                    }
                }
            } catch (StaleElementReferenceException e) {
                logger.warn("Skipped stale element: " + e.getMessage());
            }
        }

        logger.info("Finished collecting text. Collected " + limitedText.size() + " elements.");
        return limitedText;
    }


    /**
     * Validates if at least 80% of the collected text is in Spanish.
     *
     * @param collectedText A list of visible text from the webpage.
     * @return True if at least 80% of the text is in Spanish, False otherwise.
     */
    private boolean is80PercentSpanish(List<String> collectedText) {
        int totalTextCount = collectedText.size();
        int spanishTextCount = 0;

        for (String text : collectedText) {
            if (TranslationUtils.isSpanishText(text)) {
                spanishTextCount++;
            } else {
                logger.warn("Non-Spanish text detected: " + text); // Log non-Spanish text
            }
        }

        double spanishPercentage = ((double) spanishTextCount / totalTextCount) * 100;
        logger.info("Total Text: " + totalTextCount + " | Spanish Text: " + spanishTextCount + " (" + spanishPercentage + "%)");

        return spanishPercentage >= 80.0; // Check if 80% or more text is in Spanish
    }

    /**
     * Starts the validation process for the entire page text.
     *
     * @return True if the page text passes the validation, False otherwise.
     */
    public boolean validatePageTextIsInSpanish() {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            WebElement bodyElement = (WebElement) jsExecutor.executeScript("return document.body;");

            // Collect up to 500 text elements
            List<String> collectedText = collectLimitedText(bodyElement);

            // Validate if at least 80% of the text is in Spanish
            return is80PercentSpanish(collectedText);
        } catch (Exception e) {
            System.err.println("Error during DOM traversal or validation: " + e.getMessage());
            logger.error(e.getMessage());
            return false;
        }
    }
}
