package com.zikpak.facecheck.authController;

import com.zikpak.facecheck.authRequests.*;
import com.zikpak.facecheck.requestsResponses.settings.*;
import com.zikpak.facecheck.security.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ClassFileFormatVersion;
import java.lang.reflect.Method;


@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Tag(name = "Authentication")
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    //working
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
            authenticationService.register(request);
            return ResponseEntity.accepted().build();
        }


    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid RegistrationAdminRequest request) throws MessagingException {
        authenticationService.registerAdmin(request);
        return ResponseEntity.accepted().build();
    }

    //working
    @PostMapping("/register/foreman")
    public ResponseEntity<?> registerForeman(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        authenticationService.registerForeman(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/register/company")
    public ResponseEntity<Void> registerCompany(
            @RequestBody CompanyRegistrationRequest request,
            Authentication authentication
    ) throws MessagingException {
        authenticationService.registerCompany(request, authentication);
        return ResponseEntity.ok().build();
    }

    //working
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // Неверные учетные данные
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        } catch (Exception e) {
            // Логируем неожиданную ошибку
            log.error("Authentication error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication failed: " + e.getMessage());
        }
    }
    //working
    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activateAccount(token);
    }

    //Working
    @PostMapping("/employees/{employeeId}/payment")
    public ResponseEntity<Void> setPaymentData(
            @PathVariable Integer employeeId,
            @RequestBody PaymentRequest paymentRequest,
            Authentication authentication
    ) {
        authenticationService.setPaymentDataForWorkerHoursRateAndOvertime(employeeId, paymentRequest, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/email")
    public ResponseEntity<SettingsEmailResponse> updateEmail(
            Authentication authentication,
            @Valid @RequestBody SettingsEmailRequest request
    ) {
        var response = authenticationService.updateEmailInSettings(authentication, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(
            Authentication authentication,
            @Valid @RequestBody SettingsPasswordRequest request
    ) {
        authenticationService.updatePasswordInSettings(authentication, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/phone")
    public ResponseEntity<SettingsPhoneNumberResponse> updatePhoneNumber(
            Authentication authentication,
            @Valid @RequestBody SettingsPhoneNumberRequest request
    ) {
        var response = authenticationService.updatePhoneNumber(authentication, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/email")
    public ResponseEntity<Void> sendResetCode(@RequestBody @Valid EmailRequest request)
            throws MessagingException {
        authenticationService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<Void> verifyCode(@RequestBody @Valid VerifyCodeRequest request) {
        authenticationService.verifyPasswordResetCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.resetPassword(forgotPasswordRequest);
        return ResponseEntity.ok().build();
    }

}
