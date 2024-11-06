package com.test.automation.uiAutomation.googleVision;

public class QuickstartSample {

    public static void main(String... args) {
        // Path to the Google Cloud credentials JSON file
        String credentialsPath = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/googleVision/googleconfig.json";
        String imageFileName = System.getProperty("user.dir") + "/src/main/java/com/test/automation/uiAutomation/resources/wakeupcat.png";

        GoogleVisionApi visionAPI = null;
        try {
            // Initialize the Google Vision API with credentials path
            visionAPI = new GoogleVisionApi(credentialsPath);

            // Perform text detection on the image
            String detectedText = visionAPI.detectTextFromImage(imageFileName);

            // Display results
            if (detectedText != null && !detectedText.isEmpty()) {
                System.out.println("Detected Text: " + detectedText);
            } else {
                System.out.println("No text detected in the image.");
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        } finally {
            if (visionAPI != null) {
                visionAPI.close();
            }
        }
    }
}
