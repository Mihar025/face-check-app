package com.zikpak.facecheck.services.amazonS3Service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.zikpak.facecheck.controllers.S3FileDTO;
import com.zikpak.facecheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3Service {

    private final AmazonS3 s3Client;
    private final UserRepository userRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadAttendancePhoto(String base64Photo,
                                        String email,
                                        String prefix) {
        if (base64Photo == null || base64Photo.isEmpty()) {
            return null;
        }

        try {
            // Декодируем Base64 в байты
            byte[] photoBytes = Base64.getDecoder().decode(base64Photo);

            // Генерируем имя файла для фотографии посещения
            String fileName = generateAttendancePhotoName(email, prefix);

            // Создаем метаданные
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setContentLength(photoBytes.length);

            // Загружаем файл в S3
            s3Client.putObject(
                    bucketName,
                    fileName,
                    new ByteArrayInputStream(photoBytes),
                    metadata
            );

            // Возвращаем URL файла
            return s3Client.getUrl(bucketName, fileName).toString();

        } catch (Exception e) {
            log.error("Failed to upload attendance photo", e);
            throw new RuntimeException("Failed to upload attendance photo", e);
        }
    }


    public String uploadPhotoForProfile(MultipartFile base64Photo,
                                        String email,
                                        String prefix) {
        var foundedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with provided email not found"));
        if (base64Photo == null || base64Photo.isEmpty()) {
            return null;
        }

        try {
            byte[] photoBytes = base64Photo.getBytes();

            // Генерируем имя файла для фотографии посещения
            String fileName = generatePhoto(email, prefix);

            // Создаем метаданные
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(base64Photo.getContentType());
            metadata.setContentLength(base64Photo.getSize());


            // Загружаем файл в S3
            s3Client.putObject(
                    bucketName,
                    fileName,
                    new ByteArrayInputStream(photoBytes),
                    metadata
            );



            var savedUrl = s3Client.getUrl(bucketName, fileName).toString();

            foundedUser.setPhotoFileName(fileName);
            foundedUser.setPhotoUrl(savedUrl);
            var savedUser = userRepository.save(foundedUser);
            // Возвращаем URL файла
            return savedUser.getPhotoUrl();

        } catch (Exception e) {
            log.error("Failed to upload attendance photo", e);
            throw new RuntimeException("Failed to upload attendance photo", e);
        }
    }


    public S3FileDTO downloadAttendancePhoto(String fileName) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            ObjectMetadata metadata = s3Object.getObjectMetadata();
            byte[] data = IOUtils.toByteArray(s3Object.getObjectContent());

            String contentType = metadata.getContentType();
            if (contentType == null) {
                contentType = determineContentType(fileName);
            }

            return new S3FileDTO(data, contentType);
        } catch (IOException e) {
            log.error("Failed to download attendance photo", e);
            throw new RuntimeException("Failed to download attendance photo", e);
        }
    }

    public void deleteAttendancePhoto(String fileName) {
        try {
            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            log.error("Failed to delete attendance photo", e);
            throw new RuntimeException("Failed to delete attendance photo", e);
        }
    }

    private String generateAttendancePhotoName(String userEmail, String prefix) {
        return String.format("%s-%s-%s.jpg",
                prefix,
                userEmail,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        );
    }

    private String generatePhoto(String userEmail, String prefix) {
        return String.format("%s_%s_%s.jpg",
                prefix,
                userEmail,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        );
    }

    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "webp" -> "image/webp";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }


}