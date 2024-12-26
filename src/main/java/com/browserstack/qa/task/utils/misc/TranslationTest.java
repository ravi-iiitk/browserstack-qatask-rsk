package com.browserstack.qa.task.utils.misc;

import static com.browserstack.qa.task.utils.misc.TranslationUtils.isSpanishText;

public class TranslationTest {
    public static void main(String[] args) {
        String testText = "Hola, ¿cómo estás?";
        boolean isSpanish = isSpanishText(testText);
        System.out.println("Is Spanish: " + isSpanish);
    }
}
