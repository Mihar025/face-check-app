package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.helperServices.CompanyPayrollService;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
        private final CompanyPayrollService companyPayrollService;


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

    @GetMapping("/download-w2/{workerId}")
    public ResponseEntity<byte[]> downloadW2FromS3(@PathVariable Integer workerId,
                                                   @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        String fileName = "w2-statements/" + year + "/" + "w2_" + workerId + ".pdf";

        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w2_statement_" + workerId + ".pdf")
                .body(fileDTO.data());
    }


    @GetMapping("/generate-w3/{companyId}")
    public ResponseEntity<byte[]> generateW3Pdf(@PathVariable Integer companyId,
                                                @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        var pdfContent = companyPayrollService.generatePdfW3(companyId, year);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w3_summary_" + companyId + ".pdf")
                .body(pdfContent);
    }

    @GetMapping("/download-w3/{companyId}")
    public ResponseEntity<byte[]> downloadW3FromS3(@PathVariable Integer companyId,
                                                   @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        String fileName = "w3-summary/" + year + "/" + "w3_" + companyId + ".pdf";
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=w3-summary_" + companyId + ".pdf")

                .body(fileDTO.data());
    }


    @GetMapping("/download/{payrollId}")
    public ResponseEntity<byte[]> downloadPayStub(@PathVariable Integer payrollId) {
        String fileName = "paystubs/" + payrollId + ".pdf";
        S3FileDTO fileDTO = amazonS3Service.downloadAttendancePhoto(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pay_stub_" + payrollId + ".pdf")
                .body(fileDTO.data());
    }



}
