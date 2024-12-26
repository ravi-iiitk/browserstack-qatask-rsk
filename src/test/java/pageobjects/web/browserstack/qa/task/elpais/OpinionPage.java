package pageobjects.web.browserstack.qa.task.elpais;

import com.browserstack.qa.task.utils.misc.TranslationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.browserstack.qa.task.utils.selenium.ElementUtils.getText;

public class OpinionPage {
    private final WebDriver driver;
    private static final Logger logger = LogManager.getLogger(OpinionPage.class);

    @FindBy(xpath = "//article//h2")
    private List<WebElement> articleHeaders;

    @FindBy(xpath = "//article//p")
    private List<WebElement> articleContents;

    @FindBy(xpath = "//article//img")
    private List<WebElement> coverImages;

    private List<String> fetchedTitles = new ArrayList<>();
    private List<String> fetchedContents = new ArrayList<>();

    public OpinionPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        logger.info("Initialized OpinionPage elements.");
    }

    /**
     * Fetches the titles and contents of the first N articles.
     *
     * @param limit The maximum number of articles to fetch.
     */
    public void fetchArticles(int limit) {
        if (articleHeaders.isEmpty()) {
            logger.warn("No articles found on the Opinion page.");
            return;
        }

        int articleCount = Math.min(articleHeaders.size(), limit);
        logger.info("Found {} articles. Fetching the first {} articles.", articleHeaders.size(), articleCount);

        for (int i = 0; i < articleCount; i++) {
            try {
                // Fetch title
                String title = getText(articleHeaders.get(i), driver).trim();
                fetchedTitles.add(title);
                logger.info("Article {} Title: {}", i + 1, title);

                // Fetch content
                String content = getText(articleContents.get(i), driver).trim();
                fetchedContents.add(content);
                logger.info("Article {} Content: {}", i + 1, content);

                // Save cover images (optional, not part of validation)
                if (i < coverImages.size()) {
                    String imageUrl = coverImages.get(i).getAttribute("src");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        downloadImage(imageUrl, "article_" + (i + 1) + "_cover.jpg");
                    } else {
                        logger.warn("No cover image available for Article {}", i + 1);
                    }
                }
            } catch (Exception e) {
                logger.error("Error fetching Article {}: {}", i + 1, e.getMessage(), e);
            }
        }
    }

    public List<String> fetchArticleTitles(int limit) {
        if (articleHeaders.isEmpty()) {
            logger.warn("No articles found on the Opinion page.");
            return List.of();
        }

        int articleCount = Math.min(articleHeaders.size(), limit);
        logger.info("Fetching titles for the first {} articles.", articleCount);

        return articleHeaders.stream()
                .limit(articleCount)
                .map(element -> getText(element, driver).trim())
                .collect(Collectors.toList());
    }
    /**
     * Validates whether the fetched articles are in the specified language.
     *
     * @param language The language to validate (e.g., "Spanish").
     */
    public void validateArticles(String language) {
        if (fetchedTitles.isEmpty() || fetchedContents.isEmpty()) {
            logger.warn("No articles fetched to validate.");
            return;
        }

        int validSpanishCount = 0;

        for (int i = 0; i < fetchedTitles.size(); i++) {
            String title = fetchedTitles.get(i);
            String content = fetchedContents.get(i);

            boolean isTitleSpanish = TranslationUtils.isSpanishText(title);
            boolean isContentSpanish = TranslationUtils.isSpanishText(content);

            if (isTitleSpanish && isContentSpanish) {
                logger.info("Article {} is in Spanish.", i + 1);
                validSpanishCount++;
            } else {
                if (!isTitleSpanish) {
                    logger.warn("Non-Spanish Title: {}", title);
                }
                if (!isContentSpanish) {
                    logger.warn("Non-Spanish Content: {}", content);
                }
            }
        }

        // Summary
        double spanishPercentage = ((double) validSpanishCount / fetchedTitles.size()) * 100;
        logger.info("{} out of {} articles ({:.2f}%) are in Spanish.", validSpanishCount, fetchedTitles.size(), spanishPercentage);

        if (spanishPercentage < 80.0) {
            logger.warn("Less than 80% of the articles are in Spanish. Verification failed.");
        } else {
            logger.info("Verification passed. Majority of the articles are in Spanish.");
        }
    }

    private void downloadImage(String imageUrl, String fileName) {
        try {
            URL url = new URL(imageUrl);
            File destination = new File("src/test/resources/output-data/web/elpais/images/" + fileName);
            Files.copy(url.openStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Image saved as: {}", fileName);
        } catch (Exception e) {
            logger.error("Failed to download image: {}", imageUrl, e);
        }
    }
}
