package com.browserstack.qa.task.utils.misc;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Detection;

public class TranslationUtils {
    private static Translate translate;

    static {
        translate = TranslateOptions.newBuilder()
                .setApiKey("AIzaSyAqXhENvleTBvsj_RJQBIobfc9IyZCeR-8") // Replace with your API key
                .build()
                .getService();
    }

    /**
     * Detects if the given text is in Spanish.
     *
     * @param text The text to detect.
     * @return True if the text is in Spanish, false otherwise.
     */
    public static boolean isSpanishText(String text) {
        try {
            Detection detection = translate.detect(text);
            String detectedLanguage = detection.getLanguage();
            return "es".equals(detectedLanguage); // Check if the language is Spanish
        } catch (Exception e) {
            System.err.println("Error detecting language: " + e.getMessage());
            return false;
        }
    }
}
