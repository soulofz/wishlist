package com.wishlist.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.io.IOException;

@Service
@Slf4j
public class CloudImageService {

    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @PostConstruct
    public void init() {
        log.info("Cloudinary Cloud Name: " + cloudName);
        log.info("API Key: " + apiKey);

        if (cloudName == null || cloudName.isEmpty() ||
                apiKey == null || apiKey.isEmpty() ||
                apiSecret == null || apiSecret.isEmpty()) {
            log.error("Cloudinary configuration is incomplete. Please check your API credentials.");
            throw new IllegalStateException("Cloudinary configuration is incomplete. Please check your API credentials.");
        }

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));

        log.info("Cloudinary initialized successfully");
    }


    public CloudImageUploadResult uploadImage(MultipartFile file, String folder) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null.");
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", folder));

        return new CloudImageUploadResult(
                (String) uploadResult.get("secure_url"),
                (String) uploadResult.get("public_id"));
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from Cloudinary", e);
        }
    }

    public record CloudImageUploadResult(
            String imageUrl,
            String publicId
    ) {
    }

}

