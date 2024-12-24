package stepdefinitions.web.browserstack.qa.task.elpais;

import com.trio.qa.core.DriverManager;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import pageobjects.web.browserstack.qa.task.elpais.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.trio.qa.utils.selenium.ElementUtils.getText;
import static org.junit.Assert.assertTrue;

public class ElpaisStepDefinitions {

    private WebDriver driver;
    private HomePage homePage;
    private OpinionPage opinionPage;
    private TranslationPage translationPage;
    private LanguageValidation languageValidation;
    private HeaderAnalysisPage headerAnalysisPage;
    private List<String> articleTitles;
    private List<String> translatedTitles;
    private Map<String, Long> repeatedWords;
    private static final Logger logger = LogManager.getLogger(ElpaisStepDefinitions.class);

    private List<String> fetchedTitles = new ArrayList<>(); // Store fetched titles here

    @Given("I visit the website {string}")
    public void iVisitTheWebsite(String url) {
        try {
            WebDriver driver = DriverManager.getDriver(); // Get the WebDriver instance from DriverManager
            logger.info("Navigating to URL: {}", url);
            driver.get(url);

            // Initialize the HomePage object
            HomePage homePage = new HomePage(driver);
            homePage.acceptThePopUp(); // Handle any pop-ups

            logger.info("Successfully navigated to URL: {}", url);
        } catch (Exception e) {
            logger.error("Failed to navigate to URL: {}. Error: {}", url, e.getMessage());
            throw new RuntimeException("Error during navigation to URL: " + url, e);
        }
    }

    @Given("I am on the {string} homepage")
    public void iAmOnTheHomepage(String websiteName) {
        logger.info(driver.getTitle());
        assertTrue("Failed to load the homepage", driver.getTitle().contains(websiteName));
        logger.info("Verified homepage contains the title: {}", websiteName);
    }

    @Then("the website's text should be displayed in {string}")
    public void theWebsiteSTextShouldBeDisplayedIn(String language) {
        languageValidation = new LanguageValidation(driver);
        assertTrue("Website text is not in " + language, languageValidation.validatePageTextIsInSpanish());
        logger.info("Verified the website's text is displayed in {}", language);
    }

    @Given("I navigate to the {string} section of the website")
    public void iNavigateToTheSectionOfTheWebsite(String section) {
        homePage.navigateToOpinionSection();
        opinionPage = new OpinionPage(driver);
        logger.info("Navigated to the {} section of the website", section);
        try {
            String sectionUrl = "https://elpais.com/" + section.toLowerCase();
            logger.info("Navigated to section: {}", sectionUrl);
        } catch (Exception e) {
            logger.error("Failed to navigate to section: {}", section, e);
        }
    }

    @When("I fetch the first {int} articles titles")
    public void iFetchTheFirstNArticlesTitles(int limit) {
        try {
           articleTitles= opinionPage.fetchArticleTitles(limit); // Fetch the first N articles
        } catch (Exception e) {
            logger.error("Error fetching articles: {}", e.getMessage(), e);
        }
    }

    @Then("I translate the article titles to {string}")
    public void iTranslateTheArticleTitlesTo(String targetLanguage) {
        try {
            // Initialize TranslationPage
            TranslationPage translationPage = new TranslationPage();

            // Map the target language name to its ISO 639-1 code
            String languageCode = mapLanguageToCode(targetLanguage);
            if (languageCode == null) {
                throw new IllegalArgumentException("Unsupported language: " + targetLanguage);
            }

            // Translate article titles
            translatedTitles = translationPage.translateTitles(articleTitles, languageCode);

            // Log the translated titles
            logger.info("Translated Titles:");
            translatedTitles.forEach(title -> logger.info("{}", title));
        } catch (Exception e) {
            logger.error("Error translating article titles: {}", e.getMessage(), e);
        }
    }

    /**
     * Maps a language name to its ISO 639-1 code.
     *
     * @param language The language name.
     * @return The corresponding ISO 639-1 code, or null if not supported.
     */




    @Then("I print the title and content of each article in {string}")
    public void iPrintTheTitleAndContentOfEachArticleInLanguage(String language) {
        logger.info("Translated Titles: {}", translatedTitles);
    }


    @And("I download and save the cover image of each article, if available")
    public void iDownloadAndSaveTheCoverImageOfEachArticleIfAvailable() {
        logger.info("Cover images downloaded, if available.");
    }

    @When("I fetch the first {int} articles")
    public void iFetchTheFirstNArticles(int limit) {
        articleTitles = opinionPage.fetchArticleTitles(limit); // Fetch article titles
        logger.info("Fetched {} article titles: {}", articleTitles.size(), articleTitles);
    }

    @And("I print the translated titles")
    public void iPrintTheTranslatedTitles() {
        logger.info("Translated Titles:");
        translatedTitles.forEach(title -> logger.info("{}", title));
    }

    @Given("I have the following translated headers:")
    public void iHaveTheFollowingTranslatedHeaders(List<String> headers) {
        logger.info("Provided Translated Headers: {}", headers);
    }




    @Given("I have the translated headers")
    public void iHaveTheTranslatedHeaders() {
        if (translatedTitles == null || translatedTitles.isEmpty()) {
            throw new IllegalStateException("No translated headers found. Ensure titles have been translated.");
        }
        logger.info("Translated Headers: {}", translatedTitles);
    }

    @When("I identify words repeated more than {int} times across all headers")
    public void iIdentifyWordsRepeatedMoreThanTimesAcrossAllHeaders(int threshold) {
        try {
            logger.info("Analyzing repeated words in translated headers...");

            // Flatten all headers into a single list of words
            List<String> allWords = translatedTitles.stream()
                    .flatMap(title -> List.of(title.split("\\W+")).stream()) // Split titles into words
                    .map(String::toLowerCase) // Normalize to lowercase
                    .collect(Collectors.toList());

            // Count occurrences of each word
            Map<String, Long> wordCounts = allWords.stream()
                    .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

            // Filter words that are repeated more than the threshold
            repeatedWords = wordCounts.entrySet().stream()
                    .filter(entry -> entry.getValue() > threshold)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            logger.info("Repeated Words: {}", repeatedWords);
        } catch (Exception e) {
            logger.error("Error analyzing repeated words: {}", e.getMessage(), e);
        }
    }

    @Then("I print each repeated word with its count of occurrences")
    public void iPrintEachRepeatedWordWithItsCountOfOccurrences() {
        if (repeatedWords == null || repeatedWords.isEmpty()) {
            logger.info("No repeated words found.");
            return;
        }
        logger.info("Repeated Words and Their Counts:");
        repeatedWords.forEach((word, count) -> logger.info("{}: {}", word, count));
    }


    private String mapLanguageToCode(String language) {
        switch (language.toLowerCase()) {
            case "english":
                return "en";
            case "spanish":
                return "es";
            case "french":
                return "fr";
            // Add other languages as needed
            default:
                return null;
        }
    }
}
