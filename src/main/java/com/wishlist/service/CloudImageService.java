package com.wishlist.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import java.io.IOException;

@Service
public class CloudImageService {

    private Cloudinary cloudinary;
    private final RestTemplate restTemplate;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    public CloudImageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {

        if (cloudName == null || cloudName.isEmpty()) {
            throw new IllegalStateException("Cloud name is not configured");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API key is not configured");
        }
        if (apiSecret == null || apiSecret.isEmpty()) {
            throw new IllegalStateException("API secret is not configured");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null.");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB.");
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", folder));

        String imageUrl = (String) uploadResult.get("secure_url");
        return imageUrl;
    }

    public void deleteImage(String imageUrl) {

        String publicId = extractPublicIdFromUrl(imageUrl);

        String deleteUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/destroy";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("public_id", publicId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(deleteUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("result") && responseBody.get("result").equals("ok")) {
                System.out.println("Image deleted successfully from Cloudinary");
            } else {
                System.out.println("Failed to delete image from Cloudinary");
            }
        } else {
            throw new RuntimeException("Error deleting image from Cloudinary: " + response.getStatusCode());
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        String[] urlParts = imageUrl.split("/");
        return urlParts[urlParts.length - 1].split("\\.")[0];
    }

    @PostConstruct
    public void init() {
        System.out.println("Cloudinary Cloud Name: " + cloudName);
        System.out.println("API Key: " + apiKey);


        if (cloudName == null || cloudName.isEmpty() ||
                apiKey == null || apiKey.isEmpty() ||
                apiSecret == null || apiSecret.isEmpty()) {
            throw new IllegalStateException("Cloudinary configuration is incomplete. Please check your API credentials.");
        }


        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));

        System.out.println("Cloudinary initialized successfully");
    }

}

