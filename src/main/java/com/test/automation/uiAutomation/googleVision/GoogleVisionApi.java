package com.test.automation.uiAutomation.googleVision;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GoogleVisionApi {

    private ImageAnnotatorClient visionClient;

    // Constructor to initialize with credentials path
    public GoogleVisionApi(String credentialsPath) throws Exception {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        visionClient = ImageAnnotatorClient.create(settings);
    }

    // Method for text detection (as already implemented)
    public String detectTextFromImage(String imagePath) {
        try {
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
            return null;
        }
    }

    // New method for label detection
    public List<EntityAnnotation> detectLabels(String imagePath) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(imagePath));
        ByteString imgBytes = ByteString.copyFrom(data);

        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(requests);
        return response.getResponsesList().get(0).getLabelAnnotationsList();
    }

    public void close() {
        if (visionClient != null) {
            visionClient.close();
        }
    }
}
