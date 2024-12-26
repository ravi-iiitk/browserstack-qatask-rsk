package com.browserstack.qa.task.unittests;

import java.net.HttpURLConnection;
import java.net.URL;

public class VerifyBrowserStackHub {
    public static void main(String[] args) {
        String username = "ravishankar_MVw4eS";
        String accessKey = "6p1zz1NL11ceghFKqwwD";
        String hubUrl = String.format("https://ravishankar_MVw4eS:6p1zz1NL11ceghFKqwwD@hub.browserstack.com/wd/hub/status", username, accessKey);

        try {
            URL url = new URL(hubUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Hub URL is reachable. BrowserStack is up and running.");
            } else {
                System.out.println("Failed to connect to BrowserStack Hub. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while checking BrowserStack Hub URL: " + e.getMessage());
        }
    }
}