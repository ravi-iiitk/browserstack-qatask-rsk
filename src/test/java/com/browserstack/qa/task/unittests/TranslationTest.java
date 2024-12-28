package com.browserstack.qa.task.unittests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.browserstack.qa.task.utils.misc.TranslationUtils.isSpanishText;

public class TranslationTest {
    private static final Logger logger = LogManager.getLogger(TranslationTest.class);
    public static void main(String[] args) {
        String testText = "Hola, ¿cómo estás?";
        boolean isSpanish = isSpanishText(testText);
        logger.info("Is Spanish: " + isSpanish);
    }
}
