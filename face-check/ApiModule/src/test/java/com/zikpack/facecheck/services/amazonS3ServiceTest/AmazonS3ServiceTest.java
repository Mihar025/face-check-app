package com.zikpack.facecheck.services.amazonS3ServiceTest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.metrics.MetricsAmazonS3Service;
import com.zikpak.facecheck.metrics.MetricsAuthenticationService;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.S3FileDTO;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import java.util.Base64;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmazonS3ServiceTest {
    @Mock AmazonS3 s3Client;
    @Mock UserRepository userRepository;
    @Mock MetricsAmazonS3Service metricsAmazonS3Service;
    @InjectMocks
    AmazonS3Service amazonS3Service;
    @Mock Timer.Sample timerSample;
    private final String bucket2 = "my-bucket";
    private final String bucket = "test-bucket";
    private final String email = "user@example.com";
    private final String prefix = "profile";
    private final String fileName = "attendance/photo123.jpg";
    private final String fileName2 = "docs/test.pdf";

    private final byte[] pdfContent = new byte[]{0x25, 0x50, 0x44, 0x46}; // "%PDF"
    private byte[] payload;


    @BeforeEach
    void setUp() {
        payload = new byte[]{10, 20, 30, 40};

        lenient().when(metricsAmazonS3Service.startTimer()).thenReturn(timerSample);

        ReflectionTestUtils.setField(amazonS3Service, "bucketName", "test-bucket");
    }

    @Test
    void uploadPdfToS3_success() throws MalformedURLException {
        // — GIVEN —
        URL expectedUrl = new URL("https://" + bucket + ".s3.amazonaws.com/" + fileName2);
        when(s3Client.getUrl(bucket, fileName2)).thenReturn(expectedUrl);

        // — WHEN —
        String result = amazonS3Service.uploadPdfToS3(pdfContent, fileName2);

        // — THEN —
        // 1) Проверяем, что в S3 положили поток с правильным метаданными
        verify(s3Client).putObject(
                eq(bucket),
                eq(fileName2),
                any(ByteArrayInputStream.class),
                argThat(metadata ->
                        "application/pdf".equals(metadata.getContentType()) &&
                                metadata.getContentLength() == pdfContent.length
                )
        );

        // 2) Проверяем, что метрика за успех сработала
        verify(metricsAmazonS3Service).recordOperationTime(timerSample, "upload_pdf_successfully");

        // 3) Метод вернул строку из getUrl()
        assertEquals(expectedUrl.toString(), result);
    }

    @Test
    void downloadAttendancePhoto_success_withContentTypeFromMetadata() throws Exception {
        // — GIVEN —
        S3Object s3object = mock(S3Object.class);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");

        // теперь payload != null
        S3ObjectInputStream contentStream =
                new S3ObjectInputStream(new ByteArrayInputStream(payload), null);

        // мокать именно тот бакет, который в сервисе
        when(s3Client.getObject(bucket, fileName)).thenReturn(s3object);
        when(s3object.getObjectMetadata()).thenReturn(metadata);
        when(s3object.getObjectContent()).thenReturn(contentStream);

        // — WHEN —
        S3FileDTO dto = amazonS3Service.downloadAttendancePhoto(fileName);

        // — THEN —
        assertArrayEquals(payload, dto.data());
        assertEquals("image/jpeg", dto.contentType());
        verify(s3Client).getObject(bucket, fileName);
        verify(metricsAmazonS3Service)
                .recordOperationTime(timerSample, "download_photo_failed");
    }

    @Test
    void deleteAttendancePhoto_success() {
        // — WHEN —
        amazonS3Service.deleteAttendancePhoto(fileName);

        // — THEN —
        // 1) Убедились, что S3-клиент удалил нужный объект
        verify(s3Client).deleteObject(bucket, fileName);

        // 2) Метрика за успешное удаление
        verify(metricsAmazonS3Service)
                .recordOperationTime(timerSample, "delete_photo_successfully");
    }




    @Test
    void uploadAttendancePhoto_success() throws Exception {

        byte[] photoBytes = new byte[]{1,2,3};
        String base64 = Base64.getEncoder().encodeToString(photoBytes);

        AmazonS3Service spyService = spy(amazonS3Service);
        doReturn("user-photo-123.jpg")
                .when(spyService).generateAttendancePhotoName("user@example.com", "prefix");

        when(s3Client.getUrl("test-bucket", "user-photo-123.jpg"))
                .thenReturn(new URL("https://test-bucket.s3.amazonaws.com/user-photo-123.jpg"));

        String resultUrl = spyService.uploadAttendancePhoto(base64, "user@example.com", "prefix");

        assertEquals(
                "https://test-bucket.s3.amazonaws.com/user-photo-123.jpg",
                resultUrl,
                "Метод должен вернуть URL из s3Client.getUrl(...)"
        );

        verify(s3Client).putObject(
                eq("test-bucket"),
                eq("user-photo-123.jpg"),
                any(ByteArrayInputStream.class),
                any(ObjectMetadata.class)
        );

        verify(metricsAmazonS3Service)
                .recordOperationTime(timerSample, "upload_photo_successfully");
    }

    @Test
    void uploadPhotoForProfile_success() throws Exception {

        User existing = new User();
        existing.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existing));

        byte[] bytes = new byte[]{10, 20, 30};
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                bytes
        );

        AmazonS3Service spySvc = spy(amazonS3Service);
        doReturn("user-profile-xyz.png")
                .when(spySvc).generatePhoto(email, prefix);

        String expectedUrl = "https://" + bucket + ".s3.amazonaws.com/user-profile-xyz.png";
        when(s3Client.getUrl(bucket, "user-profile-xyz.png"))
                .thenReturn(new URL(expectedUrl));

        User saved = new User();
        saved.setEmail(email);
        saved.setPhotoFileName("user-profile-xyz.png");
        saved.setPhotoUrl(expectedUrl);
        when(userRepository.save(existing)).thenReturn(saved);

        // --- ACT ---
        String result = spySvc.uploadPhotoForProfile(file, email, prefix);

        // --- ASSERT ---
        assertEquals(expectedUrl, result, "Должен вернуть URL, полученный из S3");

        verify(s3Client).putObject(
                eq(bucket),
                eq("user-profile-xyz.png"),
                any(ByteArrayInputStream.class),
                argThat((ObjectMetadata md) ->
                        "image/png".equals(md.getContentType()) &&
                                md.getContentLength() == bytes.length
                )
        );

        verify(userRepository).save(argThat(user ->
                "user-profile-xyz.png".equals(user.getPhotoFileName()) &&
                        expectedUrl.equals(user.getPhotoUrl())
        ));

        verify(metricsAmazonS3Service)
                .recordOperationTime(timerSample, "upload_photo_profile_successfully");
    }

}
