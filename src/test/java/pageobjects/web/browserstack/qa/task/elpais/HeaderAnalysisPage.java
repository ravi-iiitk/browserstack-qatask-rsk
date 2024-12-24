package pageobjects.web.browserstack.qa.task.elpais;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HeaderAnalysisPage {

    private static final Logger logger = LogManager.getLogger(HeaderAnalysisPage.class);

    /**
     * Analyzes headers and counts the occurrences of each word across all headers.
     *
     * @param headers An array of strings representing the headers.
     * @return A map where keys are words and values are their counts.
     */
    public Map<String, Integer> analyzeHeaders(String[] headers) {
        Map<String, Integer> wordCount = new HashMap<>();

        logger.info("Starting header analysis...");
        for (String header : headers) {
            logger.debug("Analyzing header: {}", header);
            // Split the header into words using non-word characters as delimiters
            String[] words = header.split("\\W+");
            for (String word : words) {
                if (!word.isEmpty()) {
                    // Convert to lowercase for case-insensitive matching
                    word = word.toLowerCase();
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }
        logger.info("Completed header analysis. Word count: {}", wordCount);
        return wordCount;
    }

    /**
     * Filters out words that occur more than the specified threshold.
     *
     * @param wordCount A map of words and their counts.
     * @param threshold The minimum number of occurrences to consider a word repeated.
     * @return A map of repeated words and their counts.
     */
    public Map<String, Integer> getRepeatedWords(Map<String, Integer> wordCount, int threshold) {
        Map<String, Integer> repeatedWords = new HashMap<>();

        logger.info("Filtering words with occurrences greater than {}...", threshold);
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() > threshold) {
                repeatedWords.put(entry.getKey(), entry.getValue());
            }
        }
        logger.info("Filtered repeated words: {}", repeatedWords);
        return repeatedWords;
    }
}
