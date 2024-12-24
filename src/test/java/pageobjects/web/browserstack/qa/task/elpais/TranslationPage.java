package pageobjects.web.browserstack.qa.task.elpais;

import com.trio.qa.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TranslationPage {

    private static final Logger logger = LogManager.getLogger(TranslationPage.class);
    private final String apiKey;
    private final String translationApiUrl = "https://translation.googleapis.com/language/translate/v2"; // Google Translate API URL

    /**
     * Constructor that fetches the API key dynamically from the configuration.
     */
    public TranslationPage() {
        this.apiKey = ConfigReader.getGlobal("GOOGLE-API-KEY"); // Fetch API key from config file
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key is missing in config.properties.");
        }
        logger.info("Initialized TranslationPage with API key from config file.");
    }

    /**
     * Translates a single text string to the target language.
     *
     * @param text           The text to be translated.
     * @param targetLanguage The language to which the text should be translated.
     * @return The translated text, or null if translation fails.
     */
    public String translateText(String text, String targetLanguage) {
        try {
            logger.info("Translating text: '{}' to language: {}", text, targetLanguage);

            // Sanitize text
            String sanitizedText = text.replaceAll("[\\n\\r]", " ");

            // Create the request payload
            JSONObject payload = new JSONObject();
            payload.put("q", sanitizedText); // For Google Translate API v2
            payload.put("target", targetLanguage);

            // Set up the connection
            URL url = new URL(translationApiUrl + "?key=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Log request details
            logger.info("Request Payload: {}", payload.toString());
            logger.info("Request URL: {}", url);

            // Send the request payload
            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // Read the response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject jsonResponse = new JSONObject(response);
                String translatedText = jsonResponse.getJSONObject("data")
                        .getJSONArray("translations")
                        .getJSONObject(0)
                        .getString("translatedText");
                logger.info("Translation successful. Translated text: '{}'", translatedText);
                return translatedText;
            } else {
                logger.error("Translation failed. Response code: {}, Message: {}", connection.getResponseCode(),
                        connection.getResponseMessage());
                String errorResponse = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                logger.error("Error Response Body: {}", errorResponse);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred during translation: {}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * Translates a list of texts to the target language.
     *
     * @param texts          A list of texts to be translated.
     * @param targetLanguage The language to which the texts should be translated.
     * @return A list of translated texts.
     */
    public List<String> translateTitles(List<String> texts, String targetLanguage) {
        logger.info("Translating {} titles to language: {}", texts.size(), targetLanguage);
        return texts.stream()
                .map(text -> translateText(text, targetLanguage))
                .collect(Collectors.toList());
    }
}
