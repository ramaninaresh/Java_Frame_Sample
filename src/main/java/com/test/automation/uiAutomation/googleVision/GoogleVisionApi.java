package com.test.automation.uiAutomation.googleVision;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GoogleVisionApi {

    private static final Logger log = Logger.getLogger(GoogleVisionApi.class);
    private ImageAnnotatorClient visionClient;
    String credentialsPath;

    // Constructor to initialize with credentials path
    public GoogleVisionApi(String credentialsPath) throws Exception {
        this.credentialsPath=credentialsPath;
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));

        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
            visionClient = ImageAnnotatorClient.create(settings);
        } catch (IOException e) {
            System.out.println("Error initializing ImageAnnotatorClient: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Method for text detection (as already implemented)
    public String detectTextFromImage(String imagePath) {
        try {
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);
            byte[] data = Files.readAllBytes(Paths.get(imagePath));
            ByteString imgBytes = ByteString.copyFrom(data);

            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            StringBuilder detectedText = new StringBuilder();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return null;
                }
                res.getTextAnnotationsList().forEach(annotation -> detectedText.append(annotation.getDescription()).append(" "));
            }
            return detectedText.toString().trim();

        } catch (Exception e) {
            System.out.println("Error detecting text: " + e.getMessage());
            e.printStackTrace();  // This will print the full stack trace for better debugging
            return null;
        }
    }

    public Boolean isElementImagePresentOCR(String elementImage, String screenImage) throws IOException {
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);
        log.info("Starting process to find element text within screen image.");

        // Load the element and screen images
        ByteString elementBytes = ByteString.readFrom(new FileInputStream(elementImage));
        Image elementImg = Image.newBuilder().setContent(elementBytes).build();

        ByteString screenBytes = ByteString.readFrom(new FileInputStream(screenImage));
        Image screenImg = Image.newBuilder().setContent(screenBytes).build();

        // Create request for element image (for text detection)
        AnnotateImageRequest elementRequest = AnnotateImageRequest.newBuilder()
                .addFeatures(Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION))
                .setImage(elementImg)
                .build();

        // Create request for screen image (for text detection)
        AnnotateImageRequest screenRequest = AnnotateImageRequest.newBuilder()
                .addFeatures(Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION))
                .setImage(screenImg)
                .build();

        // Send the requests and retrieve responses
        List<AnnotateImageRequest> elementRequests = new ArrayList<>();
        elementRequests.add(elementRequest);
        AnnotateImageResponse elementResponse = visionClient.batchAnnotateImages(elementRequests).getResponses(0);

        List<AnnotateImageRequest> screenRequests = new ArrayList<>();
        screenRequests.add(screenRequest);
        AnnotateImageResponse screenResponse = visionClient.batchAnnotateImages(screenRequests).getResponses(0);

        // Check for errors in the responses
        if (elementResponse.hasError()) {
            log.error("Error in element image response: " + elementResponse.getError().getMessage());
            return Boolean.FALSE; // Return Boolean.FALSE to be consistent
        }
        if (screenResponse.hasError()) {
            log.error("Error in screen image response: " + screenResponse.getError().getMessage());
            return Boolean.FALSE; // Return Boolean.FALSE to be consistent
        }

        log.info("Processing only Element image for text detection.");
        String elementText = "";

        // Get the detected text in the element image
        if (elementResponse.getTextAnnotationsList().size() > 0) {
            elementText = elementResponse.getTextAnnotationsList().get(0).getDescription();
            log.info("Detected text in element image: " + elementText);
        } else {
            log.warn("No text detected in the element image.");
            return Boolean.FALSE; // Return Boolean.FALSE if no text is found
        }

        log.info("Processing Full Screen image for text detection.");
        String screenText = "";

        // Get the detected text in the screen image
        if (screenResponse.getTextAnnotationsList().size() > 0) {
            screenText = screenResponse.getTextAnnotationsList().get(0).getDescription();
            log.info("Detected text in screen image: " + screenText);
        } else {
            log.warn("No text detected in the screen image.");
            return Boolean.FALSE; // Return Boolean.FALSE if no text is found
        }

        // Now, check if the element text is present in the screen text
        if (screenText.contains(elementText)) {
            log.info("Element text found within the screen image.");
            return Boolean.TRUE; // Return Boolean.TRUE if found
        } else {
            log.warn("Element text not found in the screen image.");
            return Boolean.FALSE; // Return Boolean.FALSE if not found
        }
    }


    public void close() {
        if (visionClient != null) {
            visionClient.close();
        }
    }
}
