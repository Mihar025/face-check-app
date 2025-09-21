package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.services.termsOfUsageService.TermsOfUsageService;
import com.zikpak.facecheck.services.termsOfUsageService.TermsOfUseRequestDataRequest;
import com.zikpak.facecheck.services.termsOfUsageService.TermsOfUseRequestDataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("privacy-and-terms")
public class PrivacyAndTermsController {
    
    private final TermsOfUsageService termsOfUsageService;



    @PostMapping
    public ResponseEntity<TermsOfUseRequestDataResponse> acceptTerms(
            @RequestBody @Valid TermsOfUseRequestDataRequest request
    ) {
        TermsOfUseRequestDataResponse response = termsOfUsageService.acceptTermsOfUseFromWorkerAt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    public TermsOfUseRequestDataResponse getById(@PathVariable Integer id) {
        return termsOfUsageService.findById(id);
    }


    @GetMapping
    public PageResponse<TermsOfUseRequestDataResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return termsOfUsageService.findAllTermsOfUse(page, size);
    }

}
