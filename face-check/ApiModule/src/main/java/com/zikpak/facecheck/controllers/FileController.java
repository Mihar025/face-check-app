package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

        private final AmazonS3Service amazonS3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("photo") String base64Photo,
            @RequestParam("email") String email,
            @RequestParam("prefix") String prefix
    ) {
        String fileUrl = amazonS3Service.uploadAttendancePhoto(base64Photo, email, prefix);
        return ResponseEntity.ok(fileUrl);
    }

    @PostMapping("/upload/photo")
    public ResponseEntity<String> uploadPhoto(
            @RequestParam("photo") MultipartFile base64Photo,
            @RequestParam("email") String email,
            @RequestParam("prefix") String prefix
    ) {
        log.info("Received upload photo request. Email: {}, Prefix: {}", email, prefix);
        String fileUrl = amazonS3Service.uploadPhotoForProfile(base64Photo, email, prefix);
        log.info("File uploaded successfully. URL: {}", fileUrl);
        return ResponseEntity.ok(fileUrl);
    }


    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDTO.contentType()))
                .body(fileDTO.data());
    }

            @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
            amazonS3Service.deleteAttendancePhoto(fileName);
            return ResponseEntity.ok().build();
            }


}
