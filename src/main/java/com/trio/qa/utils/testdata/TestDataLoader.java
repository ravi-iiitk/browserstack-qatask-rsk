package com.trio.qa.utils.testdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trio.qa.config.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestDataLoader {

    private static final String BASE_PATH = "src/test/resources/test-data/";
    private static final String PLATFORM = ConfigReader.get("platform"); // Example: web
    private static final String APPLICATION = ConfigReader.get("application.name"); // Example: facebook

    /**
     * Dynamically constructs the folder path for test data.
     *
     * @param moduleName    Module name (e.g., auth, profile).
     * @param subModuleName Submodule name (e.g., login).
     * @return The full folder path for the test data.
     */
    private static String getFolderPath(String moduleName, String subModuleName) {
        return BASE_PATH + PLATFORM + File.separator + APPLICATION + File.separator + moduleName + File.separator + subModuleName;
    }

    /**
     * Reads the entire JSON file for a module and submodule.
     *
     * @param moduleName    Module name (e.g., auth).
     * @param subModuleName Submodule name (e.g., login).
     * @return A Map representing the JSON content.
     */
    public static Map<String, List<Map<String, Object>>> readTestData(String moduleName, String subModuleName) {
        String folderPath = getFolderPath(moduleName, subModuleName);
        String filePath = folderPath + ".json";
        ObjectMapper mapper = new ObjectMapper();

        // Debug log to verify the constructed file path
        System.out.println("Loading test data from: " + filePath);

        try {
            return mapper.readValue(new File(filePath), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test data from: " + filePath, e);
        }
    }

    /**
     * Fetches all scenarios for a specific type (e.g., positive, negative).
     *
     * @param moduleName    Module name (e.g., auth).
     * @param subModuleName Submodule name (e.g., login).
     * @param type          Type of scenario (positive or negative).
     * @return A List of Maps representing all scenarios for the given type.
     */
    public static List<Map<String, Object>> readAllScenarios(String moduleName, String subModuleName, String type) {
        Map<String, List<Map<String, Object>>> testData = readTestData(moduleName, subModuleName);

        if (!testData.containsKey(type)) {
            throw new RuntimeException("Invalid scenario type: " + type);
        }

        return testData.get(type);
    }

    /**
     * Fetches specific test data by scenario or group (e.g., positive[1], negative[3]).
     *
     * @param moduleName    Module name (e.g., auth).
     * @param subModuleName Submodule name (e.g., login).
     * @param reference     Data reference (e.g., positive[1], all_positive, negative[3]).
     * @return A List of Maps representing the requested test data.
     */
    public static List<Map<String, Object>> getTestData(String moduleName, String subModuleName, String reference) {
        Map<String, List<Map<String, Object>>> testData = readTestData(moduleName, subModuleName);

        if (reference.startsWith("all_positive")) {
            return readAllScenarios(moduleName, subModuleName, "positive");
        } else if (reference.startsWith("all_negative")) {
            return readAllScenarios(moduleName, subModuleName, "negative");
        } else if (reference.startsWith("positive[") || reference.startsWith("negative[")) {
            String[] parts = reference.replace("]", "").split("\\[");
            String type = parts[0]; // positive or negative
            int count = Integer.parseInt(parts[1]); // Number of scenarios to fetch

            return testData.get(type).subList(0, Math.min(count, testData.get(type).size()));
        } else {
            throw new RuntimeException("Invalid reference: " + reference);
        }
    }
}
